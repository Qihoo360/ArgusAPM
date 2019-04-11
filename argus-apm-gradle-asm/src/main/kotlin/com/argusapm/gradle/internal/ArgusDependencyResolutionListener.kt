package com.argusapm.gradle.internal

import com.argusapm.gradle.AppConstant
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

val COMPILE_CONFIGURATIONS = arrayOf("api", "compile")

/**
 * 兼容Compile模式
 */
fun Project.compatCompile(depLib: Any) {
    COMPILE_CONFIGURATIONS.find { configurations.findByName(it) != null }?.let {
        dependencies.add(it, depLib)
    }
}

class ArgusDependencyResolutionListener(val project: Project) : DependencyResolutionListener {
    override fun beforeResolve(dependencies: ResolvableDependencies?) {
        if (PluginConfig.argusApmConfig().dependencyEnabled) {
            if (PluginConfig.argusApmConfig().debugDependencies.isEmpty() && PluginConfig.argusApmConfig().moduleDependencies.isEmpty()) {
                project.compatCompile("com.qihoo360.argusapm:argus-apm-main:${AppConstant.VER}")
                if (PluginConfig.argusApmConfig().okhttpEnabled) {
                    project.compatCompile("com.qihoo360.argusapm:argus-apm-okhttp:${AppConstant.VER}")
                }
            } else {
                //配置本地Module库，方便断点调试
                if (PluginConfig.argusApmConfig().moduleDependencies.isNotEmpty()) {
                    PluginConfig.argusApmConfig().moduleDependencies.forEach { moduleLib: String ->
                        project.compatCompile(project.project(moduleLib))
                    }
                }

                //发布Release版本之前，可以使用Debug库测试
                if (PluginConfig.argusApmConfig().debugDependencies.isNotEmpty()) {
                    project.repositories.mavenLocal()
                    //方便在测试的时候使用，不再需要单独的Gradle发版本
                    PluginConfig.argusApmConfig().debugDependencies.forEach { debugLib: String ->
                        project.compatCompile(debugLib)
                    }
                }
            }
        }
        project.gradle.removeListener(this)
    }

    override fun afterResolve(dependencies: ResolvableDependencies?) {
    }

}