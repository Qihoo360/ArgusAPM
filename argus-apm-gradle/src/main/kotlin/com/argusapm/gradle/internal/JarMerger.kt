package com.argusapm.gradle.internal

import com.google.common.io.Closer
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class JarMerger(private val jarFile: File) {
    private val buffer = ByteArray(8192)

    private var closer: Closer? = null
    private var jarOutputStream: JarOutputStream? = null
    private var filter: IZipEntryFilter? = null

    @Throws(IOException::class)
    private fun init() {
        if (this.closer == null) {
            FileUtils.forceMkdir(jarFile.parentFile)
            this.closer = Closer.create()
            val fos = this.closer!!.register(FileOutputStream(jarFile))
            jarOutputStream = this.closer!!.register(JarOutputStream(fos))
        }
    }

    /**
     * Sets a list of regex to exclude from the jar.
     */
    fun setFilter(filter: IZipEntryFilter) {
        this.filter = filter
    }

    @Throws(IOException::class)
    fun addFolder(folder: File) {
        init()
        try {
            addFolderWithPath(folder, "")
        } catch (e: IZipEntryFilter.ZipAbortException) {
            throw  IOException(e)
        }
    }

    @Throws(IOException::class, IZipEntryFilter.ZipAbortException::class)
    private fun addFolderWithPath(folder: File, path: String) {
        folder.listFiles()?.forEach { file ->

            if (file.isFile) {
                val entryPath = path + file.name
                if (filter == null || filter!!.checkEntry(entryPath)) {
                    // new entry
                    this.jarOutputStream!!.putNextEntry(JarEntry(entryPath))

                    // put the file content
                    val localCloser = Closer.create()
                    localCloser.use { localCloser ->
                        val fis = localCloser.register(FileInputStream(file))
                        var count = -1
                        while ({ count = fis.read(buffer);count }() != -1) {
                            jarOutputStream!!.write(buffer, 0, count)
                        }
                    }

                    // close the entry
                    jarOutputStream!!.closeEntry()
                }
            } else if (file.isDirectory) {
                addFolderWithPath(file, path + file.name + "/")
            }
        }
    }

    @Throws(IOException::class)
    fun addJar(file: File) {
        addJar(file, false)
    }

    @Throws(IZipEntryFilter.ZipAbortException::class)
    private fun addJar(file: File, removeEntryTimestamp: Boolean) {
        init()

        val localCloser = Closer.create()
        try {
            val fis = localCloser.register(FileInputStream(file))
            val zis = localCloser.register(ZipInputStream(fis))

            // loop on the entries of the jar file package and put them in the final jar
            var entry: ZipEntry? = null
            while ({ entry = zis.nextEntry;entry }() != null) {
                // do not take directories or anything inside a potential META-INF folder.
                if (entry!!.isDirectory) {
                    continue
                }

                val name = entry!!.name
                if (filter != null && !filter!!.checkEntry(name)) {
                    continue
                }

                var newEntry: JarEntry?

                // Preserve the STORED method of the input entry.
                newEntry = if (entry!!.method == JarEntry.STORED) {
                    JarEntry(entry)
                } else {
                    // Create a new entry so that the compressed len is recomputed.
                    JarEntry(name)
                }
                if (removeEntryTimestamp) {
                    newEntry.time = 0
                }

                // add the entry to the jar archive
                this.jarOutputStream!!.putNextEntry(newEntry)

                // read the content of the entry from the input stream, and write it into the archive.
                var count: Int = -1
                while ({ count = zis.read(buffer);count }() != -1) {
                    jarOutputStream!!.write(buffer, 0, count)
                }

                // close the entries for this file
                jarOutputStream!!.closeEntry()
                zis.closeEntry()
            }
        } catch (e: IZipEntryFilter.ZipAbortException) {
            throw  IOException(e)
        } finally {
            localCloser.close()
        }
    }

    @Throws(IOException::class)
    fun addEntry(path: String, bytes: ByteArray) {
        init()

        jarOutputStream!!.putNextEntry(JarEntry(path))
        jarOutputStream!!.write(bytes)
        jarOutputStream!!.closeEntry()
    }

    @Throws(IOException::class)
    fun close() {
        closer?.close()
    }

    /**
     * Classes which implement this interface provides a method to check whether a file should
     * be added to a Jar file.
     */
    interface IZipEntryFilter {
        /**
         * An exception thrown during packaging of a zip file into APK file.
         * This is typically thrown by implementations of
         * {@link IZipEntryFilter#checkEntry(String)}.
         */
        open class ZipAbortException : Exception() {
            private val serialVersionUID = 1L
        }


        /**
         * Checks a file for inclusion in a Jar archive.
         * @param archivePath the archive file path of the entry
         * @return <code>true</code> if the file should be included.
         * @throws IZipEntryFilter.ZipAbortException if writing the file should be aborted.
         */
        @Throws(ZipAbortException::class)
        fun checkEntry(archivePath: String): Boolean
    }
}