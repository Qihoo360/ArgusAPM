package com.argusapm.gradle.internal.ajc

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.argusapm.gradle.internal.PluginConfig
import com.argusapm.gradle.internal.cutter.InputSourceFileStatus
import com.argusapm.gradle.internal.cutter.getAspectDir
import com.argusapm.gradle.internal.cutter.getExcludeFileDir
import com.argusapm.gradle.internal.cutter.getIncludeFileDir
import com.argusapm.gradle.internal.concurrent.ThreadPool
import com.argusapm.gradle.internal.utils.filterJar
import com.argusapm.gradle.internal.utils.log
import com.argusapm.gradle.internal.utils.logCore
import org.apache.commons.io.FileUtils
import java.io.File

val contentTypes = mutableSetOf(QualifiedContent.DefaultContentType.CLASSES)
val scopes = mutableSetOf(QualifiedContent.Scope.EXTERNAL_LIBRARIES)

class AjcWeaverManager(private val transformInvocation: TransformInvocation, private val inputSourceFileStatus: InputSourceFileStatus) {
    private val threadPool = ThreadPool()
    private val aspectPath = arrayListOf<File>()
    private val classPath = arrayListOf<File>()

    fun weaver() {

        System.setProperty("aspectj.multithreaded", "true")

        if (transformInvocation.isIncremental) {
            createIncrementalTask()
        } else {
            createTask()
        }
        log("AjcWeaverList.size is ${threadPool.taskList.size}")

        aspectPath.add(getAspectDir())
        classPath.add(getIncludeFileDir())
        classPath.add(getExcludeFileDir())

        threadPool.taskList.forEach { ajcWeaver ->
            ajcWeaver as AjcWeaver
            ajcWeaver.encoding = PluginConfig.encoding
            ajcWeaver.aspectPath = aspectPath
            ajcWeaver.classPath = classPath
            ajcWeaver.targetCompatibility = PluginConfig.targetCompatibility
            ajcWeaver.sourceCompatibility = PluginConfig.sourceCompatibility
            ajcWeaver.bootClassPath = PluginConfig.bootClassPath
            ajcWeaver.ajcArgs = PluginConfig.argusApmConfig().ajcArgs
        }
        threadPool.startWork()
    }

    private fun createTask() {

        val ajcWeaver = AjcWeaver()
        val includeJar = transformInvocation.outputProvider.getContentLocation("include", contentTypes as Set<QualifiedContent.ContentType>, scopes, Format.JAR)
        if (!includeJar.parentFile.exists()) {
            FileUtils.forceMkdir(includeJar.parentFile)
        }
        FileUtils.deleteQuietly(includeJar)
        ajcWeaver.outputJar = includeJar.absolutePath
        ajcWeaver.inPath.add(getIncludeFileDir())
        addAjcWeaver(ajcWeaver)

        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                classPath.add(jarInput.file)
                //如果该Jar参与AJC织入的话，则进行下面操作
                if (filterJar(jarInput, PluginConfig.argusApmConfig().includes, PluginConfig.argusApmConfig().excludes, PluginConfig.argusApmConfig().excludeJars)) {
                    val tempAjcWeaver = AjcWeaver()
                    tempAjcWeaver.inPath.add(jarInput.file)

                    val outputJar = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes,
                            jarInput.scopes, Format.JAR)
                    if (!outputJar.parentFile?.exists()!!) {
                        outputJar.parentFile?.mkdirs()
                    }

                    tempAjcWeaver.outputJar = outputJar.absolutePath
                    addAjcWeaver(tempAjcWeaver)
                }

            }
        }

    }

    private fun createIncrementalTask() {
        //如果AJ或者Include文件有一个变化的话,则重新织入
        if (inputSourceFileStatus.isAspectChanged || inputSourceFileStatus.isIncludeFileChanged) {
            val ajcWeaver = AjcWeaver()
            val outputJar = transformInvocation.outputProvider?.getContentLocation("include", contentTypes as Set<QualifiedContent.ContentType>, scopes, Format.JAR)
            FileUtils.deleteQuietly(outputJar)

            ajcWeaver.outputJar = outputJar?.absolutePath
            ajcWeaver.inPath.add(getIncludeFileDir())
            addAjcWeaver(ajcWeaver)

            logCore("createIncrementalTask isAspectChanged: [ ${inputSourceFileStatus.isAspectChanged} ]    isIncludeFileChanged:  [ ${inputSourceFileStatus.isIncludeFileChanged} ]")
        }


        transformInvocation.inputs?.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                classPath.add(jarInput.file)
                val outputJar = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                if (!outputJar.parentFile?.exists()!!) {
                    outputJar.parentFile?.mkdirs()
                }

                if (filterJar(jarInput, PluginConfig.argusApmConfig().includes, PluginConfig.argusApmConfig().excludes, PluginConfig.argusApmConfig().excludeJars)) {
                    if (inputSourceFileStatus.isAspectChanged) {
                        FileUtils.deleteQuietly(outputJar)

                        val tempAjcWeaver = AjcWeaver()
                        tempAjcWeaver.inPath.add(jarInput.file)
                        tempAjcWeaver.outputJar = outputJar.absolutePath
                        addAjcWeaver(tempAjcWeaver)

                        logCore("jar inputSourceFileStatus.isAspectChanged true")
                    } else {
                        if (!outputJar.exists()) {
                            val tempAjcWeaver = AjcWeaver()
                            tempAjcWeaver.inPath.add(jarInput.file)
                            tempAjcWeaver.outputJar = outputJar.absolutePath
                            addAjcWeaver(tempAjcWeaver)
                            logCore("jar inputSourceFileStatus.isAspectChanged false && outputJar.exists() is false")
                        }
                    }
                }
            }
        }
    }

    private fun addAjcWeaver(ajcWeaver: AjcWeaver) {
        threadPool.addTask(ajcWeaver)
    }
}