package com.argusapm.gradle.internal.bytecode.func

import com.argusapm.gradle.internal.asm.bytecode.BaseClassVisitor
import org.objectweb.asm.ClassVisitor

class PowerClassAdapter(api: Int, cv: ClassVisitor?) : BaseClassVisitor(api, cv) {
}