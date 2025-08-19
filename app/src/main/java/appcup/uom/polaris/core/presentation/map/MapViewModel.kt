package appcup.uom.polaris.core.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.data.waypointReachTriggerPrompt
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.core.domain.LatLong
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.ResultState
import appcup.uom.polaris.core.domain.WeatherData
import appcup.uom.polaris.features.conversational_ai.domain.Value
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import appcup.uom.polaris.features.polaris.domain.toWaypoint
import appcup.uom.polaris.features.quest.domain.QuestRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, FlowPreview::class)
class MapViewModel(
    private val locationManager: LocationManager,
    private val polarisRepository: PolarisRepository,
    private val questRepository: QuestRepository,
    private val json: Json
) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MapEvent>()
    val event = _event.asSharedFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            val quests = questRepository.fetchAllPendingQuests()
            _state.update {
                it.copy(
                    quests = quests
                )
            }
        }

        viewModelScope.launch {
            locationManager.getCoordinates { latitude, longitude ->
                if (latitude != null && longitude != null && _state.value.isMapLoaded) {
                    _state.value.currentCameraPositionState.move(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder(_state.value.currentCameraPositionState.position)
                                .target(LatLng(latitude, longitude)).build()
                        )
                    )
                    locationManager.getOrientationFlow().onEach { orientation ->
                        _state.update {
                            it.copy(
                                bearing = orientation.headingDegrees
                            )
                        }
                        if (_state.value.isTrackingUser && _state.value.isMapLoaded) {
                            _state.value.currentCameraPositionState.move(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.builder(_state.value.currentCameraPositionState.position)
                                        .bearing(orientation.headingDegrees).build()
                                )
                            )
                        }
                    }.launchIn(viewModelScope + Dispatchers.Main)
                }
            }
        }

        viewModelScope.launch {
            locationManager.getLocationUpdatesFlow(500L).collect { result ->
                when (result) {
                    is ResultState.Failure -> {}
                    ResultState.Loading -> {}
                    is ResultState.Success<LatLong> -> {
                        val (latitude, longitude) = result.data

                        if (_state.value.isTrackingUser && _state.value.isMapLoaded) {
                            _state.value.currentCameraPositionState.move(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.builder(
                                        _state.value.currentCameraPositionState.position
                                    ).target(
                                        LatLng(
                                            latitude, longitude
                                        )
                                    ).build()
                                )
                            )
                        }
                        _state.update {
                            it.copy(
                                currentLocation = _state.value.currentLocation.copy(
                                    latitude = latitude, longitude = longitude
                                ),
                                currentMarkerState = _state.value.currentMarkerState.apply {
                                    position = LatLng(
                                        latitude, longitude
                                    )
                                },
                                discoveredPublicWaypoints = _state.value.publicWaypoints.filter { waypoint ->
                                    SphericalUtil.computeDistanceBetween(
                                        LatLng(
                                            latitude, longitude
                                        ), LatLng(
                                            waypoint.latitude, waypoint.longitude
                                        )
                                    ) <= Constants.MAP_FRAGMENT_DISCOVERY_RADIUS_IN_METRES
                                })
                        }
                        if (_state.value.selectedJourney != null) {
                            checkAndUpdatePersonalWaypointsUnlockStatus(
                                latitude, longitude
                            )
                        } else if (_state.value.shouldShowStartJourneyDialog) {
                            showStartJourneyPrompt(
                                latitude, longitude
                            )
                        }

                        checkAndUpdateQuestStatus(latitude, longitude)
                    }
                }
            }
        }

        viewModelScope.launch {
            polarisRepository.getAllMyWaypoints().collect { waypoints ->
                val currentSelectedJourney = _state.value.selectedJourney

                if (currentSelectedJourney != null) {
                    val waypointsForSelectedJourney =
                        waypoints.filter { it.journeyId == currentSelectedJourney.id }

                    val isJourneyCompleted =
                        waypointsForSelectedJourney.isNotEmpty() && waypointsForSelectedJourney.all { it.isUnlocked }

                    if (isJourneyCompleted) {
                        _state.update {
                            it.copy(
                                allMyWaypoints = waypoints,
                                waypointsForSelectedJourney = emptyList(),
                                selectedJourney = null,
                            )
                        }
                        _event.emit(MapEvent.OnJourneyCompleted)
                    } else {
                        _state.update {
                            it.copy(
                                allMyWaypoints = waypoints,
                                waypointsForSelectedJourney = waypointsForSelectedJourney
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            allMyWaypoints = waypoints,
                            isSelectedWaypointCardVisible = false,
                            selectedWaypoint = null,
                            selectedWeatherData = null
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            polarisRepository.getPublicWaypoints().collect { waypoints ->
                _state.update {
                    it.copy(
                        publicWaypoints = waypoints
                    )
                }
            }
        }

    }

    fun onAction(action: MapActions) {
        when (action) {

            is MapActions.OnTrackingUserChanged -> {
                if (_state.value.isMapLoaded)
                    viewModelScope.launch {
                        _state.update {
                            it.copy(
                                isAnimatingCamera = true, isTrackingUser = false
                            )
                        }
                        _state.value.currentCameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder(
                                    _state.value.currentCameraPositionState.position
                                ).target(
                                    LatLng(
                                        _state.value.currentLocation.latitude,
                                        _state.value.currentLocation.longitude
                                    )
                                ).bearing(
                                    if (action.isTrackingUser) _state.value.bearing else 0f
                                )
                                    .zoom(if (action.isTrackingUser) Constants.MAP_DEFAULT_ZOOM else 10f)
                                    .tilt(if (action.isTrackingUser) Constants.MAP_DEFAULT_TILT else 0f)
                                    .build()
                            )
                        )
                        _state.update {
                            it.copy(
                                isAnimatingCamera = false, isTrackingUser = action.isTrackingUser
                            )
                        }
                    }
            }

            MapActions.OnCompassClicked -> {
                if (_state.value.isMapLoaded)
                    viewModelScope.launch {
                        _state.update {
                            it.copy(
                                isTrackingUser = false, isAnimatingCamera = true
                            )
                        }
                        _state.value.currentCameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder(
                                    _state.value.currentCameraPositionState.position
                                ).target(
                                    LatLng(
                                        _state.value.currentLocation.latitude,
                                        _state.value.currentLocation.longitude
                                    )
                                ).bearing(
                                    0f
                                ).zoom(10f).tilt(0f).build()
                            )
                        )
                        _state.update {
                            it.copy(
                                isAnimatingCamera = false
                            )
                        }
                    }
            }

            is MapActions.OnStartJourneyClicked -> {
                _state.update {
                    it.copy(
                        selectedJourney = action.journey,
                        waypointsForSelectedJourney = _state.value.allMyWaypoints.filter { waypoint ->
                            waypoint.journeyId == action.journey.id
                        },
                        startableJourneys = emptyList()
                    )
                }
            }

            MapActions.OnStopJourneyClicked -> {
                _state.update {
                    it.copy(
                        selectedJourney = null, waypointsForSelectedJourney = emptyList()
                    )
                }
            }

            is MapActions.OnJourneyCompletedDialogVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isJourneyCompleted = action.isVisible
                    )
                }
            }

            MapActions.OnToggleShowStartJourneyDialog -> {
                _state.update {
                    it.copy(
                        shouldShowStartJourneyDialog = !_state.value.shouldShowStartJourneyDialog
                    )
                }
            }

            is MapActions.OnPersonalWaypointClicked -> {
                _state.update {
                    it.copy(
                        selectedWaypoint = null,
                        selectedWeatherData = null,
                        isSelectedWaypointCardVisible = true,
                    )
                }
                viewModelScope.launch {
                    showPlaceOnMap(action.waypoint)
                }
            }

            MapActions.OnTrackingWaypointCardDismissed -> {
                _state.update {
                    it.copy(
                        isSelectedWaypointCardVisible = false,
                        selectedWaypoint = null,
                        selectedWeatherData = null
                    )
                }
            }

            MapActions.OnToggleQuests -> {
                _state.update {
                    it.copy(
                        isQuestsVisible = !_state.value.isQuestsVisible
                    )
                }
            }

            MapActions.OnMapLoaded -> {
                _state.update {
                    it.copy(
                        isMapLoaded = true
                    )
                }
            }

            else -> {}
        }

    }

    suspend fun CoroutineScope.checkAndUpdateQuestStatus(
        latitude: Double, longitude: Double
    ) {
        val completedQuests = _state.value.quests.filter { quest ->
            SphericalUtil.computeDistanceBetween(
                LatLng(
                    latitude, longitude
                ), LatLng(
                    quest.latitude, quest.longitude
                )
            ) <= Constants.MAP_SET_TO_UNLOCKED_RADIUS_IN_METRES
        }

        if (completedQuests.isNotEmpty()) {
            completedQuests.map { quest ->
                async {
                    questRepository.setQuestCompleted(quest.id ?: 0, quest.type)
                }
            }.awaitAll()
            _state.update {
                it.copy(
                    quests = questRepository.fetchAllPendingQuests()
                )
            }
        }

    }

    suspend fun checkAndUpdatePersonalWaypointsUnlockStatus(
        latitude: Double, longitude: Double
    ) {
        val unlockedWaypoints = _state.value.waypointsForSelectedJourney.filter { waypoint ->
            !waypoint.isUnlocked && SphericalUtil.computeDistanceBetween(
                LatLng(
                    latitude, longitude
                ), LatLng(
                    waypoint.latitude, waypoint.longitude
                )
            ) <= Constants.MAP_SET_TO_UNLOCKED_RADIUS_IN_METRES
        }

        if (unlockedWaypoints.isNotEmpty()) {
            val result = polarisRepository.setPersonalWaypointsAsUnlocked(
                journey = _state.value.selectedJourney!!,
                unlockedWaypoints = unlockedWaypoints,
                allWaypoints = _state.value.waypointsForSelectedJourney
            )
            when (result) {
                is Result.Error<DataError.JourneyError> -> {

                }

                is Result.Success<Unit> -> {
                    val nearbyPlaces = locationManager.nearbySearchPlaces(50.0)
                    val message = waypointReachTriggerPrompt(
                        _state.value.currentLocation.toString(), nearbyPlaces.toString()

                    )
                    EventBus.emit(Event.OnWaypointUnlocked(message))
                }
            }
        }
    }

    suspend fun showStartJourneyPrompt(
        latitude: Double, longitude: Double
    ) {
        val startableJourneyIds = _state.value.allMyWaypoints.groupBy { it.journeyId }
            .filter { it.value.any { waypoint -> !waypoint.isUnlocked } }.filter {
                it.value.any { waypoint ->
                    SphericalUtil.computeDistanceBetween(
                        LatLng(
                            latitude, longitude
                        ), LatLng(
                            waypoint.latitude, waypoint.longitude
                        )
                    ) <= Constants.MAP_STARTING_PROMPT_RADIUS_IN_METRES
                }
            }.map { it.key }


        if (startableJourneyIds.isEmpty()) {
            _state.update {
                it.copy(
                    startableJourneys = emptyList()
                )
            }
            return
        }

        val result = polarisRepository.getStartableJourneys(startableJourneyIds)
        when (result) {
            is Result.Error<DataError.JourneyError> -> {}
            is Result.Success<List<Journey>> -> {
                _state.update {
                    it.copy(
                        startableJourneys = result.data
                    )
                }
            }
        }
    }


    fun showPlaceOnMap(waypoint: PersonalWaypoint) {
        if (waypoint.placeId == null) {
            viewModelScope.launch {
                val result = polarisRepository.getWeatherData(waypoint.latitude, waypoint.longitude)
                when (result) {
                    is Result.Error<DataError.Remote> -> {
                        _state.update {
                            it.copy(
                                selectedWaypoint = waypoint.toWaypoint()
                            )
                        }
                    }

                    is Result.Success<WeatherData> -> {
                        _state.update {
                            it.copy(
                                selectedWaypoint = waypoint.toWaypoint(),
                                selectedWeatherData = result.data
                            )
                        }
                    }
                }
            }
            return
        }

        locationManager.getWaypointByPlaceId(waypoint.placeId) { placeInfo ->
            viewModelScope.launch {
                val result = if (placeInfo == null) {
                    polarisRepository.getWeatherData(waypoint.latitude, waypoint.longitude)
                } else {
                    polarisRepository.getWeatherData(placeInfo.latitude, placeInfo.longitude)
                }

                when (result) {
                    is Result.Error<DataError.Remote> -> {
                        _state.update {
                            it.copy(
                                selectedWaypoint = placeInfo?.copy(
                                    id = waypoint.id ?: Uuid.NIL,
                                ) ?: waypoint.toWaypoint()
                            )
                        }
                    }

                    is Result.Success<WeatherData> -> {
                        _state.update {
                            it.copy(
                                selectedWaypoint = placeInfo?.copy(
                                    id = waypoint.id ?: Uuid.NIL,
                                ) ?: waypoint.toWaypoint(), selectedWeatherData = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    fun isFragmentInteractable(waypoint: PublicWaypoint): Boolean {
        return SphericalUtil.computeDistanceBetween(
            LatLng(
                _state.value.currentLocation.latitude, _state.value.currentLocation.longitude
            ), LatLng(
                waypoint.latitude, waypoint.longitude
            )
        ) <= Constants.MAP_FRAGMENT_INTERACTABLE_RADIUS_IN_METRES
    }


    fun canUserCreatePublicWaypoint() = _state.value.discoveredPublicWaypoints.none { waypoint ->
        SphericalUtil.computeDistanceBetween(
            LatLng(
                _state.value.currentLocation.latitude, _state.value.currentLocation.longitude
            ), LatLng(
                waypoint.latitude, waypoint.longitude
            )
        ) <= Constants.MAP_FRAGMENT_CREATION_RADIUS_IN_METRES
    }

    suspend fun getStartableJourneys(): Map<String, Value> {
        val startableJourneyIds = _state.value.allMyWaypoints.groupBy { it.journeyId }
            .filter { it.value.any { waypoint -> !waypoint.isUnlocked } }.map { it.key }


        if (startableJourneyIds.isEmpty()) {
            return mapOf(
                "journeys" to Value.Str("No startable journeys")
            )
        }

        val result = polarisRepository.getStartableJourneys(startableJourneyIds)
        return when (result) {
            is Result.Error<DataError.JourneyError> -> {
                mapOf(
                    "journeys" to Value.Str("No startable journeys")
                )
            }

            is Result.Success<List<Journey>> -> {
                mapOf(
                    "journeys" to Value.Str(result.data.toString())
                )
            }
        }
    }

    suspend fun startJourney(journeyId: String): Map<String, Value> {
        val id = Uuid.parse(journeyId)
        val result = polarisRepository.getStartableJourneys(listOf(id))
        return when (result) {
            is Result.Error<DataError.JourneyError> -> {
                mapOf(
                    "journeys" to Value.Str("No startable journeys")
                )
            }

            is Result.Success<List<Journey>> -> {
                val journey = result.data.firstOrNull()

                if (journey == null) {
                    mapOf(
                        "result" to Value.Str("No startable journeys")
                    )
                } else {
                    _state.update {
                        it.copy(
                            selectedJourney = journey,
                            waypointsForSelectedJourney = _state.value.allMyWaypoints.filter { waypoint ->
                                waypoint.journeyId == id
                            },
                            startableJourneys = emptyList()
                        )
                    }
                    mapOf(
                        "result" to Value.Str("Journey started")
                    )
                }


            }
        }
    }

    fun stopJourney(): Map<String, Value> {
        _state.update {
            it.copy(
                selectedJourney = null, waypointsForSelectedJourney = emptyList()
            )
        }
        return mapOf(
            "result" to Value.Str("Journey stopped")
        )
    }

    fun getUserCurrentLocation(onResult: (Map<String, Value>) -> Unit) {
        locationManager.getAddressAndCoordinates { address, latitude, longitude ->
            if (latitude == null || longitude == null || address == null) {
                onResult(
                    mapOf(
                        "result" to Value.Str("Error getting location")
                    )
                )
                return@getAddressAndCoordinates
            }

            onResult(
                mapOf(
                    "address" to Value.Str(address),
                    "latitude" to Value.Number(latitude),
                    "longitude" to Value.Number(longitude)
                )
            )
        }
    }

    suspend fun getNearbyPlaces(radius: Double): Map<String, Value> {
        return try {
            val nearbyPlaces = locationManager.nearbySearchPlaces(radius)
            mapOf(
                "result" to Value.Str(nearbyPlaces.toString())
            )
        } catch (e: Exception) {
            mapOf(
                "result" to Value.Str("Error getting nearby places: ${e.message}")
            )
        }
    }

    suspend fun searchPlaces(query: String): Map<String, Value> {
        return try {
            val nearbyPlaces = locationManager.searchPlaces(query)
            mapOf(
                "result" to Value.Str(nearbyPlaces.toString())
            )
        } catch (e: Exception) {
            mapOf(
                "result" to Value.Str("Error getting nearby places: ${e.message}")
            )
        }
    }

    fun onWaypointReceived(placeId: String, onResult: (Map<String, Value>) -> Unit) {
        locationManager.getWaypointByPlaceId(placeId) { placeInfo ->
            if (placeInfo == null) {
                onResult(
                    mapOf(
                        "result" to Value.Str("Error getting place info")
                    )
                )
                return@getWaypointByPlaceId
            }
            _state.update {
                it.copy(
                    isSelectedWaypointCardVisible = true,
                    selectedWaypoint = placeInfo.copy(
                        id = Uuid.NIL
                    )
                )
            }
            onResult(
                mapOf(
                    "result" to Value.Str("Waypoint received")
                )
            )
        }
    }
}