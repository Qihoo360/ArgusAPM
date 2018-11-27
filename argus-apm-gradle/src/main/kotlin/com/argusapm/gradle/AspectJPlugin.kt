package com.argusapm.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.argusapm.gradle.internal.ArgusDependencyResolutionListener
import com.argusapm.gradle.internal.BuildTimeListener
import com.argusapm.gradle.internal.PluginConfig
import com.argusapm.gradle.internal.compatCompile
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AspectJPlugin : Plugin<Project> {
    private lateinit var mProject: Project
    override fun apply(project: Project) {
        mProject = project
        project.extensions.create(AppConstant.USER_CONFIG, ArgusApmConfig::class.java)

        //公共配置初始化,方便获取公共信息
        PluginConfig.init(project)

        //自定义依赖库管理
        project.gradle.addListener(ArgusDependencyResolutionListener(project))

        project.repositories.mavenCentral()
        project.compatCompile("org.aspectj:aspectjrt:1.8.9")


        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            project.gradle.addListener(BuildTimeListener())

            val android = project.extensions.getByType(AppExtension::class.java)
            android.registerTransform(AspectJTransform(project))
        }
    }
}