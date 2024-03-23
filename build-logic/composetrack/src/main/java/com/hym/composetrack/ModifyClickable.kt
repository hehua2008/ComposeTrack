package com.hym.composetrack

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_FINAL
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.Opcodes.ACC_SYNTHETIC
import org.objectweb.asm.Opcodes.ACONST_NULL
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.Opcodes.ASTORE
import org.objectweb.asm.Opcodes.BIPUSH
import org.objectweb.asm.Opcodes.CHECKCAST
import org.objectweb.asm.Opcodes.DUP
import org.objectweb.asm.Opcodes.F_FULL
import org.objectweb.asm.Opcodes.F_SAME
import org.objectweb.asm.Opcodes.F_SAME1
import org.objectweb.asm.Opcodes.GETSTATIC
import org.objectweb.asm.Opcodes.GOTO
import org.objectweb.asm.Opcodes.IAND
import org.objectweb.asm.Opcodes.ICONST_0
import org.objectweb.asm.Opcodes.ICONST_1
import org.objectweb.asm.Opcodes.ICONST_2
import org.objectweb.asm.Opcodes.ICONST_4
import org.objectweb.asm.Opcodes.IFEQ
import org.objectweb.asm.Opcodes.IFNONNULL
import org.objectweb.asm.Opcodes.IFNULL
import org.objectweb.asm.Opcodes.IF_ACMPNE
import org.objectweb.asm.Opcodes.ILOAD
import org.objectweb.asm.Opcodes.INSTANCEOF
import org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.Opcodes.ISTORE
import org.objectweb.asm.Opcodes.NEW
import org.objectweb.asm.Opcodes.NOP
import org.objectweb.asm.Opcodes.POP
import java.io.InputStream

/**
 * @author hehua2008
 * @date 2022/1/7
 */
object ModifyClickable {
    const val DETECT_TAP_GESTURES = "detectTapGestures"
    const val DETECT_TAP_GESTURES_DEFAULT = "detectTapGestures\$default"
    const val ORIGINAL_DETECT_TAP_GESTURES = "detectTapGesturesOriginal"
    const val ORIGINAL_DETECT_TAP_GESTURES_DEFAULT = "detectTapGesturesOriginal\$default"

    const val DETECT_TAP_AND_PRESS = "detectTapAndPress"
    const val DETECT_TAP_AND_PRESS_DEFAULT = "detectTapAndPress\$default"
    const val ORIGINAL_DETECT_TAP_AND_PRESS = "detectTapAndPressOriginal"
    const val ORIGINAL_DETECT_TAP_AND_PRESS_DEFAULT = "detectTapAndPressOriginal\$default"

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
            return when {
                name?.startsWith(DETECT_TAP_GESTURES) == true && descriptor == "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;" -> {
                    super.visitMethod(
                        access,
                        ORIGINAL_DETECT_TAP_GESTURES,
                        descriptor,
                        signature,
                        exceptions
                    )
                }

                name?.startsWith(DETECT_TAP_GESTURES_DEFAULT) == true && descriptor == "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;ILjava/lang/Object;)Ljava/lang/Object;" -> {
                    super.visitMethod(
                        access,
                        ORIGINAL_DETECT_TAP_GESTURES_DEFAULT,
                        descriptor,
                        signature,
                        exceptions
                    )
                }

                name?.startsWith(DETECT_TAP_AND_PRESS) == true && descriptor == "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;" -> {
                    super.visitMethod(
                        access,
                        ORIGINAL_DETECT_TAP_AND_PRESS,
                        descriptor,
                        signature,
                        exceptions
                    )
                }

                name?.startsWith(DETECT_TAP_AND_PRESS_DEFAULT) == true && descriptor == "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;ILjava/lang/Object;)Ljava/lang/Object;" -> {
                    super.visitMethod(
                        access,
                        ORIGINAL_DETECT_TAP_AND_PRESS_DEFAULT,
                        descriptor,
                        signature,
                        exceptions
                    )
                }

                else -> super.visitMethod(access, name, descriptor, signature, exceptions)
            }
        }

        override fun visitEnd() {
            addDetectTapGesturesMethod()
            addDetectTapAndPressMethod()
            super.visitEnd()
        }

        private fun addDetectTapGesturesMethod() {
            val classWriter = cv

            run {
                val methodVisitor = classWriter.visitMethod(
                    ACC_PUBLIC or ACC_FINAL or ACC_STATIC,
                    DETECT_TAP_GESTURES,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1<-Landroidx/compose/ui/geometry/Offset;Lkotlin/Unit;>;Lkotlin/jvm/functions/Function1<-Landroidx/compose/ui/geometry/Offset;Lkotlin/Unit;>;Lkotlin/jvm/functions/Function3<-Landroidx/compose/foundation/gestures/PressGestureScope;-Landroidx/compose/ui/geometry/Offset;-Lkotlin/coroutines/Continuation<-Lkotlin/Unit;>;+Ljava/lang/Object;>;Lkotlin/jvm/functions/Function1<-Landroidx/compose/ui/geometry/Offset;Lkotlin/Unit;>;Lkotlin/coroutines/Continuation<-Lkotlin/Unit;>;)Ljava/lang/Object;",
                    null
                )

                methodVisitor.visitAnnotation("Lorg/jetbrains/annotations/Nullable;", false)
                    .visitEnd()

                methodVisitor.visitAnnotableParameterCount(6, false)

                methodVisitor.visitParameterAnnotation(
                    0,
                    "Lorg/jetbrains/annotations/NotNull;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    1,
                    "Lorg/jetbrains/annotations/Nullable;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    2,
                    "Lorg/jetbrains/annotations/Nullable;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    3,
                    "Lorg/jetbrains/annotations/NotNull;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    4,
                    "Lorg/jetbrains/annotations/Nullable;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    5,
                    "Lorg/jetbrains/annotations/NotNull;",
                    false
                ).visitEnd()

                methodVisitor.visitCode()
                val label0 = Label()
                val label1 = Label()
                val label2 = Label()
                methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception")
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(23, label0)
                methodVisitor.visitInsn(NOP)
                val label3 = Label()
                methodVisitor.visitLabel(label3)
                methodVisitor.visitLineNumber(24, label3)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitTypeInsn(INSTANCEOF, "androidx/compose/ui/Modifier\$Node")
                val label4 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label4)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitTypeInsn(CHECKCAST, "androidx/compose/ui/Modifier\$Node")
                val label5 = Label()
                methodVisitor.visitJumpInsn(GOTO, label5)
                methodVisitor.visitLabel(label4)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label5)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/ui/Modifier\$Node")
                )
                methodVisitor.visitInsn(DUP)
                val label6 = Label()
                methodVisitor.visitJumpInsn(IFNULL, label6)
                methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "androidx/compose/ui/Modifier\$Node",
                    "getCoordinator\$ui_release",
                    "()Landroidx/compose/ui/node/NodeCoordinator;",
                    false
                )
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitJumpInsn(IFNULL, label6)
                methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "androidx/compose/ui/node/NodeCoordinator",
                    "getLayoutNode",
                    "()Landroidx/compose/ui/node/LayoutNode;",
                    false
                )
                val label7 = Label()
                methodVisitor.visitJumpInsn(GOTO, label7)
                methodVisitor.visitLabel(label6)
                methodVisitor.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>("java/lang/Object"))
                methodVisitor.visitInsn(POP)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label7)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/ui/node/LayoutNode")
                )
                methodVisitor.visitVarInsn(ASTORE, 7)
                methodVisitor.visitLabel(label1)
                val label8 = Label()
                methodVisitor.visitJumpInsn(GOTO, label8)
                methodVisitor.visitLabel(label2)
                methodVisitor.visitLineNumber(25, label2)
                methodVisitor.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>("java/lang/Exception"))
                methodVisitor.visitVarInsn(ASTORE, 8)
                val label9 = Label()
                methodVisitor.visitLabel(label9)
                methodVisitor.visitLineNumber(26, label9)
                methodVisitor.visitLdcInsn("TapGestureDetector")
                methodVisitor.visitLdcInsn("Hook TapGestureDetector.detectTapGestures() failed")
                methodVisitor.visitVarInsn(ALOAD, 8)
                methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Throwable")
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "android/util/Log",
                    "w",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I",
                    false
                )
                methodVisitor.visitInsn(POP)
                val label10 = Label()
                methodVisitor.visitLabel(label10)
                methodVisitor.visitLineNumber(27, label10)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitVarInsn(ASTORE, 7)
                methodVisitor.visitLabel(label8)
                methodVisitor.visitLineNumber(23, label8)
                methodVisitor.visitFrame(
                    Opcodes.F_APPEND,
                    2,
                    arrayOf<Any>(Opcodes.TOP, "androidx/compose/ui/node/LayoutNode"),
                    0,
                    null
                )
                methodVisitor.visitVarInsn(ALOAD, 7)
                methodVisitor.visitVarInsn(ASTORE, 6)
                val label11 = Label()
                methodVisitor.visitLabel(label11)
                methodVisitor.visitLineNumber(30, label11)
                methodVisitor.visitVarInsn(ALOAD, 6)
                val label12 = Label()
                methodVisitor.visitJumpInsn(IFNONNULL, label12)
                val label13 = Label()
                methodVisitor.visitLabel(label13)
                methodVisitor.visitLineNumber(31, label13)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitVarInsn(ALOAD, 3)
                methodVisitor.visitVarInsn(ALOAD, 4)
                methodVisitor.visitVarInsn(ALOAD, 5)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    ORIGINAL_DETECT_TAP_GESTURES,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    false
                )
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "kotlin/coroutines/intrinsics/IntrinsicsKt",
                    "getCOROUTINE_SUSPENDED",
                    "()Ljava/lang/Object;",
                    false
                )
                val label14 = Label()
                methodVisitor.visitJumpInsn(IF_ACMPNE, label14)
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitLabel(label14)
                methodVisitor.visitFrame(
                    F_FULL,
                    8,
                    arrayOf<Any>(
                        "androidx/compose/ui/input/pointer/PointerInputScope",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function3",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/coroutines/Continuation",
                        "androidx/compose/ui/node/LayoutNode",
                        "androidx/compose/ui/node/LayoutNode"
                    ),
                    1,
                    arrayOf<Any>("java/lang/Object")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/Unit", "INSTANCE", "Lkotlin/Unit;")
                val label15 = Label()
                methodVisitor.visitLabel(label15)
                methodVisitor.visitLineNumber(44, label15)
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitLabel(label12)
                methodVisitor.visitLineNumber(33, label12)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitInsn(DUP)
                val label16 = Label()
                methodVisitor.visitJumpInsn(IFNULL, label16)
                methodVisitor.visitVarInsn(ASTORE, 9)
                val label17 = Label()
                methodVisitor.visitLabel(label17)
                methodVisitor.visitInsn(ICONST_0)
                methodVisitor.visitVarInsn(ISTORE, 10)
                val label18 = Label()
                methodVisitor.visitLabel(label18)
                methodVisitor.visitLineNumber(34, label18)
                methodVisitor.visitTypeInsn(NEW, "androidx/compose/runtime/OnDoubleClickWrapper")
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitVarInsn(ALOAD, 6)
                methodVisitor.visitVarInsn(ALOAD, 9)
                methodVisitor.visitMethodInsn(
                    INVOKESPECIAL,
                    "androidx/compose/runtime/OnDoubleClickWrapper",
                    "<init>",
                    "(Landroidx/compose/ui/node/LayoutNode;Lkotlin/jvm/functions/Function1;)V",
                    false
                )
                val label19 = Label()
                methodVisitor.visitLabel(label19)
                methodVisitor.visitLineNumber(33, label19)
                val label20 = Label()
                methodVisitor.visitJumpInsn(GOTO, label20)
                methodVisitor.visitLabel(label16)
                methodVisitor.visitLineNumber(33, label16)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("kotlin/jvm/functions/Function1")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label20)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/runtime/OnDoubleClickWrapper")
                )
                methodVisitor.visitVarInsn(ASTORE, 7)
                val label21 = Label()
                methodVisitor.visitLabel(label21)
                methodVisitor.visitLineNumber(36, label21)
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitInsn(DUP)
                val label22 = Label()
                methodVisitor.visitJumpInsn(IFNULL, label22)
                methodVisitor.visitVarInsn(ASTORE, 10)
                val label23 = Label()
                methodVisitor.visitLabel(label23)
                methodVisitor.visitInsn(ICONST_0)
                methodVisitor.visitVarInsn(ISTORE, 11)
                val label24 = Label()
                methodVisitor.visitLabel(label24)
                methodVisitor.visitLineNumber(37, label24)
                methodVisitor.visitTypeInsn(NEW, "androidx/compose/runtime/OnLongClickWrapper")
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitVarInsn(ALOAD, 6)
                methodVisitor.visitVarInsn(ALOAD, 10)
                methodVisitor.visitMethodInsn(
                    INVOKESPECIAL,
                    "androidx/compose/runtime/OnLongClickWrapper",
                    "<init>",
                    "(Landroidx/compose/ui/node/LayoutNode;Lkotlin/jvm/functions/Function1;)V",
                    false
                )
                val label25 = Label()
                methodVisitor.visitLabel(label25)
                methodVisitor.visitLineNumber(36, label25)
                val label26 = Label()
                methodVisitor.visitJumpInsn(GOTO, label26)
                methodVisitor.visitLabel(label22)
                methodVisitor.visitLineNumber(36, label22)
                methodVisitor.visitFrame(
                    F_FULL,
                    8,
                    arrayOf<Any>(
                        "androidx/compose/ui/input/pointer/PointerInputScope",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function3",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/coroutines/Continuation",
                        "androidx/compose/ui/node/LayoutNode",
                        "androidx/compose/runtime/OnDoubleClickWrapper"
                    ),
                    1,
                    arrayOf<Any>("kotlin/jvm/functions/Function1")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label26)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/runtime/OnLongClickWrapper")
                )
                methodVisitor.visitVarInsn(ASTORE, 8)
                val label27 = Label()
                methodVisitor.visitLabel(label27)
                methodVisitor.visitLineNumber(39, label27)
                methodVisitor.visitVarInsn(ALOAD, 4)
                methodVisitor.visitInsn(DUP)
                val label28 = Label()
                methodVisitor.visitJumpInsn(IFNULL, label28)
                methodVisitor.visitVarInsn(ASTORE, 11)
                val label29 = Label()
                methodVisitor.visitLabel(label29)
                methodVisitor.visitInsn(ICONST_0)
                methodVisitor.visitVarInsn(ISTORE, 12)
                val label30 = Label()
                methodVisitor.visitLabel(label30)
                methodVisitor.visitLineNumber(40, label30)
                methodVisitor.visitTypeInsn(NEW, "androidx/compose/runtime/OnClickWrapper")
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitVarInsn(ALOAD, 6)
                methodVisitor.visitVarInsn(ALOAD, 11)
                methodVisitor.visitMethodInsn(
                    INVOKESPECIAL,
                    "androidx/compose/runtime/OnClickWrapper",
                    "<init>",
                    "(Landroidx/compose/ui/node/LayoutNode;Lkotlin/jvm/functions/Function1;)V",
                    false
                )
                val label31 = Label()
                methodVisitor.visitLabel(label31)
                methodVisitor.visitLineNumber(39, label31)
                val label32 = Label()
                methodVisitor.visitJumpInsn(GOTO, label32)
                methodVisitor.visitLabel(label28)
                methodVisitor.visitLineNumber(39, label28)
                methodVisitor.visitFrame(
                    F_FULL,
                    9,
                    arrayOf<Any>(
                        "androidx/compose/ui/input/pointer/PointerInputScope",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function3",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/coroutines/Continuation",
                        "androidx/compose/ui/node/LayoutNode",
                        "androidx/compose/runtime/OnDoubleClickWrapper",
                        "androidx/compose/runtime/OnLongClickWrapper"
                    ),
                    1,
                    arrayOf<Any>("kotlin/jvm/functions/Function1")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label32)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/runtime/OnClickWrapper")
                )
                methodVisitor.visitVarInsn(ASTORE, 9)
                val label33 = Label()
                methodVisitor.visitLabel(label33)
                methodVisitor.visitLineNumber(42, label33)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ALOAD, 7)
                methodVisitor.visitTypeInsn(CHECKCAST, "kotlin/jvm/functions/Function1")
                methodVisitor.visitVarInsn(ALOAD, 8)
                methodVisitor.visitTypeInsn(CHECKCAST, "kotlin/jvm/functions/Function1")
                methodVisitor.visitVarInsn(ALOAD, 3)
                methodVisitor.visitVarInsn(ALOAD, 9)
                methodVisitor.visitTypeInsn(CHECKCAST, "kotlin/jvm/functions/Function1")
                methodVisitor.visitVarInsn(ALOAD, 5)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    ORIGINAL_DETECT_TAP_GESTURES,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    false
                )
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "kotlin/coroutines/intrinsics/IntrinsicsKt",
                    "getCOROUTINE_SUSPENDED",
                    "()Ljava/lang/Object;",
                    false
                )
                val label34 = Label()
                methodVisitor.visitJumpInsn(IF_ACMPNE, label34)
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitLabel(label34)
                methodVisitor.visitFrame(
                    F_FULL,
                    10,
                    arrayOf<Any>(
                        "androidx/compose/ui/input/pointer/PointerInputScope",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/jvm/functions/Function3",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/coroutines/Continuation",
                        "androidx/compose/ui/node/LayoutNode",
                        "androidx/compose/runtime/OnDoubleClickWrapper",
                        "androidx/compose/runtime/OnLongClickWrapper",
                        "androidx/compose/runtime/OnClickWrapper"
                    ),
                    1,
                    arrayOf<Any>("java/lang/Object")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/Unit", "INSTANCE", "Lkotlin/Unit;")
                val label35 = Label()
                methodVisitor.visitLabel(label35)
                methodVisitor.visitLineNumber(44, label35)
                methodVisitor.visitInsn(ARETURN)
                val label36 = Label()
                methodVisitor.visitLabel(label36)
                methodVisitor.visitLocalVariable(
                    "e",
                    "Ljava/lang/Exception;",
                    null,
                    label9,
                    label8,
                    8
                )
                methodVisitor.visitLocalVariable(
                    "\$i\$a$-let-TapGestureDetectorKt\$detectTapGestures\$onDoubleTapWrapper$1",
                    "I",
                    null,
                    label18,
                    label19,
                    10
                )
                methodVisitor.visitLocalVariable(
                    "it",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label17,
                    label19,
                    9
                )
                methodVisitor.visitLocalVariable(
                    "\$i\$a$-let-TapGestureDetectorKt\$detectTapGestures\$onLongPressWrapper$1",
                    "I",
                    null,
                    label24,
                    label25,
                    11
                )
                methodVisitor.visitLocalVariable(
                    "it",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label23,
                    label25,
                    10
                )
                methodVisitor.visitLocalVariable(
                    "\$i\$a$-let-TapGestureDetectorKt\$detectTapGestures\$onTapWrapper$1",
                    "I",
                    null,
                    label30,
                    label31,
                    12
                )
                methodVisitor.visitLocalVariable(
                    "it",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label29,
                    label31,
                    11
                )
                methodVisitor.visitLocalVariable(
                    "onDoubleTapWrapper",
                    "Landroidx/compose/runtime/OnDoubleClickWrapper;",
                    null,
                    label21,
                    label36,
                    7
                )
                methodVisitor.visitLocalVariable(
                    "onLongPressWrapper",
                    "Landroidx/compose/runtime/OnLongClickWrapper;",
                    null,
                    label27,
                    label36,
                    8
                )
                methodVisitor.visitLocalVariable(
                    "onTapWrapper",
                    "Landroidx/compose/runtime/OnClickWrapper;",
                    null,
                    label33,
                    label36,
                    9
                )
                methodVisitor.visitLocalVariable(
                    "layoutNode",
                    "Landroidx/compose/ui/node/LayoutNode;",
                    null,
                    label11,
                    label36,
                    6
                )
                methodVisitor.visitLocalVariable(
                    "\$this\$detectTapGestures",
                    "Landroidx/compose/ui/input/pointer/PointerInputScope;",
                    null,
                    label0,
                    label36,
                    0
                )
                methodVisitor.visitLocalVariable(
                    "onDoubleTap",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label0,
                    label36,
                    1
                )
                methodVisitor.visitLocalVariable(
                    "onLongPress",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label0,
                    label36,
                    2
                )
                methodVisitor.visitLocalVariable(
                    "onPress",
                    "Lkotlin/jvm/functions/Function3;",
                    null,
                    label0,
                    label36,
                    3
                )
                methodVisitor.visitLocalVariable(
                    "onTap",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label0,
                    label36,
                    4
                )
                methodVisitor.visitLocalVariable(
                    "\$completion",
                    "Lkotlin/coroutines/Continuation;",
                    null,
                    label0,
                    label36,
                    5
                )
                methodVisitor.visitMaxs(6, 13)
                methodVisitor.visitEnd()
            }

            run {
                val methodVisitor = classWriter.visitMethod(
                    ACC_PUBLIC or ACC_STATIC or ACC_SYNTHETIC,
                    DETECT_TAP_GESTURES_DEFAULT,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;ILjava/lang/Object;)Ljava/lang/Object;",
                    null,
                    null
                )
                methodVisitor.visitCode()
                val label0 = Label()
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(17, label0)
                methodVisitor.visitVarInsn(ILOAD, 6)
                methodVisitor.visitInsn(ICONST_1)
                methodVisitor.visitInsn(IAND)
                val label1 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label1)
                val label2 = Label()
                methodVisitor.visitLabel(label2)
                methodVisitor.visitLineNumber(18, label2)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitVarInsn(ASTORE, 1)
                methodVisitor.visitLabel(label1)
                methodVisitor.visitLineNumber(17, label1)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ILOAD, 6)
                methodVisitor.visitInsn(ICONST_2)
                methodVisitor.visitInsn(IAND)
                val label3 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label3)
                val label4 = Label()
                methodVisitor.visitLabel(label4)
                methodVisitor.visitLineNumber(19, label4)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitVarInsn(ASTORE, 2)
                methodVisitor.visitLabel(label3)
                methodVisitor.visitLineNumber(17, label3)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ILOAD, 6)
                methodVisitor.visitInsn(ICONST_4)
                methodVisitor.visitInsn(IAND)
                val label5 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label5)
                val label6 = Label()
                methodVisitor.visitLabel(label6)
                methodVisitor.visitLineNumber(20, label6)
                methodVisitor.visitFieldInsn(
                    GETSTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    "NoPressGesture",
                    "Lkotlin/jvm/functions/Function3;"
                )
                methodVisitor.visitVarInsn(ASTORE, 3)
                methodVisitor.visitLabel(label5)
                methodVisitor.visitLineNumber(17, label5)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ILOAD, 6)
                methodVisitor.visitIntInsn(BIPUSH, 8)
                methodVisitor.visitInsn(IAND)
                val label7 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label7)
                val label8 = Label()
                methodVisitor.visitLabel(label8)
                methodVisitor.visitLineNumber(21, label8)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitVarInsn(ASTORE, 4)
                methodVisitor.visitLabel(label7)
                methodVisitor.visitLineNumber(17, label7)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitVarInsn(ALOAD, 3)
                methodVisitor.visitVarInsn(ALOAD, 4)
                methodVisitor.visitVarInsn(ALOAD, 5)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    DETECT_TAP_GESTURES,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    false
                )
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitMaxs(6, 8)
                methodVisitor.visitEnd()
            }
        }

        private fun addDetectTapAndPressMethod() {
            val classWriter = cv

            run {
                val methodVisitor = classWriter.visitMethod(
                    ACC_PUBLIC or ACC_FINAL or ACC_STATIC,
                    DETECT_TAP_AND_PRESS,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3<-Landroidx/compose/foundation/gestures/PressGestureScope;-Landroidx/compose/ui/geometry/Offset;-Lkotlin/coroutines/Continuation<-Lkotlin/Unit;>;+Ljava/lang/Object;>;Lkotlin/jvm/functions/Function1<-Landroidx/compose/ui/geometry/Offset;Lkotlin/Unit;>;Lkotlin/coroutines/Continuation<-Lkotlin/Unit;>;)Ljava/lang/Object;",
                    null
                )

                methodVisitor.visitAnnotation("Lorg/jetbrains/annotations/Nullable;", false)
                    .visitEnd()

                methodVisitor.visitAnnotableParameterCount(4, false)

                methodVisitor.visitParameterAnnotation(
                    0,
                    "Lorg/jetbrains/annotations/NotNull;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    1,
                    "Lorg/jetbrains/annotations/NotNull;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    2,
                    "Lorg/jetbrains/annotations/Nullable;",
                    false
                ).visitEnd()

                methodVisitor.visitParameterAnnotation(
                    3,
                    "Lorg/jetbrains/annotations/NotNull;",
                    false
                ).visitEnd()

                methodVisitor.visitCode()
                val label0 = Label()
                val label1 = Label()
                val label2 = Label()
                methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception")
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(58, label0)
                methodVisitor.visitInsn(NOP)
                val label3 = Label()
                methodVisitor.visitLabel(label3)
                methodVisitor.visitLineNumber(59, label3)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitTypeInsn(INSTANCEOF, "androidx/compose/ui/Modifier\$Node")
                val label4 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label4)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitTypeInsn(CHECKCAST, "androidx/compose/ui/Modifier\$Node")
                val label5 = Label()
                methodVisitor.visitJumpInsn(GOTO, label5)
                methodVisitor.visitLabel(label4)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label5)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/ui/Modifier\$Node")
                )
                methodVisitor.visitInsn(DUP)
                val label6 = Label()
                methodVisitor.visitJumpInsn(IFNULL, label6)
                methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "androidx/compose/ui/Modifier\$Node",
                    "getCoordinator\$ui_release",
                    "()Landroidx/compose/ui/node/NodeCoordinator;",
                    false
                )
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitJumpInsn(IFNULL, label6)
                methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "androidx/compose/ui/node/NodeCoordinator",
                    "getLayoutNode",
                    "()Landroidx/compose/ui/node/LayoutNode;",
                    false
                )
                val label7 = Label()
                methodVisitor.visitJumpInsn(GOTO, label7)
                methodVisitor.visitLabel(label6)
                methodVisitor.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>("java/lang/Object"))
                methodVisitor.visitInsn(POP)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label7)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/ui/node/LayoutNode")
                )
                methodVisitor.visitVarInsn(ASTORE, 5)
                methodVisitor.visitLabel(label1)
                val label8 = Label()
                methodVisitor.visitJumpInsn(GOTO, label8)
                methodVisitor.visitLabel(label2)
                methodVisitor.visitLineNumber(60, label2)
                methodVisitor.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>("java/lang/Exception"))
                methodVisitor.visitVarInsn(ASTORE, 6)
                val label9 = Label()
                methodVisitor.visitLabel(label9)
                methodVisitor.visitLineNumber(61, label9)
                methodVisitor.visitLdcInsn("TapGestureDetector")
                methodVisitor.visitLdcInsn("Hook TapGestureDetector.detectTapAndPress() failed")
                methodVisitor.visitVarInsn(ALOAD, 6)
                methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Throwable")
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "android/util/Log",
                    "w",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I",
                    false
                )
                methodVisitor.visitInsn(POP)
                val label10 = Label()
                methodVisitor.visitLabel(label10)
                methodVisitor.visitLineNumber(62, label10)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitVarInsn(ASTORE, 5)
                methodVisitor.visitLabel(label8)
                methodVisitor.visitLineNumber(58, label8)
                methodVisitor.visitFrame(
                    Opcodes.F_APPEND,
                    2,
                    arrayOf<Any>(Opcodes.TOP, "androidx/compose/ui/node/LayoutNode"),
                    0,
                    null
                )
                methodVisitor.visitVarInsn(ALOAD, 5)
                methodVisitor.visitVarInsn(ASTORE, 4)
                val label11 = Label()
                methodVisitor.visitLabel(label11)
                methodVisitor.visitLineNumber(65, label11)
                methodVisitor.visitVarInsn(ALOAD, 4)
                val label12 = Label()
                methodVisitor.visitJumpInsn(IFNONNULL, label12)
                val label13 = Label()
                methodVisitor.visitLabel(label13)
                methodVisitor.visitLineNumber(66, label13)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitVarInsn(ALOAD, 3)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    ORIGINAL_DETECT_TAP_AND_PRESS,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    false
                )
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "kotlin/coroutines/intrinsics/IntrinsicsKt",
                    "getCOROUTINE_SUSPENDED",
                    "()Ljava/lang/Object;",
                    false
                )
                val label14 = Label()
                methodVisitor.visitJumpInsn(IF_ACMPNE, label14)
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitLabel(label14)
                methodVisitor.visitFrame(
                    F_FULL,
                    6,
                    arrayOf<Any>(
                        "androidx/compose/ui/input/pointer/PointerInputScope",
                        "kotlin/jvm/functions/Function3",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/coroutines/Continuation",
                        "androidx/compose/ui/node/LayoutNode",
                        "androidx/compose/ui/node/LayoutNode"
                    ),
                    1,
                    arrayOf<Any>("java/lang/Object")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/Unit", "INSTANCE", "Lkotlin/Unit;")
                val label15 = Label()
                methodVisitor.visitLabel(label15)
                methodVisitor.visitLineNumber(73, label15)
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitLabel(label12)
                methodVisitor.visitLineNumber(68, label12)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitInsn(DUP)
                val label16 = Label()
                methodVisitor.visitJumpInsn(IFNULL, label16)
                methodVisitor.visitVarInsn(ASTORE, 7)
                val label17 = Label()
                methodVisitor.visitLabel(label17)
                methodVisitor.visitInsn(ICONST_0)
                methodVisitor.visitVarInsn(ISTORE, 8)
                val label18 = Label()
                methodVisitor.visitLabel(label18)
                methodVisitor.visitLineNumber(69, label18)
                methodVisitor.visitTypeInsn(NEW, "androidx/compose/runtime/OnClickWrapper")
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitVarInsn(ALOAD, 4)
                methodVisitor.visitVarInsn(ALOAD, 7)
                methodVisitor.visitMethodInsn(
                    INVOKESPECIAL,
                    "androidx/compose/runtime/OnClickWrapper",
                    "<init>",
                    "(Landroidx/compose/ui/node/LayoutNode;Lkotlin/jvm/functions/Function1;)V",
                    false
                )
                val label19 = Label()
                methodVisitor.visitLabel(label19)
                methodVisitor.visitLineNumber(68, label19)
                val label20 = Label()
                methodVisitor.visitJumpInsn(GOTO, label20)
                methodVisitor.visitLabel(label16)
                methodVisitor.visitLineNumber(68, label16)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("kotlin/jvm/functions/Function1")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitLabel(label20)
                methodVisitor.visitFrame(
                    F_SAME1,
                    0,
                    null,
                    1,
                    arrayOf<Any>("androidx/compose/runtime/OnClickWrapper")
                )
                methodVisitor.visitVarInsn(ASTORE, 5)
                val label21 = Label()
                methodVisitor.visitLabel(label21)
                methodVisitor.visitLineNumber(71, label21)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitVarInsn(ALOAD, 5)
                methodVisitor.visitTypeInsn(CHECKCAST, "kotlin/jvm/functions/Function1")
                methodVisitor.visitVarInsn(ALOAD, 3)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    ORIGINAL_DETECT_TAP_AND_PRESS,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    false
                )
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "kotlin/coroutines/intrinsics/IntrinsicsKt",
                    "getCOROUTINE_SUSPENDED",
                    "()Ljava/lang/Object;",
                    false
                )
                val label22 = Label()
                methodVisitor.visitJumpInsn(IF_ACMPNE, label22)
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitLabel(label22)
                methodVisitor.visitFrame(
                    F_FULL,
                    6,
                    arrayOf<Any>(
                        "androidx/compose/ui/input/pointer/PointerInputScope",
                        "kotlin/jvm/functions/Function3",
                        "kotlin/jvm/functions/Function1",
                        "kotlin/coroutines/Continuation",
                        "androidx/compose/ui/node/LayoutNode",
                        "androidx/compose/runtime/OnClickWrapper"
                    ),
                    1,
                    arrayOf<Any>("java/lang/Object")
                )
                methodVisitor.visitInsn(POP)
                methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/Unit", "INSTANCE", "Lkotlin/Unit;")
                val label23 = Label()
                methodVisitor.visitLabel(label23)
                methodVisitor.visitLineNumber(73, label23)
                methodVisitor.visitInsn(ARETURN)
                val label24 = Label()
                methodVisitor.visitLabel(label24)
                methodVisitor.visitLocalVariable(
                    "e",
                    "Ljava/lang/Exception;",
                    null,
                    label9,
                    label8,
                    6
                )
                methodVisitor.visitLocalVariable(
                    "\$i\$a$-let-TapGestureDetectorKt\$detectTapAndPress\$onTapWrapper$1",
                    "I",
                    null,
                    label18,
                    label19,
                    8
                )
                methodVisitor.visitLocalVariable(
                    "it",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label17,
                    label19,
                    7
                )
                methodVisitor.visitLocalVariable(
                    "onTapWrapper",
                    "Landroidx/compose/runtime/OnClickWrapper;",
                    null,
                    label21,
                    label24,
                    5
                )
                methodVisitor.visitLocalVariable(
                    "layoutNode",
                    "Landroidx/compose/ui/node/LayoutNode;",
                    null,
                    label11,
                    label24,
                    4
                )
                methodVisitor.visitLocalVariable(
                    "\$this\$detectTapAndPress",
                    "Landroidx/compose/ui/input/pointer/PointerInputScope;",
                    null,
                    label0,
                    label24,
                    0
                )
                methodVisitor.visitLocalVariable(
                    "onPress",
                    "Lkotlin/jvm/functions/Function3;",
                    null,
                    label0,
                    label24,
                    1
                )
                methodVisitor.visitLocalVariable(
                    "onTap",
                    "Lkotlin/jvm/functions/Function1;",
                    null,
                    label0,
                    label24,
                    2
                )
                methodVisitor.visitLocalVariable(
                    "\$completion",
                    "Lkotlin/coroutines/Continuation;",
                    null,
                    label0,
                    label24,
                    3
                )
                methodVisitor.visitMaxs(4, 9)
                methodVisitor.visitEnd()
            }

            run {
                val methodVisitor = classWriter.visitMethod(
                    ACC_PUBLIC or ACC_STATIC or ACC_SYNTHETIC,
                    DETECT_TAP_AND_PRESS_DEFAULT,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;ILjava/lang/Object;)Ljava/lang/Object;",
                    null,
                    null
                )
                methodVisitor.visitCode()
                val label0 = Label()
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(54, label0)
                methodVisitor.visitVarInsn(ILOAD, 4)
                methodVisitor.visitInsn(ICONST_1)
                methodVisitor.visitInsn(IAND)
                val label1 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label1)
                val label2 = Label()
                methodVisitor.visitLabel(label2)
                methodVisitor.visitLineNumber(55, label2)
                methodVisitor.visitFieldInsn(
                    GETSTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    "NoPressGesture",
                    "Lkotlin/jvm/functions/Function3;"
                )
                methodVisitor.visitVarInsn(ASTORE, 1)
                methodVisitor.visitLabel(label1)
                methodVisitor.visitLineNumber(54, label1)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ILOAD, 4)
                methodVisitor.visitInsn(ICONST_2)
                methodVisitor.visitInsn(IAND)
                val label3 = Label()
                methodVisitor.visitJumpInsn(IFEQ, label3)
                val label4 = Label()
                methodVisitor.visitLabel(label4)
                methodVisitor.visitLineNumber(56, label4)
                methodVisitor.visitInsn(ACONST_NULL)
                methodVisitor.visitVarInsn(ASTORE, 2)
                methodVisitor.visitLabel(label3)
                methodVisitor.visitLineNumber(54, label3)
                methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitVarInsn(ALOAD, 3)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "androidx/compose/foundation/gestures/TapGestureDetectorKt",
                    DETECT_TAP_AND_PRESS,
                    "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                    false
                )
                methodVisitor.visitInsn(ARETURN)
                methodVisitor.visitMaxs(4, 6)
                methodVisitor.visitEnd()
            }
        }
    }
}
