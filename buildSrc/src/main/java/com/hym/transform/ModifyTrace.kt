package com.hym.transform

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.io.InputStream

/**
 * @author hehua2008
 * @date 2022/1/5
 */
object ModifyTrace {
    fun modifyByteCode(ins: InputStream): ByteArray {
        val cr = ClassReader(ins)
        val cn = ClassNode()
        cr.accept(cn, 0)

        cn.methods.removeIf { it.name == "beginSection" && it.desc == "(Ljava/lang/String;)Ljava/lang/Object;" }
        val beginMv = cn.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
            "beginSection",
            "(Ljava/lang/String;)Ljava/lang/Object;",
            null,
            null
        )
        beginMv.run {
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

        cn.methods.removeIf { it.name == "endSection" && it.desc == "(Ljava/lang/Object;)V" }
        val endMv = cn.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
            "endSection",
            "(Ljava/lang/Object;)V",
            null,
            null
        )
        endMv.run {
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
            visitMethodInsn(Opcodes.INVOKESTATIC, "android/os/Trace", "endSection", "()V", false)
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

        val cw = ClassWriter(0)
        cn.accept(cw)
        return cw.toByteArray()
    }
}