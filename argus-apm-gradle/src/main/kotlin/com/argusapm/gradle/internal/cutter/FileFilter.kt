package com.argusapm.gradle.internal.cutter

import com.android.build.api.transform.*
import com.android.builder.model.AndroidProject
import com.google.common.io.ByteStreams
import com.argusapm.gradle.AppConstant
import com.argusapm.gradle.internal.PluginConfig
import com.argusapm.gradle.internal.utils.*
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File
import java.util.jar.JarFile

private lateinit var basePath: String
private lateinit var aspectPath: String
private lateinit var includeDirPath: String
private lateinit var excludeDirPath: String

class FileFilter(private val project: Project, private val variantName: String) {
    init {
        init()
    }

    private fun init() {
        basePath = project.buildDir.absolutePath + File.separator + AndroidProject.FD_INTERMEDIATES + "/${AppConstant.TRANSFORM_NAME}/" + variantName
        aspectPath = basePath + File.separator + "aspects"
        includeDirPath = basePath + File.separator + "include_dir"
        excludeDirPath = basePath + File.separator + "exclude_dir"
    }

    fun filterAJClassFromDir(dirInput: DirectoryInput, file: File) {
        if (isAspectClassFile(file)) {
            val path = file.absolutePath
            val subPath = path.substring(dirInput.file.absolutePath.length)
            val cacheFile = File(aspectPath + subPath)
            cache(file, cacheFile)
        }
    }

    fun filterClassFromDir(dirInput: DirectoryInput, file: File) {
        if (isClassFile(file)) {
            val path = file.absolutePath
            val subPath = path.substring(dirInput.file.absolutePath.length)
            val transPath = subPath.replace(File.separator, ".")

            val isInclude = isIncludeFilterMatched(transPath, PluginConfig.argusApmConfig().includes) &&
                    !isExcludeFilterMatched(transPath, PluginConfig.argusApmConfig().excludes)
            if (isInclude) {
                cache(file, File(includeDirPath + subPath))
            } else {
                cache(file, File(excludeDirPath + subPath))
            }
        }
    }

    fun filterAJClassFromJar(jarInput: JarInput) {
        val jarFile = JarFile(jarInput.file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val entryName = jarEntry.name
            if (!(jarEntry.isDirectory || !isClassFile(entryName))) {
                val bytes = ByteStreams.toByteArray(jarFile.getInputStream(jarEntry))
                val cacheFile = File(aspectPath + File.separator + entryName)
                if (isAspectClass(bytes)) {
                    cache(bytes, cacheFile)
                }
            }
        }

        jarFile.close()

    }

    fun filterClassFromJar(transformInvocation: TransformInvocation, jarInput: JarInput) {
        //如果该Jar包不需要参与到AJC代码织入的话,则直接拷贝到目标文件目录下
        if (!filterJar(jarInput, PluginConfig.argusApmConfig().includes, PluginConfig.argusApmConfig().excludes, PluginConfig.argusApmConfig().excludeJars)) {
            val dest = transformInvocation.outputProvider.getContentLocation(jarInput.name
                    , jarInput.contentTypes
                    , jarInput.scopes
                    , Format.JAR)
            FileUtils.copyFile(jarInput.file, dest)
        }
    }

    fun whenAJClassChangedOfDir(dirInput: DirectoryInput, file: File, status: Status, inputSourceFileStatus: InputSourceFileStatus) {
        if (isAspectClassFile(file)) {
            log("aj class changed ${file.absolutePath}")
            inputSourceFileStatus.isAspectChanged = true
            val path = file.absolutePath
            val subPath = path.substring(dirInput.file.absolutePath.length)
            val cacheFile = File(aspectPath + subPath)

            when (status) {
                Status.REMOVED -> {
                    FileUtils.deleteQuietly(cacheFile)
                }
                Status.CHANGED -> {
                    FileUtils.deleteQuietly(cacheFile)
                    cache(file, cacheFile)
                }
                Status.ADDED -> {
                    cache(file, cacheFile)
                }
                else -> {
                }
            }
        }
    }

    fun whenClassChangedOfDir(dirInput: DirectoryInput, file: File, status: Status, inputSourceFileStatus: InputSourceFileStatus) {
        val path = file.absolutePath
        val subPath = path.substring(dirInput.file.absolutePath.length)
        val transPath = subPath.replace(File.separator, ".")

        val isInclude = isIncludeFilterMatched(transPath, PluginConfig.argusApmConfig().includes) && !isExcludeFilterMatched(transPath, PluginConfig.argusApmConfig().excludes)

        if (!inputSourceFileStatus.isIncludeFileChanged && isInclude) {
            inputSourceFileStatus.isIncludeFileChanged = isInclude
        }

        if (!inputSourceFileStatus.isExcludeFileChanged && !isInclude) {
            inputSourceFileStatus.isExcludeFileChanged = !isInclude
        }

        val target = File(if (isInclude) {
            includeDirPath + subPath
        } else {
            excludeDirPath + subPath
        })
        when (status) {
            Status.REMOVED -> {
                logCore("[ Status.REMOVED ] file path is ${file.absolutePath}")
                FileUtils.deleteQuietly(target)
            }
            Status.CHANGED -> {
                logCore("[ Status.CHANGED ] file path is ${file.absolutePath}")
                FileUtils.deleteQuietly(target)
                cache(file, target)
            }
            Status.ADDED -> {
                logCore("[ Status.ADDED ] file path is ${file.absolutePath}")
                cache(file, target)
            }
            else -> {
                logCore("else file path is ${file.absolutePath}")
            }
        }
    }

    fun whenAJClassChangedOfJar(jarInput: JarInput, inputSourceFileStatus: InputSourceFileStatus) {
        val jarFile = java.util.jar.JarFile(jarInput.file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val entryName = jarEntry.name
            if (!jarEntry.isDirectory && isClassFile(entryName)) {
                val bytes = ByteStreams.toByteArray(jarFile.getInputStream(jarEntry))
                val cacheFile = java.io.File(aspectPath + java.io.File.separator + entryName)
                if (isAspectClass(bytes)) {
                    inputSourceFileStatus.isAspectChanged = true
                    when {
                        jarInput.status == Status.REMOVED -> FileUtils.deleteQuietly(cacheFile)
                        jarInput.status == Status.CHANGED -> {
                            FileUtils.deleteQuietly(cacheFile)
                            cache(bytes, cacheFile)
                        }
                        jarInput.status == Status.ADDED -> {
                            cache(bytes, cacheFile)
                        }
                    }
                }
            }
            jarFile.close()
        }
    }


    fun whenClassChangedOfJar(transformInvocation: TransformInvocation, jarInput: JarInput) {
        val outputJar = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)

        when {
            jarInput.status == Status.REMOVED -> {
                FileUtils.deleteQuietly(outputJar)
            }
            jarInput.status == Status.ADDED -> {
                filterJar(jarInput, PluginConfig.argusApmConfig().includes, PluginConfig.argusApmConfig().excludes, PluginConfig.argusApmConfig().excludeJars)
            }
            jarInput.status == Status.CHANGED -> {
                FileUtils.deleteQuietly(outputJar)
            }
        }

        //将不需要做AOP处理的文件原样copy到输出目录
        if (!filterJar(jarInput, PluginConfig.argusApmConfig().includes, PluginConfig.argusApmConfig().excludes, PluginConfig.argusApmConfig().excludeJars)) {
            FileUtils.copyFile(jarInput.file, outputJar)
        }
    }


    fun hasAspectJFile(): Boolean {
        return countOfFiles(File(aspectPath)) > 0
    }

}

fun getAspectDir(): File {
    return File(aspectPath)
}

fun getIncludeFileDir(): File {
    return File(includeDirPath)
}

fun getExcludeFileDir(): File {
    val excludeFile = File(excludeDirPath)
    if (!excludeFile.exists()) {
        excludeFile.mkdir()
    }
    return excludeFile
}