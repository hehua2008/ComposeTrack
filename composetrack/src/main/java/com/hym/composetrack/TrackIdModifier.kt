package com.hym.composetrack

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

/**
 * @author hehua2008
 * @date 2024/3/22
 */
/*
@Stable
fun Modifier.trackId(trackId: TrackId) = this then TrackIdElement(trackId = trackId)

@Stable
fun Modifier.trackId(name: String) = this.trackId(TrackId(name))

private data class TrackIdElement(
    val trackId: TrackId
) : ModifierNodeElement<TrackIdModifier>() {
    override fun create() = TrackIdModifier(trackId)

    override fun update(node: TrackIdModifier) {
        node.trackId = trackId
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "trackId"
        value = trackId
    }
}

private class TrackIdModifier(
    var trackId: TrackId,
) : Modifier.Node()
*/

@Stable
fun Modifier.trackId(trackId: TrackId) = this then TrackIdElement(trackId = trackId)

@Stable
fun Modifier.trackId(name: String) = this.trackId(TrackId(name))

private data class TrackIdElement(
    val trackId: TrackId
) : Modifier.Element
