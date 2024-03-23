package com.hym.composetrack

import androidx.compose.runtime.*
import androidx.compose.ui.layout.LayoutInfo
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.set

/**
 * @author hehua2008
 * @date 2021/12/3
 */
private val currentRootNodeMap: WeakHashMap<Composer, LayoutNodeInfo?> = WeakHashMap()

val currentRootNode: LayoutNodeInfo
    @Composable
    @ReadOnlyComposable
    get() = currentRootNodeMap[currentComposer]
        ?: LayoutNodeInfo((currentComposer.applier as AbstractApplier<out LayoutInfo>).root).apply {
            currentRootNodeMap[currentComposer] = this
        }

enum class RecomposeState {
    Start,
    End
}

@Composable
fun CollectRecomposeState(action: (suspend (state: RecomposeState) -> Unit)?) {
    // When action is null, cancel the collect that has been started in LaunchedEffect
    action ?: return

    // This will always refer to the latest action that CollectRecomposeState was recomposed with
    val latestAction by rememberUpdatedState(action)

    // Create an effect that matches the lifecycle of CollectRecomposeState.
    // If CollectRecomposeState recomposes, the suspend block shouldn't start again.
    LaunchedEffect(Unit) {
        launch(TraceListener.TRACE_DISPATCHER) {
            TraceListener.getTraceState(TraceListener.RECOMPOSER_RECOMPOSE)
                .cancellable()
                .collect {
                    if (it == TraceListener.TraceState.Begin) {
                        latestAction(RecomposeState.Start)
                    } else if (it == TraceListener.TraceState.End) {
                        latestAction(RecomposeState.End)
                    }
                }
        }
    }
}
