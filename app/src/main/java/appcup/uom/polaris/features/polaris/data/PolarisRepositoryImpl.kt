package appcup.uom.polaris.features.polaris.data

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.RoutesApi
import appcup.uom.polaris.core.domain.RoutesResponse
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import appcup.uom.polaris.features.polaris.domain.Waypoint

class PolarisRepositoryImpl(
    private val routesApi: RoutesApi
): PolarisRepository {
    override suspend fun getRoutePolyline(
        startingWaypoint: Waypoint,
        intermediaryWaypoints: List<Waypoint>,
        destinationWaypoint: Waypoint
    ): Result<RoutesResponse, DataError.Remote> {
        return routesApi.getRoutePolyline(startingWaypoint, intermediaryWaypoints, destinationWaypoint)
    }
}