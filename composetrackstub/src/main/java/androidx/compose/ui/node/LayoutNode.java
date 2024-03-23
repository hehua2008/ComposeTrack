package androidx.compose.ui.node;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.runtime.ComposeNodeLifecycleCallback;
import androidx.compose.runtime.collection.MutableVector;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.LayoutCoordinates;
import androidx.compose.ui.layout.LayoutInfo;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.layout.ModifierInfo;
import androidx.compose.ui.layout.Remeasurement;
import androidx.compose.ui.platform.ViewConfiguration;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.LayoutDirection;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author hehua2008
 * @date 2023/3/8
 */
public final class LayoutNode implements ComposeNodeLifecycleCallback, Remeasurement, LayoutInfo {
    @Override
    public void onDeactivate() {
    }

    @Override
    public void onRelease() {
    }

    @Override
    public void onReuse() {
    }

    @NonNull
    @Override
    public LayoutCoordinates getCoordinates() {
        return null;
    }

    @NonNull
    @Override
    public Density getDensity() {
        return null;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean isAttached() {
        return false;
    }

    @Override
    public boolean isPlaced() {
        return false;
    }

    @NonNull
    @Override
    public LayoutDirection getLayoutDirection() {
        return null;
    }

    @Nullable
    @Override
    public LayoutInfo getParentInfo() {
        return null;
    }

    @Override
    public int getSemanticsId() {
        return 0;
    }

    @NonNull
    @Override
    public ViewConfiguration getViewConfiguration() {
        return null;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @NonNull
    @Override
    public List<ModifierInfo> getModifierInfo() {
        return null;
    }

    @Override
    public void forceRemeasure() {
    }

    @NotNull
    public MeasurePolicy getMeasurePolicy() {
        return null;
    }

    @NotNull
    public Modifier getModifier() {
        return null;
    }

    public void setModifier(@NotNull Modifier value) {
    }

    @Nullable
    public Owner getOwner$ui_release() {
        return null;
    }

    public int getDepth$ui_release() {
        return 0;
    }

    @NotNull
    public MutableVector<LayoutNode> get_children$ui_release() {
        return null;
    }

    @Nullable
    public LayoutNode getParent$ui_release() {
        return null;
    }

    @NotNull
    public List<LayoutNode> getChildren$ui_release() {
        return null;
    }
}
