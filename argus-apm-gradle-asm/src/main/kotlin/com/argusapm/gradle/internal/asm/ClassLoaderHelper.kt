package com.argusapm.gradle.internal.asm

import com.android.build.api.transform.TransformInput
import com.android.build.gradle.AppExtension
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables
import org.gradle.api.Project
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader


class ClassLoaderHelper {
    companion object {
        @Throws(MalformedURLException::class)
        fun getClassLoader(inputs: Collection<TransformInput>,
                           referencedInputs: Collection<TransformInput>,
                           project: Project): URLClassLoader {
            val urls = ImmutableList.Builder<URL>()
            val androidJarPath = getAndroidJarPath(project)
            val file = File(androidJarPath)
            val androidJarURL = file.toURI().toURL()
            urls.add(androidJarURL)
            for (totalInputs in Iterables.concat(inputs, referencedInputs)) {
                for (directoryInput in totalInputs.directoryInputs) {
                    if (directoryInput.file.isDirectory) {
                        urls.add(directoryInput.file.toURI().toURL())
                    }
                }
                for (jarInput in totalInputs.jarInputs) {
                    if (jarInput.file.isFile) {
                        urls.add(jarInput.file.toURI().toURL())
                    }
                }
            }
            val allUrls = urls.build()
            val classLoaderUrls = allUrls.toArray(arrayOfNulls<URL>(allUrls.size))
            return URLClassLoader(classLoaderUrls)
        }

        /**
         * /Users/quinn/Documents/Android/SDK/platforms/android-28/android.jar
         */
        private fun getAndroidJarPath(project: Project): String {
            val appExtension = project.properties["android"] as AppExtension
            var sdkDirectory = appExtension.sdkDirectory.absolutePath
            val compileSdkVersion = appExtension.compileSdkVersion
            sdkDirectory = sdkDirectory + File.separator + "platforms" + File.separator
            return sdkDirectory + compileSdkVersion + File.separator + "android.jar"
        }
    }
}