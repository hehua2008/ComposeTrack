package com.hym.composetrack;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.runtime.CompositionContext;
import androidx.compose.runtime.Recomposer;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.LayoutCoordinatesKt;
import androidx.compose.ui.layout.LayoutIdKt;
import androidx.compose.ui.node.LayoutNode;
import androidx.compose.ui.node.Owner;
import androidx.compose.ui.platform.WindowRecomposer_androidKt;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ganmin.he
 * @date 2022/1/7
 */
public final class LayoutNodeHelper {
    private static final String TAG = "LayoutNodeHelper";

    private LayoutNodeHelper() {
    }

    /**
     * @return All ancestor nodes in the LayoutNode hierarchy, from root LayoutNode to direct parent
     * LayoutNode.
     */
    @NonNull
    public static List<LayoutNode> getAncestors(@NonNull LayoutNode node) {
        List<LayoutNode> ancestors = new ArrayList<>(node.getDepth$ui_release());
        LayoutNode parent = node.getParent$ui_release();
        while (parent != null) {
            ancestors.add(0, parent);
            parent = parent.getParent$ui_release();
        }
        return ancestors;
    }

    /**
     * @return The all modifiers of the specified LayoutNode.
     */
    @NonNull
    public static List<Modifier> getModifiers(@NonNull LayoutNode node) {
        final List<Modifier> modifierList = new ArrayList<>();
        node.getModifier().foldIn(modifierList, (modifiers, element) -> {
            modifiers.add(element);
            return modifiers;
        });
        return modifierList;
    }

    /**
     * @return The tag associated to a composable with the Modifier.identity modifier.
     */
    @Nullable
    public static Identity getIdentity(@NonNull LayoutNode node) {
        Object obj = LayoutIdKt.getLayoutId(node);
        return (obj instanceof Identity) ? (Identity) obj : null;
    }

    /**
     * @return Identity path from root LayoutNode to this LayoutNode.
     */
    @NonNull
    public static String getIdentityPath(@NonNull LayoutNode node) {
        StringBuilder sb = new StringBuilder();
        List<LayoutNode> nodes = getAncestors(node);
        nodes.add(node);
        for (LayoutNode nd : nodes) {
            Identity identity = getIdentity(nd);
            if (identity != null) {
                sb.append(identity.getName());
            } else {
                Class<?> measurePolicyClz = nd.getMeasurePolicy().getClass();
                Class<?> enclosingClass = measurePolicyClz.getEnclosingClass();
                String clzName = (enclosingClass != null) ?
                        enclosingClass.getSimpleName() : measurePolicyClz.getSimpleName();
                if (clzName.endsWith("Kt")) {
                    clzName = clzName.substring(0, clzName.length() - 2);
                }
                if (clzName.endsWith("Impl")) {
                    clzName = clzName.substring(0, clzName.length() - 4);
                }
                if (clzName.endsWith("MeasurePolicy")) {
                    clzName = clzName.substring(0, clzName.length() - 13);
                }
                sb.append(clzName);
            }
            sb.append('/');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * @return The view system Owner. This null until attach is called.
     */
    @Nullable
    public static View getOwnerView(@NonNull LayoutNode node) {
        Owner owner = node.getOwner$ui_release();
        return (owner instanceof View) ? (View) owner : null;
    }

    /**
     * @return The parent [Recomposer] for this point in the view hierarchy, or `null` if none can
     * be found.
     */
    @Nullable
    public static Recomposer getRecomposer(@NonNull LayoutNode node) {
        Owner owner = node.getOwner$ui_release();
        if (!(owner instanceof View)) return null;
        final View ownerView = (View) owner;
        CompositionContext found = WindowRecomposer_androidKt.getCompositionContext(ownerView);
        if (found instanceof Recomposer) return (Recomposer) found;
        ViewParent parentView = ownerView.getParent();
        while (parentView instanceof ViewGroup) {
            found = WindowRecomposer_androidKt.getCompositionContext((ViewGroup) parentView);
            if (found instanceof Recomposer) return (Recomposer) found;
            parentView = parentView.getParent();
        }
        return null;
    }

    /**
     * @return The LayoutNode with the specified modifier in the LayoutNode hierarchy,
     * or null` if none can be found.
     */
    @Nullable
    public static LayoutNode findLayoutNode(@NonNull LayoutNode rootNode, @NonNull Modifier modifier)
            throws IllegalStateException {
        final List<LayoutNode> nodeList = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            travelLayoutNodeTree(rootNode, node -> {
                if (modifier instanceof Identity && getIdentity(node) == modifier
                        || getModifiers(node).contains(modifier)) {
                    nodeList.add(node);
                }
            });
            if (nodeList.isEmpty()) return null;
            if (nodeList.size() > 1) {
                throw new IllegalStateException("get more then one LayoutNode with: " + modifier);
            }
            return nodeList.get(0);
        } else {
            return findInLayoutNodeTree(rootNode, curNode ->
                    (modifier instanceof Identity && getIdentity(curNode) == modifier
                            || getModifiers(curNode).contains(modifier)));
        }
    }

    @NonNull
    public static String toTreeString(@NonNull LayoutNode rootNode) {
        final StringBuilder sb = new StringBuilder();
        travelLayoutNodeTree(rootNode, node -> {
            for (int d = 0; d < node.getDepth$ui_release(); d++) {
                sb.append("  ");
            }
            sb.append("|-");
            layoutNodeToString(node, sb);
            sb.append('\n');
        });
        return sb.toString();
    }

    public static String layoutNodeToString(@NonNull LayoutNode node) {
        final StringBuilder sb = new StringBuilder();
        layoutNodeToString(node, sb);
        return sb.toString();
    }

    public static void layoutNodeToString(@NonNull LayoutNode node, @NonNull StringBuilder sb) {
        sb.append("LayoutNode@" + String.format("%07x", System.identityHashCode(node)))
                .append("  identity=").append(getIdentity(node))
                .append("  depth=").append(node.getDepth$ui_release())
                .append("  childSize=").append(node.get_children$ui_release().getSize())
                .append("  boundsInWindow=")
                .append(LayoutCoordinatesKt.boundsInWindow(node.getCoordinates()))
                .append("  measurePolicy=")
                .append(node.getMeasurePolicy().getClass().getName());
    }

    public static void travelLayoutNodeTree(@NonNull LayoutNode node,
                                            @NonNull LayoutNodeConsumer consumer) {
        consumer.consume(node);
        for (LayoutNode child : node.getChildren$ui_release()) {
            travelLayoutNodeTree(child, consumer);
        }
    }

    @Nullable
    public static LayoutNode findInLayoutNodeTree(@NonNull LayoutNode node,
                                                  @NonNull LayoutNodePredicate predicate) {
        if (predicate.test(node)) return node;
        for (LayoutNode child : node.getChildren$ui_release()) {
            LayoutNode childFound = findInLayoutNodeTree(child, predicate);
            if (childFound != null) return childFound;
        }
        return null;
    }

    public interface LayoutNodeConsumer {
        void consume(@NonNull LayoutNode node);
    }

    public interface LayoutNodePredicate {
        boolean test(@NonNull LayoutNode node);
    }
}
