package com.hym.composetrack

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.LayoutNode
import androidx.compose.ui.platform.compositionContext
import com.hym.composetrack.LayoutNodeHelper.LayoutNodeConsumer

/**
 * @author hehua2008
 * @date 2022/1/7
 */
object LayoutNodeHelper {
    private const val TAG = "LayoutNodeHelper"

    /**
     * @return All ancestor nodes in the LayoutNode hierarchy, from root LayoutNode to direct parent
     * LayoutNode.
     */
    fun getAncestors(node: LayoutNode): MutableList<LayoutNode> {
        val ancestors: MutableList<LayoutNode> = ArrayList(node.`depth$ui_release`)
        var parent = node.`parent$ui_release`
        while (parent != null) {
            ancestors.add(0, parent)
            parent = parent.`parent$ui_release`
        }
        return ancestors
    }

    /**
     * @return The all modifiers of the specified LayoutNode.
     */
    fun getModifiers(node: LayoutNode): List<Modifier> {
        val modifierList: MutableList<Modifier> = ArrayList()
        node.modifier.foldIn(modifierList) { modifiers: MutableList<Modifier>, element: Modifier.Element ->
            modifiers.add(element)
            modifiers
        }
        return modifierList
    }

    /**
     * @return A new List of Modifiers and the coordinates and any extra information
     * that may be useful. This is used for tooling to retrieve layout modifier and layer
     * information.
     */
    fun getModifierInfo(node: LayoutNode): List<ModifierInfo> {
        return node.getModifierInfo()
    }

    /**
     * @return The tag associated to a composable with the Modifier.trackId modifier.
     */
    @JvmStatic
    fun getTrackId(node: LayoutNode): TrackId? {
        val list = getModifiers(node)
        var trackIdElement: TrackIdElement? = null
        for (modifier in list) {
            if (modifier is TrackIdElement) {
                trackIdElement = modifier
                break
            }
        }
        return trackIdElement?.trackId
    }

    /**
     * @return TrackId path from root LayoutNode to this LayoutNode.
     */
    fun getTrackIdPath(node: LayoutNode): String {
        val sb = StringBuilder()
        val nodes = getAncestors(node)
        nodes.add(node)
        for (nd in nodes) {
            val trackId = getTrackId(nd)
            if (trackId != null) {
                sb.append(trackId.name)
            } else {
                val clzName = getNodeName(nd)
                sb.append(clzName)
            }
            sb.append('/')
        }
        sb.deleteCharAt(sb.length - 1)
        return sb.toString()
    }

    /**
     * @return LayoutNode measure policy name.
     */
    fun getNodeName(node: LayoutNode): String {
        val measurePolicyClz: Class<*> = node.measurePolicy.javaClass
        val enclosingClass = measurePolicyClz.enclosingClass
        var clzName =
            if (enclosingClass != null) enclosingClass.getSimpleName() else measurePolicyClz.getSimpleName()
        if (clzName.endsWith("Kt")) {
            clzName = clzName.substring(0, clzName.length - 2)
        }
        if (clzName.endsWith("Impl")) {
            clzName = clzName.substring(0, clzName.length - 4)
        }
        if (clzName.endsWith("MeasurePolicy")) {
            clzName = clzName.substring(0, clzName.length - 13)
        }
        return clzName
    }

    /**
     * @return The view system Owner. This null until attach is called.
     */
    fun getOwnerView(node: LayoutNode): View? {
        val owner: Any? = node.`owner$ui_release`
        return if (owner is View) owner else null
    }

    /**
     * @return The parent [Recomposer] for this point in the view hierarchy, or `null` if none can
     * be found.
     */
    fun getRecomposer(node: LayoutNode): Recomposer? {
        val ownerView = node.`owner$ui_release` as? View ?: return null
        var found = ownerView.compositionContext
        if (found is Recomposer) return found
        var parentView = ownerView.parent
        while (parentView is ViewGroup) {
            found = parentView.compositionContext
            if (found is Recomposer) return found
            parentView = parentView.getParent()
        }
        return null
    }

    /**
     * @return The LayoutNode with the specified modifier in the LayoutNode hierarchy,
     * or null` if none can be found.
     */
    @Throws(IllegalStateException::class)
    fun findLayoutNode(rootNode: LayoutNode, modifier: Modifier): LayoutNode? {
        val nodeList: MutableList<LayoutNode> = ArrayList()
        return if (false) {
            traverseLayoutNodeTree(rootNode) { node: LayoutNode ->
                if (modifier is TrackId && getTrackId(node) === modifier
                    || getModifiers(node).contains(modifier)
                ) {
                    nodeList.add(node)
                }
            }
            if (nodeList.isEmpty()) return null
            check(nodeList.size <= 1) { "get more then one LayoutNode with: $modifier" }
            nodeList[0]
        } else {
            findInLayoutNodeTree(rootNode) { curNode: LayoutNode ->
                (modifier is TrackId && getTrackId(curNode) === modifier
                        || getModifiers(curNode).contains(modifier))
            }
        }
    }

    fun toTreeString(rootNode: LayoutNode): String {
        val sb = StringBuilder()
        traverseLayoutNodeTree(rootNode, LayoutNodeConsumer { node: LayoutNode ->
            for (d in 0 until node.`depth$ui_release`) {
                sb.append("  ")
            }
            sb.append("|-")
            layoutNodeToString(node, sb)
            sb.append('\n')
        })
        return sb.toString()
    }

    fun layoutNodeToString(node: LayoutNode): String {
        val sb = StringBuilder()
        layoutNodeToString(node, sb)
        return sb.toString()
    }

    fun layoutNodeToString(node: LayoutNode, sb: StringBuilder) {
        sb.append("LayoutNode@" + String.format("%07x", System.identityHashCode(node)))
            .append("  trackId=").append(getTrackId(node))
            .append("  depth=").append(node.`depth$ui_release`)
            .append("  childSize=").append(node.`_children$ui_release`.size)
            .append("  boundsInWindow=")
            .append(node.coordinates.boundsInWindow())
            .append("  measurePolicy=")
            .append(node.measurePolicy.javaClass.getName())
    }

    fun traverseLayoutNodeTree(
        node: LayoutNode,
        consumer: LayoutNodeConsumer
    ) {
        consumer.consume(node)
        for (child in node.`children$ui_release`) {
            traverseLayoutNodeTree(child, consumer)
        }
    }

    fun findInLayoutNodeTree(
        node: LayoutNode,
        predicate: LayoutNodePredicate
    ): LayoutNode? {
        if (predicate.test(node)) return node
        for (child in node.`children$ui_release`) {
            val childFound = findInLayoutNodeTree(child, predicate)
            if (childFound != null) return childFound
        }
        return null
    }

    fun interface LayoutNodeConsumer {
        fun consume(node: LayoutNode)
    }

    fun interface LayoutNodePredicate {
        fun test(node: LayoutNode): Boolean
    }
}
