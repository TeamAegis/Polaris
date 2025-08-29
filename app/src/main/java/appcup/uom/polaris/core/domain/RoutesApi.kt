package appcup.uom.polaris.core.domain

import appcup.uom.polaris.features.polaris.domain.Waypoint

interface RoutesApi {
    suspend fun getRoutePolyline(startingWaypoint: Waypoint, intermediaryWaypoints: List<Waypoint>, destinationWaypoint: Waypoint): Result<RoutesResponse, DataError.Remote>
}