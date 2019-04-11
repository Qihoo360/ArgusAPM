package com.argusapm.gradle.internal

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.argusapm.gradle.ArgusApmConfig
import org.gradle.api.GradleException
import org.gradle.api.Project

class PluginConfig {
    companion object {
        lateinit var project: Project
        fun init(project: Project) {
            val hasAppPlugin = project.plugins.hasPlugin(AppPlugin::class.java)
            val hasLibPlugin = project.plugins.hasPlugin(LibraryPlugin::class.java)
            if (!hasAppPlugin && !hasLibPlugin) {
                throw  GradleException("argusapm: The 'com.android.application' or 'com.android.library' plugin is required.")
            }
            Companion.project = project
        }

        fun argusApmConfig(): ArgusApmConfig {
            return project.extensions.getByType(ArgusApmConfig::class.java)
        }
    }
}