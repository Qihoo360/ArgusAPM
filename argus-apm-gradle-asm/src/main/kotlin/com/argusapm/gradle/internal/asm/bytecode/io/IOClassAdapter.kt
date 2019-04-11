package com.argusapm.gradle.internal.bytecode.func

import com.argusapm.gradle.internal.asm.bytecode.BaseClassVisitor
import com.argusapm.gradle.internal.utils.TypeUtil
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class IOClassAdapter(api: Int, cv: ClassVisitor?) : BaseClassVisitor(api, cv) {

    override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = cv.visitMethod(access, name, desc, signature, exceptions)
        if (TypeUtil.isIO(className)) {
            if (mv != null) {
                return IOMethodAdapter(className.replace("/", "."), name, desc, api, access, desc, mv)
            }
        }
        return mv
    }
}