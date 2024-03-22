package androidx.compose.runtime

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * @author hehua2008
 * @date 2022/1/5
 */
object TraceListener {
    private const val TAG = "TraceListener"

    const val COMPOSER_DISPOSE = "Compose:Composer.dispose"
    const val COMPOSE_RECOMPOSE = "Compose:recompose"
    const val RECOMPOSER_ANIMATION = "Recomposer:animation"
    const val RECOMPOSER_RECOMPOSE = "Recomposer:recompose"

    val TRACE_DISPATCHER = newSingleThreadContext(TAG)

    enum class TraceState {
        Begin,
        End
    }

    interface TraceCallback {
        fun onTraceBegin(name: String)

        fun onTraceEnd(name: String)
    }

    private val stateMap = ConcurrentHashMap<String, MutableSharedFlow<TraceState>>()
    private val callbackMap = ConcurrentHashMap<String, CopyOnWriteArraySet<TraceCallback>>()

    /**
     * This will be invoked in [Trace.beginSection]
     */
    fun onTraceBegin(token: TraceToken) {
        stateMap[token.name]?.tryEmit(TraceState.Begin)
        callbackMap[token.name]?.run {
            GlobalScope.launch(TRACE_DISPATCHER) {
                forEach { it.onTraceBegin(token.name) }
            }
        }
    }

    /**
     * This will be invoked in [Trace.endSection]
     */
    fun onTraceEnd(token: TraceToken) {
        stateMap[token.name]?.tryEmit(TraceState.End)
        callbackMap[token.name]?.run {
            GlobalScope.launch(TRACE_DISPATCHER) {
                forEach { it.onTraceEnd(token.name) }
            }
        }
    }

    fun getTraceState(name: String): Flow<TraceState> {
        val state = stateMap[name] ?: MutableSharedFlow<TraceState>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        ).let {
            stateMap.putIfAbsent(name, it) ?: it
        }
        return state
    }

    fun registerTraceCallback(name: String, traceCallback: TraceCallback) {
        val callbacks = callbackMap[name] ?: CopyOnWriteArraySet<TraceCallback>().let {
            callbackMap.putIfAbsent(name, it) ?: it
        }
        callbacks.add(traceCallback)
    }

    fun unregisterTraceCallback(name: String, traceCallback: TraceCallback) {
        callbackMap[name]?.remove(traceCallback)
    }

    fun unregisterTraceCallback(traceCallback: TraceCallback) {
        callbackMap.forEach { (_, callbacks) ->
            callbacks.remove(traceCallback)
        }
    }
}