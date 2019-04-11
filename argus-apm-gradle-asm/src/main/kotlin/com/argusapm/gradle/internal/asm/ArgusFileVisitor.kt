package com.argusapm.gradle.internal.asm

import com.android.utils.FileUtils
import com.argusapm.gradle.internal.PluginConfig
import com.argusapm.gradle.internal.asm.bytecode.func.FuncClassAdapter
import com.argusapm.gradle.internal.bytecode.func.NetClassAdapter
import com.argusapm.gradle.internal.bytecode.func.OkHttp3ClassAdapter
import com.argusapm.gradle.internal.utils.log
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.InputStream
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class ArgusFileVisitor(private val classLoader: ClassLoader, val input: Path, val output: Path) : SimpleFileVisitor<Path>() {
    override fun visitFile(inputPath: Path, attrs: BasicFileAttributes?): FileVisitResult {
        val outputPath = output.resolve(input.relativize(inputPath))
        directRun(inputPath, outputPath)
        return FileVisitResult.CONTINUE
    }

    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        val outputPath = output.resolve(input.relativize(dir))
        Files.createDirectories(outputPath)
        return FileVisitResult.CONTINUE

    }


    private fun directRun(input: Path, output: Path) {
        if (isMatchCondition(input.toString())) {
            val inputBytes = Files.readAllBytes(input)
            val outputBytes = weaveSingleClassToByteArray(inputBytes)
            Files.write(output, outputBytes)
        } else {
            Files.copy(input, output)
        }
    }

    private fun weaveSingleClassToByteArray(inputStream: ByteArray): ByteArray {
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

        classReader.accept(classWriterWrapper, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    private fun isMatchCondition(name: String): Boolean {
        return name.endsWith(".class") && !name.contains("R$")
                && !name.contains("R.class") && !name.contains("BuildConfig.class")
    }


}