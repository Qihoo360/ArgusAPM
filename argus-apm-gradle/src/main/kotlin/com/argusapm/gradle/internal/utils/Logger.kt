package com.argusapm.gradle.internal.utils

import com.argusapm.gradle.AppConstant

fun log(info: String) {
    println("${AppConstant.TAG} $info")
}


fun logCore(info: String) {
    log(info)
}


object LogStatus {
    fun logStart(info: String) {
        println("--------------------------------------------------------------------")
        println("                  Argus-Apm [$info] Start...                 ")
        println()
    }

    fun isIncremental(info: String) {
        log("now is: isIncremental [$info]")
    }

    fun cutStart() {
        log("cut input source start...")
    }

    fun cutEnd() {
        log("cut input source end...")
    }

    fun ajcJar(info: String) {
        log("this [$info] will be compiled by ajc")
    }

    fun weaveStart() {
        log("ajc weave start...")
    }

    fun logEnd(info: String) {
        println()
        println("                  Argus-Apm [$info] end...                 ")
        println("--------------------------------------------------------------------")
    }
}