package com.hym.composetrack;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.runtime.Recomposer;
import androidx.compose.runtime.collection.MutableVector;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.geometry.Rect;
import androidx.compose.ui.layout.LayoutCoordinates;
import androidx.compose.ui.layout.LayoutCoordinatesKt;
import androidx.compose.ui.layout.LayoutInfo;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.layout.Remeasurement;
import androidx.compose.ui.node.LayoutNode;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

/**
 * @author hehua2008
 * @date 2021/12/1
 */
public class LayoutNodeInfo {
    private static final String TAG = "LayoutNodeInfo";

    @NonNull
    private final LayoutNode mNode;

    LayoutNodeInfo(@NonNull LayoutInfo node) {
        mNode = (LayoutNode) node;
    }

    @NonNull
    public Remeasurement asRemeasurement() {
        return mNode;
    }

    @NonNull
    public LayoutInfo asLayoutInfo() {
        return mNode;
    }

    /**
     * @return Coordinates of just the contents of the LayoutNode, after being affected by all
     * modifiers.
     */
    @NonNull
    public LayoutCoordinates getCoordinates() {
        return mNode.getCoordinates();
    }

    /**
     * @return The boundaries of this layout relative to the window's origin.
     */
    @NonNull
    public Rect getBoundsInWindow() {
        return LayoutCoordinatesKt.boundsInWindow(mNode.getCoordinates());
    }

    /**
     * @return Blocks that define the measurement and intrinsic measurement of the layout.
     */
    @NonNull
    public MeasurePolicy getMeasurePolicy() {
        return mNode.getMeasurePolicy();
    }

    /**
     * @return The Modifier currently applied to this node.
     */
    @NonNull
    public Modifier getModifier() {
        return mNode.getModifier();
    }

    /**
     * @return Apply the specified Modifier to this node.
     */
    public void setModifier(@NonNull Modifier modifier) {
        mNode.setModifier(modifier);
    }

    /**
     * @return The view system Owner. This null until attach is called.
     */
    @Nullable
    public Object getOwner() {
        return mNode.getOwner$ui_release();
    }

    /**
     * @return The view system Owner. This null until attach is called.
     */
    @Nullable
    public View getOwnerView() {
        return LayoutNodeHelper.getOwnerView(mNode);
    }

    /**
     * @return The parent [Recomposer] for this point in the view hierarchy, or `null` if none can
     * be found.
     */
    @Nullable
    public Recomposer getRecomposer() {
        return LayoutNodeHelper.getRecomposer(mNode);
    }

    /**
     * @return The tree depth of the LayoutNode. This is valid only when it is attached to a
     * hierarchy.
     */
    public int getDepth() {
        return mNode.getDepth$ui_release();
    }

    /**
     * @return The size of children.
     */
    @NonNull
    public int getChildrenSize() {
        return mNode.get_children$ui_release().getSize();
    }

    /**
     * @return The children of this LayoutNode.
     */
    @NonNull
    public List<LayoutNodeInfo> getChildren() {
        MutableVector<LayoutNode> nodeChildren = mNode.get_children$ui_release();
        List<LayoutNodeInfo> nodeInfoChildren = new ArrayList<>(nodeChildren.getSize());
        nodeChildren.forEach(layoutNode -> {
            nodeInfoChildren.add(new LayoutNodeInfo(layoutNode));
            return Unit.INSTANCE;
        });
        return nodeInfoChildren;
    }

    /**
     * @return The parent node in the LayoutNode hierarchy, skipping over virtual nodes.
     */
    @Nullable
    public LayoutNodeInfo getParent() {
        LayoutNode parentNode = mNode.getParent$ui_release();
        if (parentNode == null) return null;
        return new LayoutNodeInfo(parentNode);
    }

    /**
     * @return All ancestor nodes in the LayoutNode hierarchy, from root LayoutNode to direct parent
     * LayoutNode.
     */
    @NonNull
    public List<LayoutNodeInfo> getAncestors() {
        List<LayoutNodeInfo> ancestors = new ArrayList<>(getDepth());
        LayoutNodeInfo parent = getParent();
        while (parent != null) {
            ancestors.add(0, parent);
            parent = parent.getParent();
        }
        return ancestors;
    }

    /**
     * @return The all modifiers of the specified LayoutNode.
     */
    @NonNull
    public List<Modifier> getModifiers() {
        return LayoutNodeHelper.getModifiers(mNode);
    }

    /**
     * @return The tag associated to a composable with the Modifier.trackId modifier.
     */
    @Nullable
    public TrackId getTrackId() {
        return LayoutNodeHelper.getTrackId(mNode);
    }

    /**
     * @return TrackId path from root LayoutNode to this LayoutNode.
     */
    @NonNull
    public String getTrackIdPath() {
        return LayoutNodeHelper.getTrackIdPath(mNode);
    }

    /**
     * @return The LayoutNodeInfo with the specified modifier in the LayoutNode hierarchy,
     * or null` if none can be found.
     */
    @Nullable
    public LayoutNodeInfo findLayoutNodeInfo(@NonNull Modifier modifier)
            throws IllegalStateException {
        final LayoutNode node = LayoutNodeHelper.findLayoutNode(mNode, modifier);
        if (node == null) return null;
        return new LayoutNodeInfo(node);
    }

    @Override
    public int hashCode() {
        return mNode.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof LayoutNodeInfo)) return false;
        return mNode.equals(((LayoutNodeInfo) obj).mNode);
    }

    @NonNull
    @Override
    public String toString() {
        return LayoutNodeHelper.layoutNodeToString(mNode);
    }

    @NonNull
    public String toTreeString() {
        return LayoutNodeHelper.toTreeString(mNode);
    }

    public static void traverseLayoutNodeTree(@NonNull LayoutNodeInfo nodeInfo,
                                            @NonNull LayoutNodeInfoConsumer consumer) {
        consumer.consume(nodeInfo);
        for (LayoutNodeInfo child : nodeInfo.getChildren()) {
            traverseLayoutNodeTree(child, consumer);
        }
    }

    @Nullable
    public static LayoutNodeInfo findInLayoutNodeTree(@NonNull LayoutNodeInfo nodeInfo,
                                                      @NonNull LayoutNodeInfoPredicate predicate) {
        if (predicate.test(nodeInfo)) return nodeInfo;
        for (LayoutNodeInfo child : nodeInfo.getChildren()) {
            LayoutNodeInfo childFound = findInLayoutNodeTree(child, predicate);
            if (childFound != null) return childFound;
        }
        return null;
    }

    public interface LayoutNodeInfoConsumer {
        void consume(@NonNull LayoutNodeInfo nodeInfo);
    }

    public interface LayoutNodeInfoPredicate {
        boolean test(@NonNull LayoutNodeInfo nodeInfo);
    }
}
