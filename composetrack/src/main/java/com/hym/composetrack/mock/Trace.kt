package com.hym.composetrack.mock

import androidx.compose.runtime.TraceListener.onTraceBegin
import androidx.compose.runtime.TraceListener.onTraceEnd
import androidx.compose.runtime.TraceToken

/**
 * @author hehua2008
 * @date 2024/3/23
 */
// androidx/compose/runtime/Trace
internal object Trace {
    fun beginSection(name: String): Any {
        android.os.Trace.beginSection(name)
        val token = TraceToken(name)
        onTraceBegin(token)
        return token
    }

    fun endSection(token: Any) {
        onTraceEnd(token as TraceToken)
        android.os.Trace.endSection()
    }
}
