package appcup.uom.polaris.features.polaris.domain

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.RoutesResponse

interface PolarisRepository {
    suspend fun getRoutePolyline(startingWaypoint: Waypoint, intermediaryWaypoints: List<Waypoint>, destinationWaypoint: Waypoint): Result<RoutesResponse, DataError.Remote>

}