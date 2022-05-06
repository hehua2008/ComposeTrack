package com.hym.composetrack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.layout.layoutId

/**
 * @author ganmin.he
 * @date 2021/12/1
 */
@Immutable
open class Identity internal constructor(val name: String) {
    final override fun hashCode(): Int = name.hashCode()

    final override fun equals(other: Any?): Boolean = (this === other)

    override fun toString(): String = "Identity(name=$name)"
}

@Immutable
class NodeIdentity internal constructor(
    name: String,
    private val callback: OnAttachOnDetachCallback? = null
) : Identity(name) {
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

    override fun toString(): String = "NodeIdentity(name=$name)"
}

val NodeIdentity.path: String?
    @Throws(IllegalStateException::class)
    get() = node?.identityPath

@Stable
fun Modifier.identity(name: String) = this.layoutId(Identity(name))

@Stable
fun Modifier.identity(identity: NodeIdentity) = this.layoutId(identity)

@Composable
fun rememberIdentity(name: String, callback: OnAttachOnDetachCallback? = null): NodeIdentity {
    return remember(name, callback) { NodeIdentity(name, callback) }
}
