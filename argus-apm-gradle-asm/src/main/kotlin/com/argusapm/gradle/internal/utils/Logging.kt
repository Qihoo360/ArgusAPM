package com.argusapm.gradle.internal.utils

import com.argusapm.gradle.AppConstant


fun log(msg: String) {
    kotlin.io.println("ArgusAPM V${AppConstant.VER}:-->$msg")
}