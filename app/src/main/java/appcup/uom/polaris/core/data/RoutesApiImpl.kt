package appcup.uom.polaris.core.data

import android.util.Log
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.RoutesApi
import appcup.uom.polaris.core.domain.RoutesRequest
import appcup.uom.polaris.core.domain.RoutesResponse
import appcup.uom.polaris.features.polaris.domain.Waypoint
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RoutesApiImpl(
    private val httpClient: HttpClient
) : RoutesApi {
    val routesUrl = "https://routes.googleapis.com/directions/v2:computeRoutes"

    override suspend fun getRoutePolyline(
        startingWaypoint: Waypoint,
        intermediaryWaypoints: List<Waypoint>,
        destinationWaypoint: Waypoint
    ): Result<RoutesResponse, DataError.Remote> {
        val request = RoutesRequest(
            origin = RoutesRequest.RouteWaypoint(
                vehicleStopover = true,
                location = RoutesRequest.RouteWaypoint.Location(
                    latLng = RoutesRequest.RouteWaypoint.Location.LatLng(
                        latitude = startingWaypoint.latitude,
                        longitude = startingWaypoint.longitude
                    )
                )
            ),
            intermediates = intermediaryWaypoints.map { waypoint ->
                RoutesRequest.RouteWaypoint(
                    vehicleStopover = true,
                    location = RoutesRequest.RouteWaypoint.Location(
                        latLng = RoutesRequest.RouteWaypoint.Location.LatLng(
                            latitude = waypoint.latitude,
                            longitude = waypoint.longitude
                        ),
                    ),
                )
            },
            destination = RoutesRequest.RouteWaypoint(
                vehicleStopover = true,
                location = RoutesRequest.RouteWaypoint.Location(
                    latLng = RoutesRequest.RouteWaypoint.Location.LatLng(
                        latitude = destinationWaypoint.latitude,
                        longitude = destinationWaypoint.longitude
                    ),
                ),
            ),
            optimizeWaypointOrder = true
        )

        return try {
            Result.Success(httpClient.post(routesUrl) {
                header(
                    "X-Goog-FieldMask",
                    "routes.polyline.encodedPolyline,routes.optimizedIntermediateWaypointIndex"
                )
                contentType(ContentType.Application.Json)
                header("X-Goog-Api-Key", AppSecrets.mapsApiKey)
                setBody(request)
            }.body<RoutesResponse>())

        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }


    }
}