package com.hym.composetrack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.LayoutInfo

/**
 * @author hehua2008
 * @date 2024/3/22
 */
@Immutable
open class TrackId internal constructor(val name: String) {
    final override fun hashCode(): Int {
        return name.hashCode()
    }

    final override fun equals(other: Any?): Boolean {
        return (this === other)
    }

    override fun toString(): String {
        return "TrackId(name=$name)"
    }
}

@Immutable
class NodeTrackId internal constructor(
    name: String,
    private val callback: OnAttachOnDetachCallback? = null
) : TrackId(name) {
    var node: LayoutNodeInfo? = null
        private set

    fun onAttach(node: LayoutInfo) {
        this.node = LayoutNodeInfo(node)
        callback?.onAttach(this)
    }

    fun onDetach() {
        node = null
        callback?.onDetach(this)
    }

    override fun toString(): String {
        return "NodeTrackId(name=$name)"
    }
}

val NodeTrackId.path: String?
    @Throws(IllegalStateException::class)
    get() = node?.trackIdPath

@Composable
fun rememberTrackId(name: String, callback: OnAttachOnDetachCallback? = null): NodeTrackId {
    return remember(name, callback) { NodeTrackId(name, callback) }
}
