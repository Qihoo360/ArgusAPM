package com.argusapm.gradle.internal.asm

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.argusapm.gradle.internal.asm.bytecode.ClassRelationshipMapVisitor
import com.argusapm.gradle.internal.concurrent.ITask
import com.argusapm.gradle.internal.concurrent.ThreadPool
import com.argusapm.gradle.internal.utils.eachFileRecurse
import com.argusapm.gradle.internal.utils.isWeavableClass
import com.google.common.io.ByteStreams
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.FileInputStream
import java.util.jar.JarFile

class ASMRelationshipMapParse {
    private val taskManager = ThreadPool()

    fun parseClassFileOfDirInput(dirInput: DirectoryInput) {
        dirInput.file.eachFileRecurse { file ->
            taskManager.addTask(object : ITask {
                override fun call(): Any? {
                    if (file.isWeavableClass()) {
                        val inputStream = FileInputStream(file)
                        val cr = ClassReader(inputStream)
                        val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                        val ca = ClassRelationshipMapVisitor(Opcodes.ASM4, cw)
                        cr.accept(ca, ClassReader.EXPAND_FRAMES)
                    }
                    return null
                }
            })
        }
    }

    fun parseJarFileOfJarInput(jarInput: JarInput) {
        taskManager.addTask(object : ITask {
            override fun call(): Any? {
                val jarFile = JarFile(jarInput.file)
                val entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    val jarEntry = entries.nextElement()
                    val entryName = jarEntry.name
                    if (isClassFile(entryName)) {
                        val inputStream = ByteStreams.toByteArray(jarFile.getInputStream(jarEntry))
                        val cr = ClassReader(inputStream)
                        val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                        val ca = ClassRelationshipMapVisitor(Opcodes.ASM4, cw)
                        cr.accept(ca, ClassReader.EXPAND_FRAMES)
                    }
                }

                jarFile.close()
                return null
            }
        })
    }

    private fun isClassFile(filePath: String): Boolean {
        return filePath.toLowerCase().endsWith(".class")
    }

    fun start() {
        taskManager.startWork()
    }
}