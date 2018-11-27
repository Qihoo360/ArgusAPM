package com.argusapm.gradle.internal.utils

import com.android.SdkConstants
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInvocation
import com.argusapm.gradle.internal.JarMerger
import com.argusapm.gradle.internal.AspectJClassVisitor
import com.argusapm.gradle.internal.FileType
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.util.jar.JarFile


fun File.eachFileRecurse(action: (File) -> Unit) {
    if (!isDirectory) {
        action(this)
    } else {
        listFiles()?.forEach { file ->
            if (file.isDirectory) {
                file.eachFileRecurse(action)
            } else {
                action(file)
            }
        }
    }
}

fun isAspectClassFile(file: File): Boolean {
    if (isClassFile(file)) {
        return try {
            isAspectClass(FileUtils.readFileToByteArray(file))
        } catch (e: Exception) {
            logCore("isAspectClassFile Exception:[ ${e.message} ]")
            false
        }
    }
    return false
}

fun isAspectClass(bytes: ByteArray): Boolean {
    if (bytes.isEmpty()) {
        return false
    }

    try {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        val aspectJClassVisitor = AspectJClassVisitor(classWriter)
        classReader.accept(aspectJClassVisitor, ClassReader.EXPAND_FRAMES)
        return aspectJClassVisitor.isAspectClass
    } catch (e: Exception) {

    }

    return false
}

private fun fileType(file: File): FileType {
    val filePath = file.absolutePath
    return when {
        filePath?.toLowerCase()!!.endsWith(".java") -> FileType.JAVA
        filePath.toLowerCase().endsWith(".class") -> FileType.CLASS
        filePath.toLowerCase().endsWith(".jar") -> FileType.JAR
        filePath.toLowerCase().endsWith(".kt") -> FileType.KOTLIN
        filePath.toLowerCase().endsWith(".groovy") -> FileType.GROOVY
        else -> FileType.DEFAULT
    }
}

fun isClassFile(file: File): Boolean {
    return fileType(file) == FileType.CLASS
}

fun isClassFile(filePath: String): Boolean {
    return filePath.toLowerCase().endsWith(".class")
}

fun cache(sourceFile: File, cacheFile: File) {
    val bytes = FileUtils.readFileToByteArray(sourceFile)
    cache(bytes, cacheFile)
}

fun cache(classBytes: ByteArray, cacheFile: File) {
    FileUtils.writeByteArrayToFile(cacheFile, classBytes)
}

fun outputFiles(transformInvocation: TransformInvocation) {
    if (transformInvocation.isIncremental) {
        outputChangeFiles(transformInvocation)
    } else {
        outputAllFiles(transformInvocation)
    }
}

fun outputAllFiles(transformInvocation: TransformInvocation) {
    transformInvocation.outputProvider.deleteAll()

    transformInvocation.inputs.forEach { input ->
        input.directoryInputs.forEach { dirInput ->
            val outputJar = transformInvocation.outputProvider.getContentLocation("output", dirInput.contentTypes, dirInput.scopes, Format.JAR)

            mergeJar(dirInput.file, outputJar)
        }

        input.jarInputs.forEach { jarInput ->
            val dest = transformInvocation.outputProvider.getContentLocation(jarInput.name
                    , jarInput.contentTypes
                    , jarInput.scopes
                    , Format.JAR)
            FileUtils.copyFile(jarInput.file, dest)
        }
    }
}

fun outputChangeFiles(transformInvocation: TransformInvocation) {
    transformInvocation.inputs.forEach { input ->
        input.directoryInputs.forEach { dirInput ->
            if (dirInput.changedFiles.isNotEmpty()) {
                val excludeJar = transformInvocation.outputProvider.getContentLocation("exclude", dirInput.contentTypes, dirInput.scopes, Format.JAR)
                mergeJar(dirInput.file, excludeJar)
            }
        }

        input.jarInputs.forEach { jarInput ->
            val target = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
            when {
                jarInput.status == Status.REMOVED -> {
                    FileUtils.forceDelete(target)
                }

                jarInput.status == Status.CHANGED -> {
                    FileUtils.forceDelete(target)
                    FileUtils.copyFile(jarInput.file, target)
                }

                jarInput.status == Status.ADDED -> {
                    FileUtils.copyFile(jarInput.file, target)
                }

            }
        }
    }
}

fun mergeJar(sourceDir: File, targetJar: File) {
    if (!targetJar.parentFile.exists()) {
        FileUtils.forceMkdir(targetJar.parentFile)
    }

    FileUtils.deleteQuietly(targetJar)
    val jarMerger = JarMerger(targetJar)
    try {
        jarMerger.setFilter(object : JarMerger.IZipEntryFilter {
            override fun checkEntry(archivePath: String): Boolean {
                return archivePath.endsWith(SdkConstants.DOT_CLASS)
            }
        })
        jarMerger.addFolder(sourceDir)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        jarMerger.close()
    }
}

fun filterJar(jarInput: JarInput, includes: List<String>, excludes: List<String>, excludeJars: List<String>): Boolean {
    if (excludeJars.isNotEmpty()) {
        val jarPath = jarInput.file.absolutePath
        return !isExcludeFilterMatched(jarPath, excludeJars)
    }
    if (includes.isEmpty() && excludes.isEmpty()) {
        return true
    } else if (includes.isEmpty()) {
        var isExclude = false
        val jarFile = JarFile(jarInput.file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val entryName = jarEntry.name
            val tranEntryName = entryName.replace(File.separator, ".")
            if (isExcludeFilterMatched(tranEntryName, excludes)) {
                isExclude = true
                break
            }
        }

        jarFile.close()
        return !isExclude
    } else if (excludes.isEmpty()) {
        var isInclude = false
        val jarFile = JarFile(jarInput.file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val entryName = jarEntry.name
            val tranEntryName = entryName.replace(File.separator, ".")
            if (isIncludeFilterMatched(tranEntryName, includes)) {
                isInclude = true
                break
            }
        }

        jarFile.close()
        return isInclude
    } else {
        var isIncludeMatched = false
        var isExcludeMatched = false
        val jarFile = JarFile(jarInput.file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val entryName = jarEntry.name
            val tranEntryName = entryName.replace(File.separator, ".")
            if (isIncludeFilterMatched(tranEntryName, includes)) {
                isIncludeMatched = true
            }

            if (isExcludeFilterMatched(tranEntryName, excludes)) {
                isExcludeMatched = true
            }
        }

        jarFile.close()
        return isIncludeMatched && !isExcludeMatched
    }
}

fun isExcludeFilterMatched(str: String, filters: List<String>): Boolean {
    return isFilterMatched(str, filters, FilterPolicy.EXCLUDE)
}

fun isIncludeFilterMatched(str: String, filters: List<String>): Boolean {
    return isFilterMatched(str, filters, FilterPolicy.INCLUDE)
}

private fun isFilterMatched(str: String, filters: List<String>, filterPolicy: FilterPolicy): Boolean {

    if (filters.isEmpty()) {
        return filterPolicy == FilterPolicy.INCLUDE
    }

    filters.forEach {
        if (isContained(str, it)) {
            return true
        }
    }
    return false
}

private fun isContained(str: String, filter: String): Boolean {

    if (str.contains(filter)) {
        return true
    } else {
        if (filter.contains("/")) {
            return str.contains(filter.replace("/", File.separator))
        } else if (filter.contains("\\")) {
            return str.contains(filter.replace("\\", File.separator))
        }
    }

    return false
}

enum class FilterPolicy {
    INCLUDE,
    EXCLUDE
}

fun countOfFiles(file: File): Int {
    return if (file.isFile) {
        1
    } else {
        val files = file.listFiles()
        var total = 0
        files?.forEach {
            total += countOfFiles(it)
        }

        total
    }
}