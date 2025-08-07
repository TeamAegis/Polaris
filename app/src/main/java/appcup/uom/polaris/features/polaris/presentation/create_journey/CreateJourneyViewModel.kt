package appcup.uom.polaris.features.polaris.presentation.create_journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.features.polaris.data.LocationManager
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CreateJourneyViewModel(
    locationManager: LocationManager
) : ViewModel() {
    private val _state = MutableStateFlow(CreateJourneyState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            locationManager.getCoordinates { latitude, longitude ->
                if (latitude != null && longitude != null) {
                    if (!_state.value.isStartingLocationCustom) {
                        _state.update {
                            it.copy(
                                startingLocation = _state.value.startingLocation.copy(
                                    latitude = latitude,
                                    longitude = longitude
                                ),
                                startingMarkerState = MarkerState(
                                    position = LatLng(
                                        latitude,
                                        longitude
                                    )
                                ),
                                startingCameraPositionState = CameraPositionState(
                                    position = CameraPosition.fromLatLngZoom(
                                        LatLng(
                                            latitude,
                                            longitude
                                        ), 15f
                                    )
                                )
                            )
                        }
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

            is CreateJourneyAction.OnStartingLocationCustomChanged -> {
                _state.update {
                    it.copy(
                        isStartingLocationCustom = action.isCustom
                    )
                }
            }

            is CreateJourneyAction.OnEndingLocationCustomChanged -> {
                _state.update {
                    it.copy(
                        isEndingLocationCustom = action.isCustom
                    )
                }
            }

            is CreateJourneyAction.OnStartingLocationVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isStartingLocationSelectorVisible = action.isVisible
                    )
                }
            }

            is CreateJourneyAction.OnEndingLocationVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isEndingLocationSelectorVisible = action.isVisible
                    )
                }
            }

            else -> {}
        }
    }

}