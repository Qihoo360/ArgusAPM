package com.argusapm.gradle.internal.bytecode.func

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.LocalVariablesSorter

class WebMethodAdapter(private val methodName: String, private val methodDesc: String, api: Int, access: Int, desc: String?, mv: MethodVisitor?) : LocalVariablesSorter(api, access, desc, mv) {
    override fun visitCode() {

        mv.visitVarInsn(ALOAD, 1)
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/webkit/WebView", "getProgress", "()I", false)
        mv.visitIntInsn(BIPUSH, 100)
        val l0 = Label()
        mv.visitJumpInsn(IF_ICMPNE, l0)
        mv.visitVarInsn(ALOAD, 1)
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/webkit/WebView", "getSettings", "()Landroid/webkit/WebSettings;", false)
        mv.visitVarInsn(ASTORE, 3)
        mv.visitVarInsn(ALOAD, 3)
        mv.visitInsn(ICONST_1)
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/webkit/WebSettings", "setJavaScriptEnabled", "(Z)V", false)
        mv.visitVarInsn(ALOAD, 1)
        mv.visitTypeInsn(NEW, "com/argusapm/android/core/job/webview/JSBridge")
        mv.visitInsn(DUP)
        mv.visitVarInsn(ALOAD, 2)
        mv.visitMethodInsn(INVOKESPECIAL, "com/argusapm/android/core/job/webview/JSBridge", "<init>", "(Ljava/lang/String;)V", false)
        mv.visitLdcInsn("android_apm")
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/webkit/WebView", "addJavascriptInterface", "(Ljava/lang/Object;Ljava/lang/String;)V", false)
        mv.visitLdcInsn("javascript:%s.sendResource(JSON.stringify(window.performance.timing));")
        mv.visitInsn(ICONST_1)
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
        mv.visitInsn(DUP)
        mv.visitInsn(ICONST_0)
        mv.visitLdcInsn("android_apm")
        mv.visitInsn(AASTORE)
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false)
        mv.visitVarInsn(ASTORE, 4)
        mv.visitVarInsn(ALOAD, 1)
        mv.visitVarInsn(ALOAD, 4)
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/webkit/WebView", "loadUrl", "(Ljava/lang/String;)V", false)
        mv.visitLabel(l0)

        super.visitCode()
    }
}