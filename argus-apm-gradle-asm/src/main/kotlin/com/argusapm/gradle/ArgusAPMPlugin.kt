package com.argusapm.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.argusapm.gradle.internal.ArgusDependencyResolutionListener
import com.argusapm.gradle.internal.BuildTimeListener
import com.argusapm.gradle.internal.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ArgusAPMPlugin : Plugin<Project> {
    private lateinit var mProject: Project
    override fun apply(project: Project) {
        mProject = project
        project.extensions.create(AppConstant.USER_CONFIG, ArgusApmConfig::class.java)
        PluginConfig.init(project)
        //自定义依赖库管理
        project.gradle.addListener(ArgusDependencyResolutionListener(project))
        
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            //监听每个任务的执行时间
            project.gradle.addListener(BuildTimeListener())

            val android = project.extensions.getByType(AppExtension::class.java)
            android.registerTransform(ArgusAPMTransform(project))
        }
    }
}