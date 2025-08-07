package appcup.uom.polaris.core.presentation.map

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.domain.LatLong
import appcup.uom.polaris.core.domain.ResultState
import appcup.uom.polaris.features.polaris.data.LocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, FlowPreview::class)
class MapViewModel(
    locationManager: LocationManager,
    placesClient: PlacesClient
) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    private val _searchState = MutableStateFlow(TextFieldState(""))
    val searchState = _searchState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                locationManager.getOrientationFlow().sample(500),
                locationManager.getLocationUpdatesFlow(500L)
            ){
                orientation, location ->
                orientation to location
            }.collect { (orientation, result) ->
                when (result) {
                    is ResultState.Failure -> {}
                    ResultState.Loading -> {}
                    is ResultState.Success<LatLong> -> {
                        _state.update {
                            it.copy(
                                isMapInitialized = true
                            )
                        }
                        val (latitude, longitude) = result.data

                        if (_state.value.isTrackingUser) {
                            _state.value.currentCameraPositionState.move(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(
                                        LatLng(
                                            latitude,
                                            longitude
                                        ),
                                        _state.value.currentCameraPositionState.position.zoom,
                                        _state.value.currentCameraPositionState.position.tilt,
                                        orientation.azimuth
                                    )
                                )
                            )
                        }
                        _state.update {
                            it.copy(
                                currentLocation = _state.value.currentLocation.copy(
                                    latitude = latitude,
                                    longitude = longitude
                                ),
                                currentMarkerState = _state.value.currentMarkerState.apply {
                                    position = LatLng(
                                        latitude,
                                        longitude
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }


//        viewModelScope.launch {
//            locationManager.getOrientationFlow().sample(1000).collect { orientation ->
//                println(Constants.DEBUG_VALUE + orientation)
//                if (_state.value.isMapInitialized)
//                    _state.value.currentCameraPositionState.move(
//                        update = CameraUpdateFactory.newCameraPosition(
//                            CameraPosition.builder().bearing(orientation.azimuth).build()
//                        )
//                    )
////                    _state.value.currentCameraPositionState.animate(
////                        update = CameraUpdateFactory.newCameraPosition(
////                            CameraPosition(
////                                LatLng(
////                                    _state.value.currentCameraPositionState.position.target.latitude,
////                                    _state.value.currentCameraPositionState.position.target.longitude
////                                ),
////                                _state.value.currentCameraPositionState.position.zoom,
////                                _state.value.currentCameraPositionState.position.tilt,
////                                orientation.azimuth
////                            )
////                        )
////                    )
//            }
//        }
//        viewModelScope.launch {
//            locationManager.getLocationUpdatesFlow(500L).collect { result ->
//                when (result) {
//                    is ResultState.Failure -> {}
//                    ResultState.Loading -> {}
//                    is ResultState.Success<LatLong> -> {
//                        _state.update {
//                            it.copy(
//                                isMapInitialized = true
//                            )
//                        }
//                        val (latitude, longitude) = result.data
//
//                        if (_state.value.isTrackingUser) {
//                            _state.value.currentCameraPositionState.move(
//                                update = CameraUpdateFactory.newCameraPosition(
//                                    CameraPosition(
//                                        LatLng(
//                                            latitude,
//                                            longitude
//                                        ),
//                                        _state.value.currentCameraPositionState.position.zoom,
//                                        _state.value.currentCameraPositionState.position.tilt,
//                                        _state.value.currentCameraPositionState.position.bearing
//                                    )
//                                )
//                            )
//                        }
//                        _state.update {
//                            it.copy(
//                                currentLocation = _state.value.currentLocation.copy(
//                                    latitude = latitude,
//                                    longitude = longitude
//                                ),
//                                currentMarkerState = _state.value.currentMarkerState.apply {
//                                    position = LatLng(
//                                        latitude,
//                                        longitude
//                                    )
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
        viewModelScope.launch {
            _searchState.debounce(500.milliseconds).collect { query: TextFieldState ->
                println(Constants.DEBUG_VALUE + query.text.toString())
                val response = placesClient.awaitFindAutocompletePredictions {
//                locationBias = bias
//                typesFilter = listOf(
//                    PlaceTypes.ESTABLISHMENT
//                )
                    this.query = query.text.toString()
//                countries = listOf("US")
                }
                _state.update {
                    it.copy(
                        predictions = response.autocompletePredictions
                    )
                }
            }
        }
    }

    fun onAction(action: MapActions) {
        when (action) {
            is MapActions.OnSelectedPlaceChanged -> {
                _state.update {
                    it.copy(
                        selectedPlace = action.selectedPlace
                    )
                }
            }

            is MapActions.OnSearchQueryChanged -> {
                _searchState.update {
                    TextFieldState(action.searchQuery)
                }
            }

            is MapActions.OnTrackingUserChanged -> {
                _state.update {
                    it.copy(
                        isTrackingUser = action.isTrackingUser
                    )
                }
            }

            else -> {}
        }

    }
}