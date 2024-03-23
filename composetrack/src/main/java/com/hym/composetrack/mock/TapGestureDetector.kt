package com.hym.composetrack.mock

import android.util.Log
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.runtime.OnClickWrapper
import androidx.compose.runtime.OnDoubleClickWrapper
import androidx.compose.runtime.OnLongClickWrapper
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutNode

/**
 * @author hehua2008
 * @date 2024/3/23
 */
// androidx/compose/foundation/gestures/TapGestureDetectorKt
private val NoPressGesture: suspend PressGestureScope.(Offset) -> Unit = { }

internal suspend fun PointerInputScope.detectTapGestures(
    onDoubleTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onPress: suspend PressGestureScope.(Offset) -> Unit = NoPressGesture,
    onTap: ((Offset) -> Unit)? = null
) {
    val layoutNode = try {
        (this as? Modifier.Node)?.coordinator?.layoutNode
    } catch (e: Exception) {
        Log.w("TapGestureDetector", "Hook TapGestureDetector.detectTapGestures() failed", e)
        null
    }

    if (layoutNode == null) {
        detectTapGesturesOriginal(onDoubleTap, onLongPress, onPress, onTap)
    } else {
        val onDoubleTapWrapper = onDoubleTap?.let {
            OnDoubleClickWrapper(layoutNode, it)
        }
        val onLongPressWrapper = onLongPress?.let {
            OnLongClickWrapper(layoutNode, it)
        }
        val onTapWrapper = onTap?.let {
            OnClickWrapper(layoutNode, it)
        }
        detectTapGesturesOriginal(onDoubleTapWrapper, onLongPressWrapper, onPress, onTapWrapper)
    }
}

internal suspend fun PointerInputScope.detectTapGesturesOriginal(
    onDoubleTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onPress: suspend PressGestureScope.(Offset) -> Unit = NoPressGesture,
    onTap: ((Offset) -> Unit)? = null
) {
}

internal suspend fun PointerInputScope.detectTapAndPress(
    onPress: suspend PressGestureScope.(Offset) -> Unit = NoPressGesture,
    onTap: ((Offset) -> Unit)? = null
) {
    val layoutNode = try {
        (this as? Modifier.Node)?.coordinator?.layoutNode
    } catch (e: Exception) {
        Log.w("TapGestureDetector", "Hook TapGestureDetector.detectTapAndPress() failed", e)
        null
    }

    if (layoutNode == null) {
        detectTapAndPressOriginal(onPress, onTap)
    } else {
        val onTapWrapper = onTap?.let {
            OnClickWrapper(layoutNode, it)
        }
        detectTapAndPressOriginal(onPress, onTapWrapper)
    }
}

internal suspend fun PointerInputScope.detectTapAndPressOriginal(
    onPress: suspend PressGestureScope.(Offset) -> Unit = NoPressGesture,
    onTap: ((Offset) -> Unit)? = null
) {
}

// androidx/compose/ui/Modifier
internal interface Modifier {
    abstract class Node : DelegatableNode {
        internal var coordinator: NodeCoordinator? = null
            private set
    }
}

// androidx/compose/ui/node/NodeCoordinator
internal abstract class NodeCoordinator(
    override val layoutNode: LayoutNode
) : MeasureScopeWithLayoutNode

// androidx/compose/ui/node/MeasureScopeWithLayoutNode
internal interface MeasureScopeWithLayoutNode : MeasureScope {
    val layoutNode: LayoutNode
}

// "androidx/compose/ui/Modifier\$Node",
// "getCoordinator\$ui_release",
// "()Landroidx/compose/ui/node/NodeCoordinator;"
