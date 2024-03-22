package androidx.compose.runtime

import android.util.Log
import androidx.compose.ui.layout.LayoutInfo

/**
 * @author hehua2008
 * @date 2022/1/6
 */
object UiApplierListener {
    private const val TAG = "UiApplierListener"

    fun onBeginChanges(uiApplier: AbstractApplier<LayoutInfo>) {
        Log.w(TAG, "onBeginChanges")
    }

    fun onEndChanges(uiApplier: AbstractApplier<LayoutInfo>) {
        Log.w(TAG, "onEndChanges")
    }

    fun down(uiApplier: AbstractApplier<LayoutInfo>, node: LayoutInfo) {
        Log.w(TAG, "down")
    }

    fun up(uiApplier: AbstractApplier<LayoutInfo>) {
        Log.w(TAG, "up")
    }

    fun onClear(uiApplier: AbstractApplier<LayoutInfo>) {
        Log.w(TAG, "onClear")
    }

    fun insertBottomUp(
        uiApplier: AbstractApplier<LayoutInfo>,
        index: Int,
        instance: LayoutInfo
    ) {
        Log.w(TAG, "insertBottomUp")
    }

    fun insertTopDown(
        uiApplier: AbstractApplier<LayoutInfo>,
        index: Int,
        instance: LayoutInfo
    ) {
        Log.w(TAG, "insertTopDown")
    }

    fun move(uiApplier: AbstractApplier<LayoutInfo>, from: Int, to: Int, count: Int) {
        Log.w(TAG, "move")
    }

    fun remove(uiApplier: AbstractApplier<LayoutInfo>, index: Int, count: Int) {
        Log.w(TAG, "remove")
    }
}