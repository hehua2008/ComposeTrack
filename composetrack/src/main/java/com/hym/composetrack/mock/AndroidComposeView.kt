package com.hym.composetrack.mock

import android.content.Context
import android.view.ViewGroup
import androidx.compose.runtime.OnAttachOnDetachListener
import androidx.compose.ui.node.LayoutNode
import androidx.compose.ui.node.Owner

/**
 * @author hehua2008
 * @date 2024/3/23
 */
// androidx/compose/ui/platform/AndroidComposeView
internal class AndroidComposeView(context: Context) : ViewGroup(context), Owner {
    override fun onAttach(node: LayoutNode) {
        // ...
        OnAttachOnDetachListener.onAttach(this, node)
    }

    override fun onDetach(node: LayoutNode) {
        // ...
        OnAttachOnDetachListener.onDetach(this, node)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }
}
