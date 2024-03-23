package androidx.compose.ui.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author hehua2008
 * @date 2024/3/23
 */
public interface Owner {
    void onAttach(@NotNull LayoutNode node);

    void onDetach(@NotNull LayoutNode node);
}
