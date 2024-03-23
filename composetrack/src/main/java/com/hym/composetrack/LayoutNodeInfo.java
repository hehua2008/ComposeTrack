package com.hym.composetrack;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.runtime.ComposeNodeLifecycleCallback;
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

import java.lang.ref.WeakReference;
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
    private final WeakReference<LayoutNode> mNode;

    public LayoutNodeInfo(@NonNull LayoutInfo node) {
        mNode = new WeakReference((LayoutNode) node);
    }

    @Nullable
    public ComposeNodeLifecycleCallback asComposeNodeLifecycleCallback() {
        return mNode.get();
    }

    @Nullable
    public Remeasurement asRemeasurement() {
        return mNode.get();
    }

    @Nullable
    public LayoutInfo asLayoutInfo() {
        return mNode.get();
    }

    /**
     * @return Coordinates of just the contents of the LayoutNode, after being affected by all
     * modifiers.
     */
    @Nullable
    public LayoutCoordinates getCoordinates() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return node.getCoordinates();
    }

    /**
     * @return The boundaries of this layout relative to the window's origin.
     */
    @Nullable
    public Rect getBoundsInWindow() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return LayoutCoordinatesKt.boundsInWindow(node.getCoordinates());
    }

    /**
     * @return Blocks that define the measurement and intrinsic measurement of the layout.
     */
    @Nullable
    public MeasurePolicy getMeasurePolicy() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return node.getMeasurePolicy();
    }

    /**
     * @return The Modifier currently applied to this node.
     */
    @Nullable
    public Modifier getModifier() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return node.getModifier();
    }

    /**
     * @return Apply the specified Modifier to this node.
     */
    public void setModifier(@NonNull Modifier modifier) {
        LayoutNode node = mNode.get();
        if (node == null) return;
        node.setModifier(modifier);
    }

    /**
     * @return The view system Owner. This null until attach is called.
     */
    @Nullable
    public Object getOwner() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return node.getOwner$ui_release();
    }

    /**
     * @return The view system Owner. This null until attach is called.
     */
    @Nullable
    public View getOwnerView() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return LayoutNodeHelper.getOwnerView(node);
    }

    /**
     * @return The parent [Recomposer] for this point in the view hierarchy, or `null` if none can
     * be found.
     */
    @Nullable
    public Recomposer getRecomposer() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return LayoutNodeHelper.getRecomposer(node);
    }

    /**
     * @return The tree depth of the LayoutNode. This is valid only when it is attached to a
     * hierarchy.
     */
    public int getDepth() {
        LayoutNode node = mNode.get();
        if (node == null) return 0;
        return node.getDepth$ui_release();
    }

    /**
     * @return The size of children.
     */
    @Nullable
    public int getChildrenSize() {
        LayoutNode node = mNode.get();
        if (node == null) return 0;
        return node.get_children$ui_release().getSize();
    }

    /**
     * @return The children of this LayoutNode.
     */
    @Nullable
    public List<LayoutNodeInfo> getChildren() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        MutableVector<LayoutNode> nodeChildren = node.get_children$ui_release();
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
        LayoutNode node = mNode.get();
        if (node == null) return null;
        LayoutNode parentNode = node.getParent$ui_release();
        if (parentNode == null) return null;
        return new LayoutNodeInfo(parentNode);
    }

    /**
     * @return All ancestor nodes in the LayoutNode hierarchy, from root LayoutNode to direct parent
     * LayoutNode.
     */
    @Nullable
    public List<LayoutNodeInfo> getAncestors() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        LayoutNodeInfo parent = getParent();
        if (parent == null) return null;
        List<LayoutNodeInfo> ancestors = new ArrayList<>(getDepth());
        while (parent != null) {
            ancestors.add(0, parent);
            parent = parent.getParent();
        }
        return ancestors;
    }

    /**
     * @return The all modifiers of the specified LayoutNode.
     */
    @Nullable
    public List<Modifier> getModifiers() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return LayoutNodeHelper.getModifiers(node);
    }

    /**
     * @return The tag associated to a composable with the Modifier.trackId modifier.
     */
    @Nullable
    public TrackId getTrackId() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return LayoutNodeHelper.getTrackId(node);
    }

    /**
     * @return TrackId path from root LayoutNode to this LayoutNode.
     */
    @Nullable
    public String getTrackIdPath() {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        return LayoutNodeHelper.getTrackIdPath(node);
    }

    /**
     * @return The LayoutNodeInfo with the specified modifier in the LayoutNode hierarchy,
     * or null` if none can be found.
     */
    @Nullable
    public LayoutNodeInfo findLayoutNodeInfo(@NonNull Modifier modifier)
            throws IllegalStateException {
        LayoutNode node = mNode.get();
        if (node == null) return null;
        final LayoutNode foundNode = LayoutNodeHelper.findLayoutNode(node, modifier);
        if (foundNode == null) return null;
        return new LayoutNodeInfo(foundNode);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof LayoutNodeInfo)) return false;
        LayoutNode node = mNode.get();
        LayoutNode anotherNode = ((LayoutNodeInfo) obj).mNode.get();
        return node != null && node.equals(anotherNode);
    }

    @NonNull
    @Override
    public String toString() {
        LayoutNode node = mNode.get();
        if (node == null) return "null";
        return LayoutNodeHelper.layoutNodeToString(node);
    }

    @NonNull
    public String toTreeString() {
        LayoutNode node = mNode.get();
        if (node == null) return "null";
        return LayoutNodeHelper.toTreeString(node);
    }

    public static void travelLayoutNodeTree(@NonNull LayoutNodeInfo nodeInfo,
                                            @NonNull LayoutNodeInfoConsumer consumer) {
        consumer.consume(nodeInfo);
        for (LayoutNodeInfo child : nodeInfo.getChildren()) {
            travelLayoutNodeTree(child, consumer);
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
