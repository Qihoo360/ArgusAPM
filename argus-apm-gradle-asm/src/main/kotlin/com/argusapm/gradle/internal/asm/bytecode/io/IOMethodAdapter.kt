package com.argusapm.gradle.internal.bytecode.func

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.LocalVariablesSorter

class IOMethodAdapter(private val className: String, private val methodName: String, private val methodDesc: String, api: Int, access: Int, desc: String?, mv: MethodVisitor?) : LocalVariablesSorter(api, access, desc, mv) {
    override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean) {

        super.visitMethodInsn(opcode, owner, name, desc, itf)

        
    }
}