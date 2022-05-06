package androidx.compose.runtime

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.hym.composetrack.ClickableCallback
import com.hym.composetrack.LayoutNodeInfo
import com.hym.composetrack.currentRootNode
import java.util.*

/**
 * @author ganmin.he
 * @date 2022/1/12
 */
private val rootNodeCallbackMap = WeakHashMap<LayoutNodeInfo, ClickableCallback>()

private fun onClick(modifier: Modifier) {
    rootNodeCallbackMap.forEach { (root, callback) ->
        root.findLayoutNodeInfo(modifier)?.let {
            callback.onClick(it)
            return
        }
    }
}

private fun onLongClick(modifier: Modifier) {
    rootNodeCallbackMap.forEach { (root, callback) ->
        root.findLayoutNodeInfo(modifier)?.let {
            callback.onLongClick(it)
            return
        }
    }
}

private fun onDoubleClick(modifier: Modifier) {
    rootNodeCallbackMap.forEach { (root, callback) ->
        root.findLayoutNodeInfo(modifier)?.let {
            callback.onDoubleClick(it)
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
    protected val modifier: Modifier,
    protected val action: (Offset) -> Unit
) : (Offset) -> Unit

class OnClickWrapper(modifier: Modifier, onClick: (Offset) -> Unit) :
    BaseClickableWrapper(modifier, onClick) {
    override fun invoke(offset: Offset) {
        action(offset)
        onClick(modifier)
    }
}

class OnLongClickWrapper(modifier: Modifier, onLongClick: (Offset) -> Unit) :
    BaseClickableWrapper(modifier, onLongClick) {
    override fun invoke(offset: Offset) {
        action(offset)
        onLongClick(modifier)
    }
}

class OnDoubleClickWrapper(modifier: Modifier, onDoubleClick: (Offset) -> Unit) :
    BaseClickableWrapper(modifier, onDoubleClick) {
    override fun invoke(offset: Offset) {
        action(offset)
        onDoubleClick(modifier)
    }
}
