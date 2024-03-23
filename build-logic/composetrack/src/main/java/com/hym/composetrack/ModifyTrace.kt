package com.hym.composetrack

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.InputStream

/**
 * @author hehua2008
 * @date 2022/1/5
 */
object ModifyTrace {
    fun modifyByteCode(ins: InputStream): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        val cv = MyClassVisitor(Opcodes.ASM9, cw)
        val cr = ClassReader(ins)
        cr.accept(cv, 0)
        return cw.toByteArray()
    }

    fun createClassVisitor(api: Int, classVisitor: ClassVisitor): ClassVisitor {
        return MyClassVisitor(api, classVisitor)
    }

    private class MyClassVisitor(api: Int, classVisitor: ClassVisitor) :
        ClassVisitor(api, classVisitor) {

        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val superMv = super.visitMethod(access, name, descriptor, signature, exceptions)
            return when {
                name == "beginSection" && descriptor == "(Ljava/lang/String;)Ljava/lang/Object;" -> {
                    BeginSectionMethodVisitor(api, superMv)
                }

                name == "endSection" && descriptor == "(Ljava/lang/Object;)V" -> {
                    EndSectionMethodVisitor(api, superMv)
                }

                else -> superMv
            }
        }
    }

    private class BeginSectionMethodVisitor(api: Int, val myDelegate: MethodVisitor) :
        MethodVisitor(api, null) {
        override fun visitEnd() {
            myDelegate.run {
                visitAnnotation("Lorg/jetbrains/annotations/NotNull;", false)
                    .visitEnd()
                visitAnnotableParameterCount(1, false)
                visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
                    .visitEnd()
                visitCode()
                val label0 = Label()
                visitLabel(label0)
                visitVarInsn(Opcodes.ALOAD, 1)
                visitLdcInsn("name")
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "kotlin/jvm/internal/Intrinsics",
                    "checkNotNullParameter",
                    "(Ljava/lang/Object;Ljava/lang/String;)V",
                    false
                )
                val label1 = Label()
                visitLabel(label1)
                visitLineNumber(9, label1)
                visitVarInsn(Opcodes.ALOAD, 1)
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "android/os/Trace",
                    "beginSection",
                    "(Ljava/lang/String;)V",
                    false
                )
                val label2 = Label()
                visitLabel(label2)
                visitLineNumber(10, label2)
                visitTypeInsn(Opcodes.NEW, "androidx/compose/runtime/TraceToken")
                visitInsn(Opcodes.DUP)
                visitVarInsn(Opcodes.ALOAD, 1)
                visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "androidx/compose/runtime/TraceToken",
                    "<init>",
                    "(Ljava/lang/String;)V",
                    false
                )
                visitVarInsn(Opcodes.ASTORE, 2)
                val label3 = Label()
                visitLabel(label3)
                visitLineNumber(11, label3)
                visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "androidx/compose/runtime/TraceListener",
                    "INSTANCE",
                    "Landroidx/compose/runtime/TraceListener;"
                )
                visitVarInsn(Opcodes.ALOAD, 2)
                visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "androidx/compose/runtime/TraceListener",
                    "onTraceBegin",
                    "(Landroidx/compose/runtime/TraceToken;)V",
                    false
                )
                val label4 = Label()
                visitLabel(label4)
                visitLineNumber(12, label4)
                visitVarInsn(Opcodes.ALOAD, 2)
                visitInsn(Opcodes.ARETURN)
                val label5 = Label()
                visitLabel(label5)
                visitLocalVariable(
                    "token",
                    "Landroidx/compose/runtime/TraceToken;",
                    null,
                    label3,
                    label5,
                    2
                )
                visitLocalVariable(
                    "this",
                    "Landroidx/compose/runtime/Trace;",
                    null,
                    label0,
                    label5,
                    0
                )
                visitLocalVariable("name", "Ljava/lang/String;", null, label0, label5, 1)
                visitMaxs(3, 3)
                visitEnd()
            }
        }
    }

    private class EndSectionMethodVisitor(api: Int, val myDelegate: MethodVisitor) :
        MethodVisitor(api, null) {
        override fun visitEnd() {
            myDelegate.run {
                visitAnnotableParameterCount(1, false)
                visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
                    .visitEnd()
                visitCode()
                val label0 = Label()
                visitLabel(label0)
                visitVarInsn(Opcodes.ALOAD, 1)
                visitLdcInsn("token")
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "kotlin/jvm/internal/Intrinsics",
                    "checkNotNullParameter",
                    "(Ljava/lang/Object;Ljava/lang/String;)V",
                    false
                )
                val label1 = Label()
                visitLabel(label1)
                visitLineNumber(16, label1)
                visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "androidx/compose/runtime/TraceListener",
                    "INSTANCE",
                    "Landroidx/compose/runtime/TraceListener;"
                )
                visitVarInsn(Opcodes.ALOAD, 1)
                visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/TraceToken")
                visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "androidx/compose/runtime/TraceListener",
                    "onTraceEnd",
                    "(Landroidx/compose/runtime/TraceToken;)V",
                    false
                )
                val label2 = Label()
                visitLabel(label2)
                visitLineNumber(17, label2)
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "android/os/Trace",
                    "endSection",
                    "()V",
                    false
                )
                val label3 = Label()
                visitLabel(label3)
                visitLineNumber(18, label3)
                visitInsn(Opcodes.RETURN)
                val label4 = Label()
                visitLabel(label4)
                visitLocalVariable(
                    "this",
                    "Landroidx/compose/runtime/Trace;",
                    null,
                    label0,
                    label4,
                    0
                )
                visitLocalVariable("token", "Ljava/lang/Object;", null, label0, label4, 1)
                visitMaxs(2, 2)
                visitEnd()
            }
        }
    }
}
