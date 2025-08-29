package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.Serializable

@Serializable
data class GeneratedWaypoints(
    val title: String,
    val description: String,
    val waypoints: List<Waypoint>
)
