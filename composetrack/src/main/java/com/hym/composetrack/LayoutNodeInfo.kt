package com.hym.composetrack

import android.view.View
import androidx.compose.runtime.ComposeNodeLifecycleCallback
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Remeasurement
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.LayoutNode
import java.lang.ref.WeakReference

/**
 * @author hehua2008
 * @date 2021/12/1
 */
class LayoutNodeInfo(node: LayoutInfo) {
    private val mNode: WeakReference<LayoutNode?>

    init {
        mNode = WeakReference(node as LayoutNode)
    }

    fun asComposeNodeLifecycleCallback(): ComposeNodeLifecycleCallback? {
        return mNode.get()
    }

    fun asRemeasurement(): Remeasurement? {
        return mNode.get()
    }

    fun asLayoutInfo(): LayoutInfo? {
        return mNode.get()
    }

    val coordinates: LayoutCoordinates?
        /**
         * @return Coordinates of just the contents of the LayoutNode, after being affected by all
         * modifiers.
         */
        get() {
            val node = mNode.get() ?: return null
            return node.coordinates
        }

    val boundsInWindow: Rect?
        /**
         * @return The boundaries of this layout relative to the window's origin.
         */
        get() {
            val node = mNode.get() ?: return null
            return node.coordinates.boundsInWindow()
        }

    val measurePolicy: MeasurePolicy?
        /**
         * @return Blocks that define the measurement and intrinsic measurement of the layout.
         */
        get() {
            val node = mNode.get() ?: return null
            return node.measurePolicy
        }

    var modifier: Modifier?
        /**
         * @return The Modifier currently applied to this node.
         */
        get() {
            val node = mNode.get() ?: return null
            return node.modifier
        }
        /**
         *  Apply the specified Modifier to this node.
         */
        set(value) {
            value ?: return
            val node = mNode.get() ?: return
            node.setModifier(value)
        }

    val owner: Any?
        /**
         * @return The view system Owner. This null until attach is called.
         */
        get() {
            val node = mNode.get() ?: return null
            return node.`owner$ui_release`
        }

    val ownerView: View?
        /**
         * @return The view system Owner. This null until attach is called.
         */
        get() {
            val node = mNode.get() ?: return null
            return LayoutNodeHelper.getOwnerView(node)
        }

    val recomposer: Recomposer?
        /**
         * @return The parent [Recomposer] for this point in the view hierarchy, or `null` if none can
         * be found.
         */
        get() {
            val node = mNode.get() ?: return null
            return LayoutNodeHelper.getRecomposer(node)
        }

    val depth: Int
        /**
         * @return The tree depth of the LayoutNode. This is valid only when it is attached to a
         * hierarchy.
         */
        get() {
            val node = mNode.get() ?: return 0
            return node.`depth$ui_release`
        }

    val childrenSize: Int
        /**
         * @return The size of children.
         */
        get() {
            val node = mNode.get() ?: return 0
            return node.`_children$ui_release`.size
        }

    val children: List<LayoutNodeInfo>?
        /**
         * @return The children of this LayoutNode.
         */
        get() {
            val node = mNode.get() ?: return null
            val nodeChildren: MutableVector<LayoutNode> = node.`_children$ui_release`
            val nodeInfoChildren: MutableList<LayoutNodeInfo> = ArrayList(nodeChildren.size)
            nodeChildren.forEach { layoutNode: LayoutNode ->
                nodeInfoChildren.add(LayoutNodeInfo(layoutNode))
            }
            return nodeInfoChildren
        }

    val parent: LayoutNodeInfo?
        /**
         * @return The parent node in the LayoutNode hierarchy, skipping over virtual nodes.
         */
        get() {
            val node = mNode.get() ?: return null
            val parentNode = node.`parent$ui_release` ?: return null
            return LayoutNodeInfo(parentNode)
        }

    val ancestors: List<LayoutNodeInfo>?
        /**
         * @return All ancestor nodes in the LayoutNode hierarchy, from root LayoutNode to direct parent
         * LayoutNode.
         */
        get() {
            val node = mNode.get() ?: return null
            var parent: LayoutNodeInfo? = parent ?: return null
            val ancestors: MutableList<LayoutNodeInfo> = ArrayList(depth)
            while (parent != null) {
                ancestors.add(0, parent)
                parent = parent.parent
            }
            return ancestors
        }

    val modifiers: List<Modifier>?
        /**
         * @return The all modifiers of the specified LayoutNode.
         */
        get() {
            val node = mNode.get() ?: return null
            return LayoutNodeHelper.getModifiers(node)
        }

    val trackId: TrackId?
        /**
         * @return The tag associated to a composable with the Modifier.trackId modifier.
         */
        get() {
            val node = mNode.get() ?: return null
            return LayoutNodeHelper.getTrackId(node)
        }

    val trackIdPath: String?
        /**
         * @return TrackId path from root LayoutNode to this LayoutNode.
         */
        get() {
            val node = mNode.get() ?: return null
            return LayoutNodeHelper.getTrackIdPath(node)
        }

    /**
     * @return The LayoutNodeInfo with the specified modifier in the LayoutNode hierarchy,
     * or null` if none can be found.
     */
    @Throws(IllegalStateException::class)
    fun findLayoutNodeInfo(modifier: Modifier): LayoutNodeInfo? {
        val node = mNode.get() ?: return null
        val foundNode = LayoutNodeHelper.findLayoutNode(node, modifier) ?: return null
        return LayoutNodeInfo(foundNode)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LayoutNodeInfo) return false
        val node = mNode.get()
        val anotherNode = other.mNode.get()
        return node != null && node == anotherNode
    }

    override fun toString(): String {
        val node = mNode.get() ?: return "null"
        return LayoutNodeHelper.layoutNodeToString(node)
    }

    fun toTreeString(): String {
        val node = mNode.get() ?: return "null"
        return LayoutNodeHelper.toTreeString(node)
    }

    interface LayoutNodeInfoConsumer {
        fun consume(nodeInfo: LayoutNodeInfo)
    }

    interface LayoutNodeInfoPredicate {
        fun test(nodeInfo: LayoutNodeInfo): Boolean
    }

    companion object {
        private const val TAG = "LayoutNodeInfo"

        fun traverseLayoutNodeTree(
            nodeInfo: LayoutNodeInfo,
            consumer: LayoutNodeInfoConsumer
        ) {
            consumer.consume(nodeInfo)
            val children = nodeInfo.children ?: return
            for (child in children) {
                traverseLayoutNodeTree(child, consumer)
            }
        }

        fun findInLayoutNodeTree(
            nodeInfo: LayoutNodeInfo,
            predicate: LayoutNodeInfoPredicate
        ): LayoutNodeInfo? {
            if (predicate.test(nodeInfo)) return nodeInfo
            val children = nodeInfo.children ?: return null
            for (child in children) {
                val childFound = findInLayoutNodeTree(child, predicate)
                if (childFound != null) return childFound
            }
            return null
        }
    }
}
