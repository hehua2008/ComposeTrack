package com.hym.transform

import org.objectweb.asm.*
import java.io.InputStream

/**
 * @author hehua2008
 * @date 2022/1/7
 */
object ModifyAndroidComposeView {
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
            if (name == "onAttach" && descriptor == "(Landroidx/compose/ui/node/LayoutNode;)V") {
                return OnAttachMethodVisitor(api, superMv)
            } else if (name == "onDetach" && descriptor == "(Landroidx/compose/ui/node/LayoutNode;)V") {
                return OnDetachMethodVisitor(api, superMv)
            }
            return superMv
        }
    }

    private abstract class BaseMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        MethodVisitor(api, methodVisitor) {

        abstract fun doVisitInsnReturn()

        final override fun visitInsn(opcode: Int) {
            if (opcode == Opcodes.RETURN) {
                doVisitInsnReturn()
            }
            super.visitInsn(opcode)
        }
    }

    private class OnAttachMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitVarInsn(Opcodes.ALOAD, 1)
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "androidx/compose/runtime/OnAttachOnDetachListener",
                "onAttach",
                "(Landroid/view/View;Landroidx/compose/ui/node/LayoutNode;)V",
                false
            )
        }
    }

    private class OnDetachMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitVarInsn(Opcodes.ALOAD, 1)
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "androidx/compose/runtime/OnAttachOnDetachListener",
                "onDetach",
                "(Landroid/view/View;Landroidx/compose/ui/node/LayoutNode;)V",
                false
            )
        }
    }
}