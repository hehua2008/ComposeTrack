package androidx.compose.runtime;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.compose.ui.node.LayoutNode;
import androidx.compose.ui.platform.AndroidComposeView;

import com.hym.composetrack.Identity;
import com.hym.composetrack.LayoutNodeHelper;
import com.hym.composetrack.NodeIdentity;

/**
 * @author ganmin.he
 * @date 2022/1/7
 */
@SuppressLint("LongLogTag")
public final class OnAttachOnDetachListener {
    private static final String TAG = "OnAttachOnDetachListener";

    private OnAttachOnDetachListener() {
    }

    public static void onAttach(@NonNull AndroidComposeView owner, @NonNull LayoutNode node) {
        //Log.w(TAG, "onAttach");
        Identity identity = LayoutNodeHelper.getIdentity(node);
        if (identity instanceof NodeIdentity) {
            NodeIdentity nodeIdentity = ((NodeIdentity) identity);
            nodeIdentity.onAttach(node);
        }
    }

    public static void onDetach(@NonNull AndroidComposeView owner, @NonNull LayoutNode node) {
        //Log.w(TAG, "onDetach");
        Identity identity = LayoutNodeHelper.getIdentity(node);
        if (identity instanceof NodeIdentity) {
            NodeIdentity nodeIdentity = ((NodeIdentity) identity);
            nodeIdentity.onDetach();
        }
    }
}
