package androidx.compose.runtime

/**
 * @author ganmin.he
 * @date 2022/1/6
 *
 * This will be used in [Trace] and [TraceListener]
 */
class TraceToken(val name: String) {
    override fun toString(): String = "TraceToken($name)@${hashCode()}"
}