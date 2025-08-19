package appcup.uom.polaris.features.polaris.presentation.waypoint_selector

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.domain.Waypoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi

@OptIn(FlowPreview::class, ExperimentalUuidApi::class)
class WaypointSelectorViewModel(
    private val locationManager: LocationManager,
    placesClient: PlacesClient
) : ViewModel() {

    private val _state = MutableStateFlow(WaypointSelectorState())
    val state = _state.asStateFlow()

    private val _searchState = MutableStateFlow(TextFieldState(""))
    val searchState = _searchState.asStateFlow()

    init {
        viewModelScope.launch {
            locationManager.getAddressAndCoordinates { address, latitude, longitude ->
                if (latitude != null && longitude != null) {
                    _state.update {
                        it.copy(
                            selectedWaypoint = _state.value.selectedWaypoint?.copy(
                                latitude = latitude,
                                longitude = longitude,
                                address = address ?: "Unknown address"
                            ),
                            waypointMarkerState = MarkerState(
                                position = LatLng(
                                    latitude,
                                    longitude
                                )
                            ),
                            waypointCameraPositionState = CameraPositionState(
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

        viewModelScope.launch {
            _searchState.debounce(500.milliseconds).collect { query: TextFieldState ->
                val response = placesClient.awaitFindAutocompletePredictions {
//                locationBias = bias
//                typesFilter = listOf(
//                    PlaceTypes.ESTABLISHMENT
//                )
                    this.query = query.text.toString()
                    countries = listOf(Constants.MAP_COUNTRY_BIAS)
                }
                _state.update {
                    it.copy(
                        isSearching = false,
                        predictions = response.autocompletePredictions
                    )
                }
            }
        }
    }


    fun onAction(action: WaypointSelectorAction) {
        when (action) {
            is WaypointSelectorAction.OnSelectedPlaceChanged -> {
                _state.update {
                    it.copy(
                        expanded = false
                    )
                }

                showPlaceOnMap(action.selectedPlace?.placeId)

            }

            is WaypointSelectorAction.OnSearchQueryChanged -> {
                _searchState.update {
                    TextFieldState(action.searchQuery)
                }
                _state.update {
                    it.copy(
                        isSearching = true
                    )
                }
            }

            is WaypointSelectorAction.OnSearchExpandedChanged -> {
                _state.update {
                    it.copy(
                        expanded = action.expanded
                    )
                }
            }

            WaypointSelectorAction.SetToCurrentLocation -> {
                setToCurrentLocation()
            }

            is WaypointSelectorAction.OnMapClick -> {
                onMapClick(action.latitude, action.longitude)
            }

            is WaypointSelectorAction.OnPoiClick -> {
                showPlaceOnMap(action.placeId)
            }

            WaypointSelectorAction.OnMapLoaded -> {
                _state.update {
                    it.copy(
                        isMapLoaded = true
                    )
                }
            }

            else -> {}
        }

    }

    fun onMapClick(latitude: Double, longitude: Double) {
        locationManager.getAddressFromLocation(latitude, longitude) { address ->
            _state.update {
                it.copy(
                    selectedWaypoint = Waypoint().copy(
                        latitude = latitude,
                        longitude = longitude,
                        address = address ?: "Unknown address"
                    ),
                    waypointMarkerState = MarkerState(
                        position = LatLng(
                            latitude,
                            longitude
                        )
                    )
                )
            }
            if (_state.value.isMapLoaded && !_state.value.isAnimating)
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isAnimating = true
                        )
                    }
                    _state.value.waypointCameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                LatLng(
                                    latitude,
                                    longitude
                                ), Constants.MAP_DEFAULT_ZOOM
                            )
                        )
                    )
                    _state.update {
                        it.copy(
                            isAnimating = false
                        )
                    }
                }

        }
    }

    fun showPlaceOnMap(placeId: String?) {
        if (placeId == null) return

        locationManager.getWaypointByPlaceId(placeId) { placeInfo ->
            if (placeInfo == null) return@getWaypointByPlaceId

            _state.update {
                it.copy(
                    waypointMarkerState = _state.value.waypointMarkerState.apply {
                        position = LatLng(placeInfo.latitude, placeInfo.longitude)
                    },
                    selectedWaypoint = placeInfo
                )
            }

            if (_state.value.isMapLoaded && !_state.value.isAnimating)
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isAnimating = true
                        )
                    }
                    _state.value.waypointCameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                LatLng(
                                    placeInfo.latitude,
                                    placeInfo.longitude
                                ), Constants.MAP_DEFAULT_ZOOM
                            )
                        )
                    )
                    _state.update {
                        it.copy(
                            isAnimating = false
                        )
                    }
                }
        }
    }

    fun setToCurrentLocation() {
        viewModelScope.launch {
            locationManager.getAddressAndCoordinates { address, latitude, longitude ->
                if (latitude != null && longitude != null) {
                    _state.update {
                        it.copy(
                            waypointMarkerState = MarkerState(
                                position = LatLng(
                                    latitude,
                                    longitude
                                )
                            ),
                            selectedWaypoint = Waypoint().copy(
                                latitude = latitude,
                                longitude = longitude,
                                address = address ?: "Unknown address"
                            )
                        )
                    }
                }

                if (_state.value.isMapLoaded && !_state.value.isAnimating)
                    viewModelScope.launch {
                        _state.update {
                            it.copy(
                                isAnimating = true
                            )
                        }
                        _state.value.waypointCameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(
                                    LatLng(
                                        latitude!!,
                                        longitude!!
                                    ), Constants.MAP_DEFAULT_ZOOM
                                )
                            )
                        )
                        _state.update {
                            it.copy(
                                isAnimating = false
                            )
                        }
                    }
            }
        }
    }

}