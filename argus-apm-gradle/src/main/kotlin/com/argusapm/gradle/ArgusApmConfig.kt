package com.argusapm.gradle

open class ArgusApmConfig {
    var enabled: Boolean = true
    var okhttpEnabled: Boolean = true
    var dependencyEnabled: Boolean = true
    val includes = arrayListOf<String>()
    val excludes = arrayListOf<String>()
    val excludeJars = arrayListOf<String>()
    val ajcArgs = arrayListOf<String>()
    val debugDependencies = arrayListOf<String>()
    val moduleDependencies = arrayListOf<String>()

    /**
     * 用来控制AJ是否织入
     */
    fun enabled(enable: Boolean): ArgusApmConfig {
        this.enabled = enable
        return this
    }

    /**
     * 用来控制是否接入okhttp相关的功能
     */
    fun okhttpEnabled(okhttpEnable: Boolean): ArgusApmConfig {
        this.okhttpEnabled = okhttpEnable
        return this
    }

    /**
     * 用来控制是否需要让插件默认依赖一些ArgusAPM库
     */
    fun dependencyEnabled(dependencyEnabled: Boolean): ArgusApmConfig {
        this.dependencyEnabled = dependencyEnabled
        return this
    }

    fun include(vararg filters: String): ArgusApmConfig {
        this.includes.addAll(filters)
        return this
    }

    fun exclude(vararg filters: String): ArgusApmConfig {
        this.excludes.addAll(filters)
        return this
    }

    fun excludeJar(vararg filters: String): ArgusApmConfig {
        this.excludeJars.addAll(filters)
        return this
    }

    fun ajcArgs(vararg ajcArgs: String): ArgusApmConfig {
        this.ajcArgs.addAll(ajcArgs)
        return this
    }

    fun debugDependencies(vararg debugDependencies: String): ArgusApmConfig {
        this.debugDependencies.addAll(debugDependencies)
        return this
    }

    fun moduleDependencies(vararg moduleDependencies: String): ArgusApmConfig {
        this.moduleDependencies.addAll(moduleDependencies)
        return this
    }
}