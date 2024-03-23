package com.hym.composetrack

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

private const val CLICKABLE = "androidx.compose.foundation.gestures.TapGestureDetectorKt"
const val CLICKABLE_CLASS = "androidx/compose/foundation/gestures/TapGestureDetectorKt.class"

val ClickableMethodList = listOf(
    ModifyClickable.DETECT_TAP_GESTURES,
    ModifyClickable.DETECT_TAP_GESTURES_DEFAULT,
    ModifyClickable.ORIGINAL_DETECT_TAP_GESTURES,
    ModifyClickable.ORIGINAL_DETECT_TAP_GESTURES_DEFAULT,
    ModifyClickable.DETECT_TAP_AND_PRESS,
    ModifyClickable.DETECT_TAP_AND_PRESS_DEFAULT,
    ModifyClickable.ORIGINAL_DETECT_TAP_AND_PRESS,
    ModifyClickable.ORIGINAL_DETECT_TAP_AND_PRESS_DEFAULT
)

abstract class ModifyClickableClassVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ModifyClickable.createClassVisitor(
            instrumentationContext.apiVersion.get(),
            nextClassVisitor
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className == CLICKABLE
    }
}
