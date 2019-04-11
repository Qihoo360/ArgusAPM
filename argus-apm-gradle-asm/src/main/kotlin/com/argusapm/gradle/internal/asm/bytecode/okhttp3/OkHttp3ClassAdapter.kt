package com.argusapm.gradle.internal.bytecode.func

import com.argusapm.gradle.internal.asm.bytecode.BaseClassVisitor
import com.argusapm.gradle.internal.utils.TypeUtil
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class OkHttp3ClassAdapter(api: Int, cv: ClassVisitor?) : BaseClassVisitor(api, cv) {

    override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        if (isInterface || !TypeUtil.isNeedWeaveMethod(className, access)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        val mv = cv.visitMethod(access, name, desc, signature, exceptions)
        if (TypeUtil.isOkhttpClientBuilder(className) && mv != null) {
            return Okhttp3MethodAdapter(name, api, access, desc, mv)
        }
        return mv
    }
}