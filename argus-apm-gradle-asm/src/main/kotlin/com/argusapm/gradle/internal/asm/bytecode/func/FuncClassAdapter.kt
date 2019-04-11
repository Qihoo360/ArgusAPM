package com.argusapm.gradle.internal.asm.bytecode.func

import com.argusapm.gradle.internal.asm.bytecode.BaseClassVisitor
import com.argusapm.gradle.internal.utils.TypeUtil.Companion.isNeedWeaveMethod
import com.argusapm.gradle.internal.utils.TypeUtil.Companion.isOnReceiveMethod
import com.argusapm.gradle.internal.utils.TypeUtil.Companion.isRunMethod
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class FuncClassAdapter(api: Int, cv: ClassVisitor?) : BaseClassVisitor(api, cv) {
    override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        if (isInterface || !isNeedWeaveMethod(className, access)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        val mv = cv.visitMethod(access, name, desc, signature, exceptions)
        if ((isRunMethod(name, desc) || isOnReceiveMethod(name, desc)) && mv != null) {
            return FuncMethodAdapter(className.replace("/", "."), name, desc, api, access, desc, mv)
        }
        return mv
    }
}