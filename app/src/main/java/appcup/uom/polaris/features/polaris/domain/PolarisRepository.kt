package appcup.uom.polaris.features.polaris.domain

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.RoutesResponse
import appcup.uom.polaris.core.domain.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface PolarisRepository {
    suspend fun getRoutePolyline(
        startingWaypoint: Waypoint,
        intermediaryWaypoints: List<Waypoint>,
        destinationWaypoint: Waypoint
    ): Result<RoutesResponse, DataError.JourneyError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun createJourney(
        name: String,
        description: String,
        preferences: List<Preferences>,
        encodedPolyline: String,
        startingWaypoint: Waypoint,
        intermediaryWaypoints: List<Waypoint>,
        destinationWaypoint: Waypoint
    ): Result<Journey, DataError.JourneyError>

    fun getJourneys(): Flow<List<Journey>>

    fun getAllMyWaypoints(): Flow<List<PersonalWaypoint>>

    fun getPublicWaypoints(): Flow<List<PublicWaypoint>>

    suspend fun setPersonalWaypointsAsUnlocked(journey: Journey, unlockedWaypoints: List<PersonalWaypoint>, allWaypoints: List<PersonalWaypoint>): Result<Unit, DataError.JourneyError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getStartableJourneys(ids: List<Uuid>): Result<List<Journey>, DataError.JourneyError>

    suspend fun getWeatherData(latitude: Double, longitude: Double): Result<WeatherData, DataError.Remote>


}