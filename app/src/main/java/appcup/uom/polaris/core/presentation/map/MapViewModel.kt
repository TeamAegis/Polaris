package appcup.uom.polaris.core.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.domain.LatLong
import appcup.uom.polaris.core.domain.ResultState
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, FlowPreview::class)
class MapViewModel(
    locationManager: LocationManager,
    polarisRepository: PolarisRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()


    init {


        viewModelScope.launch {
            locationManager.getCoordinates { latitude, longitude ->
                if (latitude != null && longitude != null) {
                    _state.value.currentCameraPositionState.move(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition
                                .builder(_state.value.currentCameraPositionState.position)
                                .target(LatLng(latitude, longitude))
                                .build()
                        )
                    )
                    locationManager.getOrientationFlow().onEach { orientation ->
                        _state.update {
                            it.copy(
                                bearing = orientation.headingDegrees
                            )
                        }
                        if (_state.value.isTrackingUser) {
                            _state.value.currentCameraPositionState.move(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition
                                        .builder(_state.value.currentCameraPositionState.position)
                                        .bearing(orientation.headingDegrees)
                                        .build()
                                )
                            )
                        }
                    }.launchIn(viewModelScope + Dispatchers.Main)
                }
            }
        }

        locationManager.getLocationUpdatesFlow(500L).onEach { result ->
            when (result) {
                is ResultState.Failure -> {}
                ResultState.Loading -> {}
                is ResultState.Success<LatLong> -> {
                    val (latitude, longitude) = result.data

                    if (_state.value.isTrackingUser) {
                        _state.value.currentCameraPositionState.move(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder(
                                    _state.value.currentCameraPositionState.position
                                ).target(
                                    LatLng(
                                        latitude,
                                        longitude
                                    )
                                ).build()
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
                            },
                            discoveredPublicWaypoints = _state.value.publicWaypoints.filter { waypoint ->
                                SphericalUtil.computeDistanceBetween(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ),
                                    LatLng(
                                        waypoint.latitude,
                                        waypoint.longitude
                                    )
                                ) <= Constants.MAP_FRAGMENT_DISCOVERY_RADIUS_IN_METRES
                            }
                        )
                    }
                }
            }

        }.launchIn(viewModelScope)

        polarisRepository.getAllMyWaypoints().onEach { waypoints ->
            _state.update {
                it.copy(
                    allMyWaypoints = waypoints
                )
            }
        }.launchIn(viewModelScope)

        polarisRepository.getPublicWaypoints().onEach { waypoints ->
            _state.update {
                it.copy(
                    publicWaypoints = waypoints
                )
            }
        }.launchIn(viewModelScope)


    }

    fun onAction(action: MapActions) {
        when (action) {

            is MapActions.OnTrackingUserChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isAnimatingCamera = true,
                            isTrackingUser = false
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
                            isAnimatingCamera = false,
                            isTrackingUser = action.isTrackingUser
                        )
                    }
                }
            }

            MapActions.OnCompassClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isTrackingUser = false,
                            isAnimatingCamera = true
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
                            )
                                .zoom(10f)
                                .tilt(0f)
                                .build()
                        )
                    )
                    _state.update {
                        it.copy(
                            isAnimatingCamera = false
                        )
                    }
                }
            }

            else -> {}
        }

    }
}