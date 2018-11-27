package com.argusapm.gradle.internal

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class AspectJClassVisitor(classWriter: ClassWriter) : ClassVisitor(Opcodes.ASM5, classWriter) {
    var isAspectClass = false

    override fun visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor {
        isAspectClass = (desc == "Lorg/aspectj/lang/annotation/Aspect;")
        return super.visitAnnotation(desc, visible)
    }
}