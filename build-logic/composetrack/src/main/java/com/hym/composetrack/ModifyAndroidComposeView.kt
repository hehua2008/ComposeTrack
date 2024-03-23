package com.hym.composetrack

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
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
                name == "onAttach" && descriptor == "(Landroidx/compose/ui/node/LayoutNode;)V" -> {
                    OnAttachMethodVisitor(api, superMv)
                }

                name == "onDetach" && descriptor == "(Landroidx/compose/ui/node/LayoutNode;)V" -> {
                    OnDetachMethodVisitor(api, superMv)
                }

                else -> superMv
            }
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
