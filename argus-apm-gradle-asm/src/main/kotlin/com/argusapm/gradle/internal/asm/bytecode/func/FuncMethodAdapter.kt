package com.argusapm.gradle.internal.asm.bytecode.func

import com.argusapm.gradle.internal.utils.TypeUtil
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.commons.LocalVariablesSorter


class FuncMethodAdapter(private val className: String, private val methodName: String, private val methodDesc: String, api: Int, access: Int, desc: String?, mv: MethodVisitor?) : LocalVariablesSorter(api, access, desc, mv) {

    private var startTimeIndex = 0
    private var lineNumber = 0

    override fun visitLineNumber(line: Int, start: Label?) {
        this.lineNumber = line
        super.visitLineNumber(line, start)
    }

    override fun visitCode() {
        super.visitCode()
        if (TypeUtil.isRunMethod(methodName, methodDesc)) {
            whenMethodEnter()
        } else if (TypeUtil.isOnReceiveMethod(methodName, methodDesc)) {
            whenMethodEnter()
        }

    }

    private fun whenMethodEnter() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        startTimeIndex = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(LSTORE, startTimeIndex);
    }

    override fun visitInsn(opcode: Int) {
        if (((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW)) {
            if (TypeUtil.isRunMethod(methodName, methodDesc)) {
                whenRunMethodExit()
            } else if (TypeUtil.isOnReceiveMethod(methodName, methodDesc)) {
                whenOnReceiveMethodExit()
            }
        }
        super.visitInsn(opcode);
    }

    private fun whenOnReceiveMethodExit() {

        mv.visitVarInsn(LLOAD, startTimeIndex)
        mv.visitLdcInsn("method-execution")
        mv.visitLdcInsn("void $className.onReceive(Context context, Intent intent)")
        mv.visitVarInsn(ALOAD, 1)
        mv.visitVarInsn(ALOAD, 2)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitLdcInsn("${className.substring(className.lastIndexOf(".") + 1)}.java:$lineNumber")
        mv.visitLdcInsn("execution(void $className.onReceive(Context context, Intent intent))")
        mv.visitLdcInsn("onReceive")
        mv.visitInsn(ACONST_NULL)
        mv.visitMethodInsn(INVOKESTATIC, "com/argusapm/android/core/job/func/FuncTrace", "dispatch", "(JLjava/lang/String;Ljava/lang/String;Landroid/content/Context;Landroid/content/Intent;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false)
    }

    private fun whenRunMethodExit() {
        mv.visitVarInsn(LLOAD, startTimeIndex)
        mv.visitLdcInsn("method-execution")
        mv.visitLdcInsn("void $className.run()")
        mv.visitInsn(ACONST_NULL)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitLdcInsn("${className.substring(className.lastIndexOf(".") + 1)}.java:$lineNumber")
        mv.visitLdcInsn("execution(void $className.run())")
        mv.visitLdcInsn("run")
        mv.visitInsn(ACONST_NULL)
        mv.visitMethodInsn(INVOKESTATIC, "com/argusapm/android/core/job/func/FuncTrace", "dispatch", "(JLjava/lang/String;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false)

    }
}