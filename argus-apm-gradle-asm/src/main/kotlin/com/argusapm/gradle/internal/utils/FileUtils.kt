package com.argusapm.gradle.internal.utils

import com.google.common.hash.Hashing
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream

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

fun File.isWeavableClass(): Boolean {
    return name.endsWith(".class") && !name.contains("R$")
            && !name.contains("R.class") && !name.contains("BuildConfig.class")
}

fun File.getUniqueJarName(): String {
    val origJarName = name
    val hashing = Hashing.sha256().hashString(path, Charsets.UTF_16LE).toString()
    val dotPos = origJarName.lastIndexOf('.')
    return if (dotPos < 0) {
        "${origJarName}_$hashing"
    } else {
        val nameWithoutDotExt = origJarName.substring(0, dotPos)
        "${nameWithoutDotExt}_$hashing"
    }
}


class ZipFileUtils {
    companion object {
        @Throws(Exception::class)
        fun addZipEntry(zipOutputStream: ZipOutputStream, zipEntry: ZipEntry, inputStream: InputStream) {
            try {
                zipOutputStream.putNextEntry(zipEntry)
                val buffer = ByteArray(16 * 1024)
                var length = -1

                do {
                    length = inputStream.read(buffer, 0, buffer.size)
                    if (length != -1) {
                        zipOutputStream.write(buffer, 0, length)
                        zipOutputStream.flush()
                    } else {
                        break
                    }
                } while (true)


            } catch (e: ZipException) {
                log("addZipEntry error!")
            } finally {
                closeQuietly(inputStream)

                zipOutputStream.closeEntry()
            }
        }

        private fun closeQuietly(closeable: Closeable?) {
            try {
                closeable?.close()
            } catch (e: IOException) {
                log("Failed to close resource")
            }
        }
    }
}
