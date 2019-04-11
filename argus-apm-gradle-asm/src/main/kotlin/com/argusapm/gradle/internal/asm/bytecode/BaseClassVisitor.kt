package com.argusapm.gradle.internal.asm.bytecode

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

open class BaseClassVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {
    var className = ""
    var isInterface: Boolean = false
    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
        this.isInterface = (access and Opcodes.ACC_INTERFACE) != 0
    }
}