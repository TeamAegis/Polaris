package appcup.uom.polaris.features.polaris.data

import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.data.createJourneyFromExistingWaypointsPrompt
import appcup.uom.polaris.core.data.createJourneyFunctionCallingPrompt
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.RoutesApi
import appcup.uom.polaris.core.domain.RoutesResponse
import appcup.uom.polaris.core.domain.WeatherApi
import appcup.uom.polaris.core.domain.WeatherData
import appcup.uom.polaris.features.polaris.domain.GeneratedWaypoints
import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.JourneyStatus
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import appcup.uom.polaris.features.polaris.domain.Waypoint
import appcup.uom.polaris.features.polaris.domain.WaypointType
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.PublicPreviewAPI
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(PublicPreviewAPI::class)
class PolarisRepositoryImpl(
    private val routesApi: RoutesApi,
    private val weatherApi: WeatherApi,
    private val supabaseClient: SupabaseClient,
    private val locationManager: LocationManager,
    private val firebaseWaypointAiFunctionCallChat: Chat,
    private val firebaseWaypointGenerativeModel: GenerativeModel

) : PolarisRepository {
    override suspend fun getRoutePolyline(
        startingWaypoint: Waypoint,
        intermediaryWaypoints: List<Waypoint>,
        destinationWaypoint: Waypoint
    ): Result<RoutesResponse, DataError.JourneyError> {
        val result =
            routesApi.getRoutePolyline(startingWaypoint, intermediaryWaypoints, destinationWaypoint)
        return when (result) {
            is Result.Error -> {
                Result.Error(DataError.JourneyError.UNKNOWN)
            }

            is Result.Success -> {
                Result.Success(result.data)
            }

        }
    }


    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun createJourney(
        name: String,
        description: String,
        preferences: List<Preferences>,
        encodedPolyline: String,
        startingWaypoint: Waypoint,
        intermediaryWaypoints: List<Waypoint>,
        destinationWaypoint: Waypoint
    ): Result<Journey, DataError.JourneyError> {
        if (name.isBlank()) {
            return Result.Error(DataError.JourneyError.NAME_EMPTY)
        }
        if (description.isBlank()) {
            return Result.Error(DataError.JourneyError.DESCRIPTION_EMPTY)
        }
        if (destinationWaypoint.latitude == 0.0 && destinationWaypoint.longitude == 0.0) {
            return Result.Error(DataError.JourneyError.END_LOCATION_NOT_SET)
        }

        return try {
            val journey = supabaseClient.from("journeys").insert(
                    Journey(
                        name = name,
                        description = description,
                        preferences = preferences,
                        encodedPolyline = encodedPolyline,
                        status = JourneyStatus.NOT_STARTED,
                        userId = StaticData.user.id,
                        createdAt = Clock.System.now(),
                    )
                ) {
                    select()
                }.decodeSingle<Journey>()


            val waypoints = listOf(
                startingWaypoint.toPersonalWaypoint(
                    type = WaypointType.START,
                    isUnlocked = false,
                    userId = StaticData.user.id,
                    journeyId = journey.id!!,
                    id = null
                )
            ) + intermediaryWaypoints.map {
                it.toPersonalWaypoint(
                    type = WaypointType.INTERMEDIATE,
                    isUnlocked = false,
                    userId = StaticData.user.id,
                    journeyId = journey.id,
                    id = null
                )
            } + listOf(
                destinationWaypoint.toPersonalWaypoint(
                    type = WaypointType.END,
                    isUnlocked = false,
                    userId = StaticData.user.id,
                    journeyId = journey.id,
                    id = null
                )
            )
            supabaseClient.from("personal_waypoints").insert(
                waypoints
            )
            Result.Success(journey)

        } catch (e: Exception) {
            Result.Error(DataError.JourneyError.UNKNOWN)

        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getJourneys(): Flow<List<Journey>> = callbackFlow {
        val channel =
            supabaseClient.channel("journey_channel_${StaticData.user.id}_${Uuid.random()}")
        val journeyFlow = channel.postgresListDataFlow(
            schema = "public", table = "journeys", primaryKey = Journey::id
        )
        channel.subscribe()
        val job = launch {
            journeyFlow.collect { journeys ->
                trySend(journeys)
            }
        }

        awaitClose {
            job.cancel()
            runBlocking {
                supabaseClient.realtime.removeChannel(channel)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getJourney(id: Uuid): Result<Journey, DataError.JourneyError> {
        return try {
            Result.Success(supabaseClient.from("journeys").select().decodeSingle<Journey>())
        } catch (e: Exception) {
            Result.Error(DataError.JourneyError.UNKNOWN)
        }

    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteJourney(id: Uuid) {
        try {
            supabaseClient.from("journeys").delete {
                filter {
                    eq("id", id)
                }
            }
        } catch (e: Exception) {

        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getPersonalWaypoints(id: Uuid): Result<List<PersonalWaypoint>, DataError.JourneyError> {
        return try {
            Result.Success(supabaseClient.from("personal_waypoints").select {
                filter {
                    PersonalWaypoint::journeyId eq id
                }
            }.decodeList<PersonalWaypoint>())
        } catch (e: Exception) {
            Result.Error(DataError.JourneyError.UNKNOWN)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getAllMyWaypoints(): Flow<List<PersonalWaypoint>> = callbackFlow {
        val channel =
            supabaseClient.channel("personal_waypoints_channel_${StaticData.user.id}_${Uuid.random()}")
        val personalWaypointsFlow = channel.postgresListDataFlow(
            schema = "public",
            table = "personal_waypoints",
            primaryKey = PersonalWaypoint::id,
        )
        channel.subscribe()
        val job = launch {
            personalWaypointsFlow.collect { personalWaypoints ->
                trySend(personalWaypoints)
            }
        }

        awaitClose {
            job.cancel()
            runBlocking {
                supabaseClient.realtime.removeChannel(channel)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getPublicWaypoints(): Flow<List<PublicWaypoint>> = callbackFlow {
        val channel =
            supabaseClient.channel("public_waypoints_channel_${StaticData.user.id}_${Uuid.random()}")
        val publicWaypointsFlow = channel.postgresListDataFlow(
            schema = "public",
            table = "public_waypoints",
            primaryKey = PublicWaypoint::id,
        )
        channel.subscribe()
        val job = launch {
            publicWaypointsFlow.collect { publicWaypoints ->
                trySend(publicWaypoints)
            }
        }

        awaitClose {
            job.cancel()
            runBlocking {
                supabaseClient.realtime.removeChannel(channel)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun setPersonalWaypointsAsUnlocked(
        journey: Journey,
        unlockedWaypoints: List<PersonalWaypoint>,
        allWaypoints: List<PersonalWaypoint>
    ): Result<Unit, DataError.JourneyError> {
        return try {
            supabaseClient.from("personal_waypoints").update({
                PersonalWaypoint::isUnlocked setTo true
            }) {
                filter {
                    PersonalWaypoint::id isIn (unlockedWaypoints.map { it.id })
                }
            }

            val isStillInProgress =
                unlockedWaypoints.size < allWaypoints.filter { !it.isUnlocked }.size


            if (isStillInProgress && journey.status == JourneyStatus.NOT_STARTED) {
                supabaseClient.from("journeys").update({
                    Journey::status setTo JourneyStatus.IN_PROGRESS
                }) {
                    filter {
                        Journey::id eq journey.id
                    }
                }

            } else {
                supabaseClient.from("journeys").update({
                    Journey::status setTo JourneyStatus.COMPLETED
                }) {
                    filter {
                        Journey::id eq journey.id
                    }
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.JourneyError.UNKNOWN)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getStartableJourneys(ids: List<Uuid>): Result<List<Journey>, DataError.JourneyError> {
        return try {
            Result.Success(
                data = supabaseClient.from("journeys").select(columns = Columns.ALL) {
                    filter {
                        Journey::id isIn ids
                    }
                }.decodeList<Journey>()
            )
        } catch (e: Exception) {
            Result.Error(DataError.JourneyError.UNKNOWN)
        }
    }

    override suspend fun getWeatherData(
        latitude: Double, longitude: Double
    ): Result<WeatherData, DataError.Remote> {
        return weatherApi.getWeather(latitude, longitude)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun generateIntermediateWaypoints(
        name: String, description: String, preferences: List<Preferences>, encodedPolyline: String
    ): Result<GeneratedWaypoints, DataError.JourneyError> {
        if (name.isBlank()) {
            return Result.Error(DataError.JourneyError.NAME_EMPTY)
        }
        if (description.isBlank()) {
            return Result.Error(DataError.JourneyError.DESCRIPTION_EMPTY)
        }

        return try {
            var prompt = createJourneyFunctionCallingPrompt(
                journeyName = name,
                journeyDescription = description,
                userPreference = preferences.joinToString { it.name },
                encodedPolyline = encodedPolyline
            )

            val result = firebaseWaypointAiFunctionCallChat.sendMessage(prompt)


            val functionResponses = coroutineScope {
                result.functionCalls.map { functionCall ->
                    async {
                        if (functionCall.name == "getNearbyPlacesAlongRoute") {
                            val searchQuery =
                                functionCall.args.getOrDefault("searchQuery", "landmarks")

                            locationManager.getNearbyPlacesAlongRoute(
                                searchQuery.toString(), encodedPolyline
                            )
                        } else {
                            emptyList()
                        }
                    }
                }.awaitAll().flatten()
            }

            prompt = createJourneyFromExistingWaypointsPrompt(
                journeyName = name,
                journeyDescription = description,
                userPreference = preferences.joinToString { it.name },
                encodedPolyline = encodedPolyline,
                intermediateWaypoints = functionResponses.toString()
            )

            val res = firebaseWaypointGenerativeModel.generateContent(prompt)

            val content = Json.decodeFromString(GeneratedWaypoints.serializer(), res.text!!)


            Result.Success(
                content.copy(
                waypoints = content.waypoints.map {
                    it.copy(
                        id = Uuid.random()
                    )
                }))
        } catch (e: Exception) {
            Result.Error(DataError.JourneyError.UNKNOWN)
        }
    }
}