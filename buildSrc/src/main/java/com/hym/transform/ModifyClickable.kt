package com.hym.transform

import org.objectweb.asm.*
import java.io.InputStream

/**
 * @author hehua2008
 * @date 2022/1/7
 */
object ModifyClickable {
    fun modifyByteCode(ins: InputStream): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        val cv = MyClassVisitor(Opcodes.ASM9, cw)
        val cr = ClassReader(ins)
        cr.accept(cv, 0)
        return cw.toByteArray()
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
            if (name?.startsWith("detectTapGestures") == true && descriptor == "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;") {
                return DetectTapGesturesMethodVisitor(api, superMv)
            }
            if (name?.startsWith("detectTapAndPress") == true && descriptor == "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;") {
                return DetectTapAndPressMethodVisitor(api, superMv)
            }
            return superMv
        }
    }

    private abstract class BaseMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        MethodVisitor(api, methodVisitor) {

        abstract fun doVisitCode()

        final override fun visitCode() {
            super.visitCode()
            doVisitCode()
        }
    }

    private class DetectTapGesturesMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitCode() {
            super.visitVarInsn(Opcodes.ALOAD, 1)
            val label1 = Label()
            super.visitJumpInsn(Opcodes.IFNULL, label1)
            val label2 = Label()
            super.visitLabel(label2)
            super.visitLineNumber(65, label2)
            super.visitTypeInsn(Opcodes.NEW, "androidx/compose/runtime/OnDoubleClickWrapper")
            super.visitInsn(Opcodes.DUP)
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/ui/Modifier")
            super.visitVarInsn(Opcodes.ALOAD, 1)
            super.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "androidx/compose/runtime/OnDoubleClickWrapper",
                "<init>",
                "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;)V",
                false
            )
            super.visitVarInsn(Opcodes.ASTORE, 1)
            super.visitLabel(label1)
            super.visitLineNumber(67, label1)
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            super.visitVarInsn(Opcodes.ALOAD, 2)
            val label3 = Label()
            super.visitJumpInsn(Opcodes.IFNULL, label3)
            val label4 = Label()
            super.visitLabel(label4)
            super.visitLineNumber(68, label4)
            super.visitTypeInsn(Opcodes.NEW, "androidx/compose/runtime/OnLongClickWrapper")
            super.visitInsn(Opcodes.DUP)
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/ui/Modifier")
            super.visitVarInsn(Opcodes.ALOAD, 2)
            super.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "androidx/compose/runtime/OnLongClickWrapper",
                "<init>",
                "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;)V",
                false
            )
            super.visitVarInsn(Opcodes.ASTORE, 2)
            super.visitLabel(label3)
            super.visitLineNumber(70, label3)
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            super.visitVarInsn(Opcodes.ALOAD, 4)
            val label5 = Label()
            super.visitJumpInsn(Opcodes.IFNULL, label5)
            val label6 = Label()
            super.visitLabel(label6)
            super.visitLineNumber(71, label6)
            super.visitTypeInsn(Opcodes.NEW, "androidx/compose/runtime/OnClickWrapper")
            super.visitInsn(Opcodes.DUP)
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/ui/Modifier")
            super.visitVarInsn(Opcodes.ALOAD, 4)
            super.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "androidx/compose/runtime/OnClickWrapper",
                "<init>",
                "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;)V",
                false
            )
            super.visitVarInsn(Opcodes.ASTORE, 4)
            super.visitLabel(label5)
            super.visitLineNumber(107, label5)
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
        }
    }

    private class DetectTapAndPressMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitCode() {
            super.visitVarInsn(Opcodes.ALOAD, 2)
            val label1 = Label()
            super.visitJumpInsn(Opcodes.IFNULL, label1)
            val label2 = Label()
            super.visitLabel(label2)
            super.visitLineNumber(135, label2)
            super.visitTypeInsn(Opcodes.NEW, "androidx/compose/runtime/OnClickWrapper")
            super.visitInsn(Opcodes.DUP)
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/ui/Modifier")
            super.visitVarInsn(Opcodes.ALOAD, 2)
            super.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "androidx/compose/runtime/OnClickWrapper",
                "<init>",
                "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;)V",
                false
            )
            super.visitVarInsn(Opcodes.ASTORE, 2)
            super.visitLabel(label1)
            super.visitLineNumber(137, label1)
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
        }
    }
}