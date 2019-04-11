package com.argusapm.gradle.internal.asm

import com.argusapm.gradle.internal.PluginConfig
import com.argusapm.gradle.internal.asm.bytecode.func.FuncClassAdapter
import com.argusapm.gradle.internal.bytecode.func.NetClassAdapter
import com.argusapm.gradle.internal.bytecode.func.OkHttp3ClassAdapter
import com.argusapm.gradle.internal.bytecode.func.WebClassAdapter
import com.argusapm.gradle.internal.concurrent.ITask
import com.argusapm.gradle.internal.concurrent.ThreadPool
import com.argusapm.gradle.internal.utils.TypeUtil
import com.argusapm.gradle.internal.utils.TypeUtil.Companion.isWeaveThisJar
import com.argusapm.gradle.internal.utils.ZipFileUtils
import com.argusapm.gradle.internal.utils.log
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.*
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


class ASMWeaver {
    private val taskManager = ThreadPool()
    fun weaveClass(inputFile: File, outputFile: File) {
        taskManager.addTask(object : ITask {
            override fun call(): Any? {
                FileUtils.touch(outputFile)
                val inputStream = FileInputStream(inputFile)
                val bytes = weaveSingleClassToByteArray(inputStream)
                val fos = FileOutputStream(outputFile)
                fos.write(bytes)
                fos.close()
                inputStream.close()
                return null
            }
        })
    }

    private fun weaveSingleClassToByteArray(inputStream: InputStream): ByteArray {
        val classReader = ClassReader(inputStream)
        val classWriter = ExtendClassWriter(ClassWriter.COMPUTE_MAXS)
        var classWriterWrapper: ClassVisitor = classWriter

        if (PluginConfig.argusApmConfig().funcEnabled) {
            classWriterWrapper = FuncClassAdapter(Opcodes.ASM4, classWriterWrapper)
        }

        if (PluginConfig.argusApmConfig().netEnabled) {
            classWriterWrapper = NetClassAdapter(Opcodes.ASM4, classWriterWrapper)
        }

        if (PluginConfig.argusApmConfig().okhttpEnabled) {
            classWriterWrapper = OkHttp3ClassAdapter(Opcodes.ASM4, classWriterWrapper)
        }

        if (PluginConfig.argusApmConfig().webviewEnabled) {
            classWriterWrapper = WebClassAdapter(Opcodes.ASM4, classWriterWrapper)
        }

        classReader.accept(classWriterWrapper, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }


    fun weaveJar(inputJar: File, outputJar: File) {
        taskManager.addTask(object : ITask {
            override fun call(): Any? {
                FileUtils.copyFile(inputJar, outputJar)
                if (isWeaveThisJar(inputJar.name)) {
                    weaveJarTask(inputJar, outputJar)
                }
                return null
            }
        })
    }

    private fun weaveJarTask(input: File, output: File) {
        var zipOutputStream: ZipOutputStream? = null
        var zipFile: ZipFile? = null
        try {
            zipOutputStream = ZipOutputStream(BufferedOutputStream(Files.newOutputStream(output.toPath())))
            zipFile = ZipFile(input)
            val enumeration = zipFile.entries()
            while (enumeration.hasMoreElements()) {
                val zipEntry = enumeration.nextElement()
                val zipEntryName = zipEntry.name
                if (TypeUtil.isMatchCondition(zipEntryName) && TypeUtil.isNeedWeave(zipEntryName)) {
                    val data = weaveSingleClassToByteArray(BufferedInputStream(zipFile.getInputStream(zipEntry)))
                    val byteArrayInputStream = ByteArrayInputStream(data)
                    val newZipEntry = ZipEntry(zipEntryName)
                    ZipFileUtils.addZipEntry(zipOutputStream, newZipEntry, byteArrayInputStream)
                } else {
                    val inputStream = zipFile.getInputStream(zipEntry)
                    val newZipEntry = ZipEntry(zipEntryName)
                    ZipFileUtils.addZipEntry(zipOutputStream, newZipEntry, inputStream)
                }
            }
        } catch (e: Exception) {
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.finish()
                    zipOutputStream.flush()
                    zipOutputStream.close()
                }
                zipFile?.close()
            } catch (e: Exception) {
                log("close stream err!")
            }
        }
    }

    fun start() {
        taskManager.startWork()
    }
}