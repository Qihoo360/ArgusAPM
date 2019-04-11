package com.argusapm.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.argusapm.gradle.internal.asm.ASMWeaver
import com.argusapm.gradle.internal.utils.eachFileRecurse
import com.argusapm.gradle.internal.utils.getUniqueJarName
import com.google.common.collect.Sets
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File

internal class ArgusAPMTransform(private val project: Project) : Transform() {

    private lateinit var asmWeaver: ASMWeaver
    override fun getName(): String {
        return AppConstant.TRANSFORM_NAME
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return Sets.immutableEnumSet(QualifiedContent.DefaultContentType.CLASSES)!!
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation) {
        asmWeaver = ASMWeaver()

        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }

        transformInvocation.inputs.forEach { input ->
            input.directoryInputs.forEach { dirInput ->
                val dest = transformInvocation.outputProvider.getContentLocation(dirInput.name,
                        dirInput.contentTypes, dirInput.scopes,
                        Format.DIRECTORY)
                FileUtils.forceMkdir(dest)
                if (transformInvocation.isIncremental) {
                    val srcDirPath = dirInput.file.absolutePath
                    val destDirPath = dest.absolutePath
                    dirInput.changedFiles.forEach { (file, status) ->
                        val destFilePath = file.absolutePath.replace(srcDirPath, destDirPath)
                        val destFile = File(destFilePath)
                        when (status) {
                            Status.REMOVED -> {
                                FileUtils.deleteQuietly(destFile)
                            }
                            Status.CHANGED -> {
                                FileUtils.deleteQuietly(destFile)
                                asmWeaver.weaveClass(file, destFile)
                            }
                            Status.ADDED -> {
                                asmWeaver.weaveClass(file, destFile)
                            }
                            else -> {
                            }
                        }
                    }
                } else {
                    dirInput.file.eachFileRecurse { file ->
                        asmWeaver.weaveClass(file, File(file.absolutePath.replace(dirInput.file.absolutePath, dest.absolutePath)))
                    }
                }

            }

            input.jarInputs.forEach { jarInput ->
                val dest = transformInvocation.outputProvider.getContentLocation(
                        jarInput.file.getUniqueJarName(),
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR)
                if (transformInvocation.isIncremental) {
                    val status = jarInput.status
                    when (status) {
                        Status.REMOVED -> {
                            FileUtils.deleteQuietly(dest)
                        }
                        Status.CHANGED -> {
                            FileUtils.deleteQuietly(dest)
                            asmWeaver.weaveJar(jarInput.file, dest)
                        }
                        Status.ADDED -> {
                            asmWeaver.weaveJar(jarInput.file, dest)
                        }
                        else -> {
                        }
                    }
                } else {
                    asmWeaver.weaveJar(jarInput.file, dest)
                }
            }
        }

        asmWeaver.start()
    }
}