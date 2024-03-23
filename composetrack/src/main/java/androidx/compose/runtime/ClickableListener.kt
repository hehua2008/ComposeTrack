package androidx.compose.runtime

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.node.LayoutNode
import com.hym.composetrack.ClickableCallback
import com.hym.composetrack.LayoutNodeInfo
import com.hym.composetrack.currentRootNode
import java.util.WeakHashMap

/**
 * @author hehua2008
 * @date 2022/1/12
 */
private val rootNodeCallbackMap = WeakHashMap<LayoutNodeInfo, ClickableCallback>()

private fun onClick(layoutNode: LayoutNode) {
    val layoutNodeInfo = LayoutNodeInfo(layoutNode)
    val ownerView = layoutNodeInfo.ownerView ?: return
    rootNodeCallbackMap.forEach { (root, callback) ->
        if (root.ownerView === ownerView) {
            callback.onClick(layoutNodeInfo)
            return
        }
    }
}

private fun onLongClick(layoutNode: LayoutNode) {
    val layoutNodeInfo = LayoutNodeInfo(layoutNode)
    val ownerView = layoutNodeInfo.ownerView ?: return
    rootNodeCallbackMap.forEach { (root, callback) ->
        if (root.ownerView === ownerView) {
            callback.onLongClick(layoutNodeInfo)
            return
        }
    }
}

private fun onDoubleClick(layoutNode: LayoutNode) {
    val layoutNodeInfo = LayoutNodeInfo(layoutNode)
    val ownerView = layoutNodeInfo.ownerView ?: return
    rootNodeCallbackMap.forEach { (root, callback) ->
        if (root.ownerView === ownerView) {
            callback.onDoubleClick(layoutNodeInfo)
            return
        }
    }
}

@Composable
fun SetClickableCallback(clickableCallback: ClickableCallback?) {
    clickableCallback ?: return
    val rootNode = currentRootNode

    DisposableEffect(rootNode, clickableCallback) {
        rootNodeCallbackMap[rootNode] = clickableCallback

        onDispose {
            rootNodeCallbackMap.remove(rootNode)
        }
    }
}

abstract class BaseClickableWrapper(
    protected val layoutNode: LayoutNode,
    protected val action: (Offset) -> Unit
) : (Offset) -> Unit

class OnClickWrapper(layoutNode: LayoutNode, onClick: (Offset) -> Unit) :
    BaseClickableWrapper(layoutNode, onClick) {
    override fun invoke(offset: Offset) {
        action(offset)
        onClick(layoutNode)
    }
}

class OnLongClickWrapper(layoutNode: LayoutNode, onLongClick: (Offset) -> Unit) :
    BaseClickableWrapper(layoutNode, onLongClick) {
    override fun invoke(offset: Offset) {
        action(offset)
        onLongClick(layoutNode)
    }
}

class OnDoubleClickWrapper(layoutNode: LayoutNode, onDoubleClick: (Offset) -> Unit) :
    BaseClickableWrapper(layoutNode, onDoubleClick) {
    override fun invoke(offset: Offset) {
        action(offset)
        onDoubleClick(layoutNode)
    }
}
