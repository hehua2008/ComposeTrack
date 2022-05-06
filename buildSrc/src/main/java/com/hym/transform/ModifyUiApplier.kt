package com.hym.transform

import org.objectweb.asm.*
import java.io.InputStream


/**
 * @author hehua2008
 * @date 2022/1/6
 */
object ModifyUiApplier {
    fun modifyByteCode(ins: InputStream): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
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
            if (name == "onEndChanges" && descriptor == "()V") {
                return OnEndChangesMethodVisitor(api, superMv)
            } else if (name == "insertTopDown" && descriptor == "(ILandroidx/compose/ui/node/LayoutNode;)V") {
                return InsertTopDownMethodVisitor(api, superMv)
            } else if (name == "insertBottomUp" && descriptor == "(ILandroidx/compose/ui/node/LayoutNode;)V") {
                return InsertBottomUpMethodVisitor(api, superMv)
            } else if (name == "remove" && descriptor == "(II)V") {
                return RemoveMethodVisitor(api, superMv)
            } else if (name == "move" && descriptor == "(III)V") {
                return MoveMethodVisitor(api, superMv)
            } else if (name == "onClear" && descriptor == "()V") {
                return OnClearMethodVisitor(api, superMv)
            }
            return superMv
        }

        override fun visitEnd() {
            visitOnBeginChanges()
            visitDown()
            visitUp()
            super.visitEnd()
        }

        private fun visitOnBeginChanges() {
            visitMethod(Opcodes.ACC_PUBLIC, "onBeginChanges", "()V", null, null).run {
                visitCode()
                val label0 = Label()
                visitLabel(label0)
                visitLineNumber(15, label0)
                visitVarInsn(Opcodes.ALOAD, 0)
                visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "androidx/compose/runtime/AbstractApplier",
                    "onBeginChanges",
                    "()V",
                    false
                )
                val label1 = Label()
                visitLabel(label1)
                visitLineNumber(16, label1)
                visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "androidx/compose/runtime/UiApplierListener",
                    "INSTANCE",
                    "Landroidx/compose/runtime/UiApplierListener;"
                )
                visitVarInsn(Opcodes.ALOAD, 0)
                visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
                visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "androidx/compose/runtime/UiApplierListener",
                    "onBeginChanges",
                    "(Landroidx/compose/runtime/AbstractApplier;)V",
                    false
                )
                val label2 = Label()
                visitLabel(label2)
                visitLineNumber(17, label2)
                visitInsn(Opcodes.RETURN)
                val label3 = Label()
                visitLabel(label3)
                visitLocalVariable(
                    "this",
                    "Landroidx/compose/ui/node/UiApplier;",
                    null,
                    label0,
                    label3,
                    0
                )
                visitMaxs(2, 1)
                visitEnd()
            }
        }

        private fun visitDown() {
            visitMethod(
                Opcodes.ACC_PUBLIC,
                "down",
                "(Landroidx/compose/ui/node/LayoutNode;)V",
                null,
                null
            ).run {
                visitAnnotableParameterCount(1, false)
                visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
                    .visitEnd()
                visitCode()
                val label0 = Label()
                visitLabel(label0)
                visitVarInsn(Opcodes.ALOAD, 1)
                visitLdcInsn("node")
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "kotlin/jvm/internal/Intrinsics",
                    "checkNotNullParameter",
                    "(Ljava/lang/Object;Ljava/lang/String;)V",
                    false
                )
                val label1 = Label()
                visitLabel(label1)
                visitLineNumber(51, label1)
                visitVarInsn(Opcodes.ALOAD, 0)
                visitVarInsn(Opcodes.ALOAD, 1)
                visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "androidx/compose/runtime/AbstractApplier",
                    "down",
                    "(Ljava/lang/Object;)V",
                    false
                )
                val label2 = Label()
                visitLabel(label2)
                visitLineNumber(52, label2)
                visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "androidx/compose/runtime/UiApplierListener",
                    "INSTANCE",
                    "Landroidx/compose/runtime/UiApplierListener;"
                )
                visitVarInsn(Opcodes.ALOAD, 0)
                visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
                visitVarInsn(Opcodes.ALOAD, 1)
                visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "androidx/compose/runtime/UiApplierListener",
                    "down",
                    "(Landroidx/compose/runtime/AbstractApplier;Landroidx/compose/ui/layout/LayoutInfo;)V",
                    false
                )
                val label3 = Label()
                visitLabel(label3)
                visitLineNumber(53, label3)
                visitInsn(Opcodes.RETURN)
                val label4 = Label()
                visitLabel(label4)
                visitLocalVariable(
                    "this",
                    "Landroidx/compose/ui/node/UiApplier;",
                    null,
                    label0,
                    label4,
                    0
                )
                visitLocalVariable(
                    "node",
                    "Landroidx/compose/ui/node/LayoutNode;",
                    null,
                    label0,
                    label4,
                    1
                )
                visitMaxs(3, 2)
                visitEnd()
            }
        }

        private fun visitUp() {
            visitMethod(Opcodes.ACC_PUBLIC, "up", "()V", null, null).run {
                visitCode()
                val label0 = Label()
                visitLabel(label0)
                visitLineNumber(56, label0)
                visitVarInsn(Opcodes.ALOAD, 0)
                visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "androidx/compose/runtime/AbstractApplier",
                    "up",
                    "()V",
                    false
                )
                val label1 = Label()
                visitLabel(label1)
                visitLineNumber(57, label1)
                visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "androidx/compose/runtime/UiApplierListener",
                    "INSTANCE",
                    "Landroidx/compose/runtime/UiApplierListener;"
                )
                visitVarInsn(Opcodes.ALOAD, 0)
                visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
                visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "androidx/compose/runtime/UiApplierListener",
                    "up",
                    "(Landroidx/compose/runtime/AbstractApplier;)V",
                    false
                )
                val label2 = Label()
                visitLabel(label2)
                visitLineNumber(58, label2)
                visitInsn(Opcodes.RETURN)
                val label3 = Label()
                visitLabel(label3)
                visitLocalVariable(
                    "this",
                    "Landroidx/compose/ui/node/UiApplier;",
                    null,
                    label0,
                    label3,
                    0
                )
                visitMaxs(2, 1)
                visitEnd()
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

    private class OnEndChangesMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitFieldInsn(
                Opcodes.GETSTATIC,
                "androidx/compose/runtime/UiApplierListener",
                "INSTANCE",
                "Landroidx/compose/runtime/UiApplierListener;"
            )
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "androidx/compose/runtime/UiApplierListener",
                "onEndChanges",
                "(Landroidx/compose/runtime/AbstractApplier;)V",
                false
            )
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 1, maxLocals)
        }
    }

    private class InsertTopDownMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitFieldInsn(
                Opcodes.GETSTATIC,
                "androidx/compose/runtime/UiApplierListener",
                "INSTANCE",
                "Landroidx/compose/runtime/UiApplierListener;"
            )
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
            super.visitVarInsn(Opcodes.ILOAD, 1)
            super.visitVarInsn(Opcodes.ALOAD, 2)
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "androidx/compose/runtime/UiApplierListener",
                "insertTopDown",
                "(Landroidx/compose/runtime/AbstractApplier;ILandroidx/compose/ui/layout/LayoutInfo;)V",
                false
            )
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 2, maxLocals)
        }
    }

    private class InsertBottomUpMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitFieldInsn(
                Opcodes.GETSTATIC,
                "androidx/compose/runtime/UiApplierListener",
                "INSTANCE",
                "Landroidx/compose/runtime/UiApplierListener;"
            )
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
            super.visitVarInsn(Opcodes.ILOAD, 1)
            super.visitVarInsn(Opcodes.ALOAD, 2)
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "androidx/compose/runtime/UiApplierListener",
                "insertBottomUp",
                "(Landroidx/compose/runtime/AbstractApplier;ILandroidx/compose/ui/layout/LayoutInfo;)V",
                false
            )
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 1, maxLocals)
        }
    }

    private class RemoveMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitFieldInsn(
                Opcodes.GETSTATIC,
                "androidx/compose/runtime/UiApplierListener",
                "INSTANCE",
                "Landroidx/compose/runtime/UiApplierListener;"
            )
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
            super.visitVarInsn(Opcodes.ILOAD, 1)
            super.visitVarInsn(Opcodes.ILOAD, 2)
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "androidx/compose/runtime/UiApplierListener",
                "remove",
                "(Landroidx/compose/runtime/AbstractApplier;II)V",
                false
            )
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 1, maxLocals)
        }
    }

    private class MoveMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitFieldInsn(
                Opcodes.GETSTATIC,
                "androidx/compose/runtime/UiApplierListener",
                "INSTANCE",
                "Landroidx/compose/runtime/UiApplierListener;"
            )
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
            super.visitVarInsn(Opcodes.ILOAD, 1)
            super.visitVarInsn(Opcodes.ILOAD, 2)
            super.visitVarInsn(Opcodes.ILOAD, 3)
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "androidx/compose/runtime/UiApplierListener",
                "move",
                "(Landroidx/compose/runtime/AbstractApplier;III)V",
                false
            )
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 1, maxLocals)
        }
    }

    private class OnClearMethodVisitor(api: Int, methodVisitor: MethodVisitor) :
        BaseMethodVisitor(api, methodVisitor) {

        override fun doVisitInsnReturn() {
            super.visitFieldInsn(
                Opcodes.GETSTATIC,
                "androidx/compose/runtime/UiApplierListener",
                "INSTANCE",
                "Landroidx/compose/runtime/UiApplierListener;"
            )
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitTypeInsn(Opcodes.CHECKCAST, "androidx/compose/runtime/AbstractApplier")
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "androidx/compose/runtime/UiApplierListener",
                "onClear",
                "(Landroidx/compose/runtime/AbstractApplier;)V",
                false
            )
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 1, maxLocals)
        }
    }
}