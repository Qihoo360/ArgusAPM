package com.argusapm.gradle.internal

import com.android.build.gradle.*
import com.argusapm.gradle.ArgusApmConfig
import com.argusapm.gradle.internal.utils.getVariants
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import java.io.File

class PluginConfig {
    companion object {
        lateinit var project: Project
        lateinit var encoding: String
        lateinit var bootClassPath: String
        lateinit var sourceCompatibility: String
        lateinit var targetCompatibility: String

        fun init(project: Project) {
            val hasAppPlugin = project.plugins.hasPlugin(AppPlugin::class.java)
            val hasLibPlugin = project.plugins.hasPlugin(LibraryPlugin::class.java)
            if (!hasAppPlugin && !hasLibPlugin) {
                throw  GradleException("argusapm: The 'com.android.application' or 'com.android.library' plugin is required.")
            }

            Companion.project = project

            getVariants(project).all { variant ->
                val javaCompile = variant.javaCompile as JavaCompile
                encoding = javaCompile.options.encoding
                bootClassPath = getBootClasspath().joinToString(File.pathSeparator)
                sourceCompatibility = javaCompile.sourceCompatibility
                targetCompatibility = javaCompile.targetCompatibility
            }
        }

        private fun getBootClasspath(): List<File> {
            val hasAppPlugin = project.plugins.hasPlugin(AppPlugin::class.java)
            val plugin = project.plugins.getPlugin(if (hasAppPlugin) {
                AppPlugin::class.java
            } else {
                LibraryPlugin::class.java
            })
            val extAndroid = if (hasAppPlugin) {
                project.extensions.getByType(AppExtension::class.java)
            } else {
                project.extensions.getByType(LibraryExtension::class.java)
            }
            return extAndroid.bootClasspath
                    ?: plugin::class.java.getMethod("getRuntimeJarList").invoke(plugin) as List<File>
        }

        fun argusApmConfig(): ArgusApmConfig {
            return project.extensions.getByType(ArgusApmConfig::class.java)
        }

    }
}