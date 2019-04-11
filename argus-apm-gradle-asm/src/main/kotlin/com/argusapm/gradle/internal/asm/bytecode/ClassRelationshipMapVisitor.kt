package com.argusapm.gradle.internal.asm.bytecode

import org.objectweb.asm.ClassVisitor

class ClassRelationshipMapVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {
    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        ClassFile.classMap(name, superName)
    }
}