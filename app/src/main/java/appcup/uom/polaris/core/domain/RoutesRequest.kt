package appcup.uom.polaris.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class RoutesRequest(
    val origin: RouteWaypoint,
    val destination: RouteWaypoint,
    val intermediates: List<RouteWaypoint>,
    val optimizeWaypointOrder: Boolean,
    val polylineQuality: PolylineQuality = PolylineQuality.HIGH_QUALITY,
) {
    @Serializable
    data class RouteWaypoint(
        val via: Boolean = false,
        val vehicleStopover: Boolean = false,
        val sideOfRoad: Boolean = false,
        val location: Location,
    ) {
        @Serializable
        data class Location(
            val latLng: LatLng,
        ) {
            @Serializable
            data class LatLng(
                val latitude: Double,
                val longitude: Double
            )
        }
    }

    @Serializable
    enum class PolylineQuality {
        HIGH_QUALITY,
        OVERVIEW
    }
}
