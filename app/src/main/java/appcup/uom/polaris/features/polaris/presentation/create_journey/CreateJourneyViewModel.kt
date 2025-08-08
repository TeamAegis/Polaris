package appcup.uom.polaris.features.polaris.presentation.create_journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.RoutesResponse
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import appcup.uom.polaris.features.polaris.domain.WaypointSelectorType
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateJourneyViewModel(
    locationManager: LocationManager,
    private val polarisRepository: PolarisRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CreateJourneyState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<CreateJourneyEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            locationManager.getAddressAndCoordinates { address, latitude, longitude ->
                if (latitude != null && longitude != null) {
                    _state.update {
                        it.copy(
                            startingWaypoint = _state.value.startingWaypoint.copy(
                                latitude = latitude,
                                longitude = longitude,
                                address = address ?: "Unknown address"
                            ),
                            startingMarkerState = MarkerState(
                                position = LatLng(
                                    latitude,
                                    longitude
                                )
                            ),
                            cameraPositionState = CameraPositionState(
                                position = CameraPosition.fromLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), Constants.MAP_DEFAULT_ZOOM
                                )
                            )
                        )
                    }

                }
            }
        }
    }

    fun onAction(action: CreateJourneyAction) {
        when (action) {
            is CreateJourneyAction.OnToolbarExpandedChanged -> {
                _state.update {
                    it.copy(
                        isToolbarExpanded = action.isExpanded
                    )
                }
            }

            CreateJourneyAction.OnSendMessageToAIBottomSheetClicked -> {
                _state.update {
                    it.copy(
                        isSendMessageToAIBottomSheetOpen = !_state.value.isSendMessageToAIBottomSheetOpen
                    )
                }
            }

            CreateJourneyAction.OnSendMessageToLiveAgentBottomSheetClicked -> {
                _state.update {
                    it.copy(
                        isSendMessageToLiveAgentBottomSheetOpen = !_state.value.isSendMessageToLiveAgentBottomSheetOpen
                    )
                }
            }

            is CreateJourneyAction.OnJourneyNameChanged -> {
                _state.update {
                    it.copy(
                        journeyName = action.name
                    )
                }
            }

            is CreateJourneyAction.OnJourneyDescriptionChanged -> {
                _state.update {
                    it.copy(
                        journeyDescription = action.description
                    )
                }
            }

            is CreateJourneyAction.OnPreferencesAdded -> {
                _state.update {
                    it.copy(
                        selectedPreferences = it.selectedPreferences + action.preference
                    )
                }
            }

            is CreateJourneyAction.OnPreferencesRemoved -> {
                _state.update {
                    it.copy(
                        selectedPreferences = it.selectedPreferences - action.preference
                    )
                }
            }

            is CreateJourneyAction.OnWaypointSelectorVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isWaypointSelectorVisible = action.isVisible,
                        waypointSelectorType = action.waypointSelectorType
                    )
                }
            }

            is CreateJourneyAction.OnWaypointSelectorResult -> {
                when (_state.value.waypointSelectorType) {
                    WaypointSelectorType.STARTING -> {
                        _state.update {
                            it.copy(
                                startingWaypoint = action.waypoint,
                                startingMarkerState = _state.value.startingMarkerState.apply {
                                    position =
                                        LatLng(action.waypoint.latitude, action.waypoint.longitude)
                                },
                            )
                        }
                    }

                    WaypointSelectorType.INTERMEDIATE -> {
                        _state.update {
                            it.copy(
                                intermediateWaypoints = it.intermediateWaypoints + action.waypoint,
                                intermediateMarkerStates = it.intermediateMarkerStates + MarkerState(
                                    position = LatLng(
                                        action.waypoint.latitude,
                                        action.waypoint.longitude
                                    )

                                )
                            )
                        }
                    }

                    WaypointSelectorType.ENDING -> {
                        _state.update {
                            it.copy(
                                endingWaypoint = action.waypoint,
                                endingMarkerState = MarkerState(
                                    position = LatLng(
                                        action.waypoint.latitude,
                                        action.waypoint.longitude
                                    )
                                ),
                            )
                        }
                    }
                }
                getJourneyPolyline()
            }

            is CreateJourneyAction.OnIntermediateWaypointRemoved -> {
                _state.update {
                    it.copy(
                        intermediateWaypoints = it.intermediateWaypoints.filterIndexed { index, _ ->
                            index != action.index
                        },
                        intermediateMarkerStates = it.intermediateMarkerStates.filterIndexed { index, _ ->
                            index != action.index
                        }
                    )
                }
                getJourneyPolyline()
            }

            else -> {}
        }
    }

    fun getJourneyPolyline() {
        if (_state.value.endingWaypoint.id == Uuid.NIL) return

        viewModelScope.launch(Dispatchers.IO) {
            val response = polarisRepository.getRoutePolyline(
                startingWaypoint = _state.value.startingWaypoint,
                intermediaryWaypoints = _state.value.intermediateWaypoints,
                destinationWaypoint = _state.value.endingWaypoint
            )
            when (response) {
                is Result.Error<DataError.Remote> -> {
                    _event.emit(CreateJourneyEvent.OnError(response.error.message))
                }

                is Result.Success<RoutesResponse> -> {
                    if (response.data.routes.isEmpty()) return@launch
                    val optimizedIntermediateWaypointsIndex = response.data.routes.first().optimizedIntermediateWaypointIndex

                    if (optimizedIntermediateWaypointsIndex != null && optimizedIntermediateWaypointsIndex.size > 1) {
                        _state.update {
                            it.copy(
                                polyline = PolyUtil.decode(response.data.routes.first().polyline.encodedPolyline),
                                intermediateWaypoints = optimizedIntermediateWaypointsIndex.map { index ->
                                    _state.value.intermediateWaypoints[index]
                                },
                                intermediateMarkerStates = optimizedIntermediateWaypointsIndex.map { index -> _state.value.intermediateMarkerStates[index] }
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                polyline = PolyUtil.decode(response.data.routes.first().polyline.encodedPolyline)
                            )
                        }
                    }
                }
            }
        }

    }
}