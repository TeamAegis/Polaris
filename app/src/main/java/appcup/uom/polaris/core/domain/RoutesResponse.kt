package appcup.uom.polaris.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class RoutesResponse(
    val routes: List<Route>
) {
    @Serializable
    data class Route(
        val polyline: Polyline,
        val optimizedIntermediateWaypointIndex: List<Int>? = null,
    ) {
        @Serializable
        data class Polyline(
            val encodedPolyline: String,
        )
    }
}
