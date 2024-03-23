package androidx.compose.runtime;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.compose.ui.node.LayoutNode;

import com.hym.composetrack.LayoutNodeHelper;
import com.hym.composetrack.NodeTrackId;
import com.hym.composetrack.TrackId;

/**
 * @author hehua2008
 * @date 2022/1/7
 */
@SuppressLint("LongLogTag")
public final class OnAttachOnDetachListener {
    private static final String TAG = "OnAttachOnDetachListener";

    private OnAttachOnDetachListener() {
    }

    public static void onAttach(@NonNull /*AndroidComposeView*/View owner, @NonNull LayoutNode node) {
        //Log.w(TAG, "onAttach");
        TrackId trackId = LayoutNodeHelper.getTrackId(node);
        if (trackId instanceof NodeTrackId) {
            NodeTrackId nodeTrackId = ((NodeTrackId) trackId);
            nodeTrackId.onAttach(node);
        }
    }

    public static void onDetach(@NonNull /*AndroidComposeView*/View owner, @NonNull LayoutNode node) {
        //Log.w(TAG, "onDetach");
        TrackId trackId = LayoutNodeHelper.getTrackId(node);
        if (trackId instanceof NodeTrackId) {
            NodeTrackId nodeTrackId = ((NodeTrackId) trackId);
            nodeTrackId.onDetach();
        }
    }
}
