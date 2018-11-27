package com.argusapm.gradle.internal.cutter

import com.android.build.api.transform.*
import com.argusapm.gradle.internal.ajc.contentTypes
import com.argusapm.gradle.internal.ajc.scopes
import com.argusapm.gradle.internal.concurrent.ITask
import com.argusapm.gradle.internal.concurrent.ThreadPool
import com.argusapm.gradle.internal.utils.*
import org.apache.commons.io.FileUtils

/**
 * 输入源切割器，分别切割到：include、exclude、aspects三个目录下
 */
internal class InputSourceCutter(val transformInvocation: TransformInvocation, val fileFilter: FileFilter, val inputSourceFileStatus: InputSourceFileStatus) {
    private val taskManager = ThreadPool()

    init {
        if (transformInvocation.isIncremental) {
            LogStatus.isIncremental("true")
            LogStatus.cutStart()

            transformInvocation.inputs.forEach { input ->
                input.directoryInputs.forEach { dirInput ->
                    whenDirInputsChanged(dirInput)
                }

                input.jarInputs.forEach { jarInput ->
                    whenJarInputsChanged(jarInput)
                }
            }

            LogStatus.cutEnd()
        } else {
            LogStatus.isIncremental("false")
            LogStatus.cutStart()

            transformInvocation.outputProvider.deleteAll()

            transformInvocation.inputs.forEach { input ->
                input.directoryInputs.forEach { dirInput ->
                    cutDirInputs(dirInput)
                }

                input.jarInputs.forEach { jarInput ->
                    cutJarInputs(jarInput)
                }
            }
            LogStatus.cutEnd()
        }
    }


    private fun cutDirInputs(dirInput: DirectoryInput) {
        taskManager.addTask(object : ITask {
            override fun call(): Any? {
                dirInput.file.eachFileRecurse { file ->
                    //过滤出AJ文件
                    fileFilter.filterAJClassFromDir(dirInput, file)
                    //过滤出class文件
                    fileFilter.filterClassFromDir(dirInput, file)
                }

                //put exclude files into jar
                if (countOfFiles(getExcludeFileDir()) > 0) {
                    val excludeJar = transformInvocation.outputProvider.getContentLocation("exclude", contentTypes as Set<QualifiedContent.ContentType>, scopes, Format.JAR)
                    mergeJar(getExcludeFileDir(), excludeJar)
                }
                return null
            }
        })
    }


    private fun cutJarInputs(jarInput: JarInput) {
        taskManager.addTask(object : ITask {
            override fun call(): Any? {
                fileFilter.filterAJClassFromJar(jarInput)
                fileFilter.filterClassFromJar(transformInvocation, jarInput)
                return null
            }
        })
    }

    private fun whenDirInputsChanged(dirInput: DirectoryInput) {
        taskManager.addTask(object : ITask {
            override fun call(): Any? {
                dirInput.changedFiles.forEach { (file, status) ->
                    fileFilter.whenAJClassChangedOfDir(dirInput, file, status, inputSourceFileStatus)
                    fileFilter.whenClassChangedOfDir(dirInput, file, status, inputSourceFileStatus)
                }

                //如果include files 发生变化，则删除include输出jar
                if (inputSourceFileStatus.isIncludeFileChanged) {
                    logCore("whenDirInputsChanged include")
                    val includeOutputJar = transformInvocation.outputProvider.getContentLocation("include", contentTypes as Set<QualifiedContent.ContentType>, scopes, Format.JAR)
                    FileUtils.deleteQuietly(includeOutputJar)
                }

                //如果exclude files发生变化，则重新生成exclude jar到输出目录
                if (inputSourceFileStatus.isExcludeFileChanged) {
                    logCore("whenDirInputsChanged exclude")
                    val excludeOutputJar = transformInvocation.outputProvider.getContentLocation("exclude", contentTypes as Set<QualifiedContent.ContentType>?, scopes, Format.JAR)
                    FileUtils.deleteQuietly(excludeOutputJar)
                    mergeJar(getExcludeFileDir(), excludeOutputJar)
                }
                return null
            }
        })
    }

    private fun whenJarInputsChanged(jarInput: JarInput) {
        if (jarInput.status != Status.NOTCHANGED) {
            taskManager.addTask(object : ITask {
                override fun call(): Any? {
                    fileFilter.whenAJClassChangedOfJar(jarInput, inputSourceFileStatus)
                    fileFilter.whenClassChangedOfJar(transformInvocation, jarInput)
                    return null
                }
            })
        }
    }

    fun startCut() {
        taskManager.startWork()
    }
}