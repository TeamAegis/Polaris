package appcup.uom.polaris.core.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.conversational_ai.utils.PermissionResultCallback
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.domain.FragmentsRepository
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class AppViewModel(
    private val permissionBridge: PermissionBridge,
    private val fragmentsRepository: FragmentsRepository,
    private val locationManager: LocationManager
) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AppEvent>()
    val event = _event.asSharedFlow()

    init {
        _state.update {
            it.copy(
                hasCameraPermission = permissionBridge.isCameraPermissionGranted(),
                hasLocationPermission = permissionBridge.isFineLocationPermissionGranted()
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.OnFabMenuExpanded -> {
                _state.update {
                    it.copy(isFabMenuExpanded = action.isFabMenuExpanded)
                }
            }


            AppAction.RequestCameraPermission -> {
                permissionBridge.requestCameraPermission(object : PermissionResultCallback {
                    override fun onPermissionGranted() {
                        viewModelScope.launch {
                            _event.emit(AppEvent.CameraPermissionGranted)
                        }
                        _state.update {
                            it.copy(hasCameraPermission = true)
                        }
                    }

                    override fun onPermissionDenied(isPermanentDenied: Boolean) {
                        if (isPermanentDenied) {
                            viewModelScope.launch {
                                _event.emit(AppEvent.CameraPermissionDeniedPermanent)
                            }
                        } else {
                            viewModelScope.launch {
                                _event.emit(AppEvent.CameraPermissionDenied)
                            }
                        }
                        _state.update {
                            it.copy(
                                hasCameraPermission = false
                            )
                        }
                    }
                })
            }

            AppAction.RequestLocationPermission -> {
                permissionBridge.requestFineLocationPermission(object : PermissionResultCallback {
                    override fun onPermissionGranted() {
                        viewModelScope.launch {
                            _event.emit(AppEvent.LocationPermissionGranted)
                        }
                        _state.update {
                            it.copy(hasLocationPermission = true)
                        }
                    }

                    override fun onPermissionDenied(isPermanentDenied: Boolean) {
                        if (isPermanentDenied) {
                            viewModelScope.launch {
                                _event.emit(AppEvent.LocationPermissionDeniedPermanent)
                            }
                        } else {
                            viewModelScope.launch {
                                _event.emit(AppEvent.LocationPermissionDenied)
                            }
                        }
                        _state.update {
                            it.copy(
                                hasLocationPermission = false
                            )
                        }
                    }
                })
            }

            AppAction.OnControlPanelExpandedChanged -> {
                _state.update {
                    it.copy(isControlPanelExpanded = !it.isControlPanelExpanded)
                }
            }

            is AppAction.OnCreatePublicWaypointClicked -> {
                locationManager.getAddressAndCoordinates { address, latitude, longitude ->
                    if (latitude == null || longitude == null)
                        return@getAddressAndCoordinates

                    viewModelScope.launch {
                        val result =  fragmentsRepository.createPublicWaypoint(
                            waypoint = PublicWaypoint(
                                id = null,
                                address = address,
                                longitude = longitude,
                                latitude = latitude
                            )
                        )
                        when (result) {
                            is Result.Error<DataError.FragmentError> -> {
                                _event.emit(AppEvent.OnError(result.error.message))
                            }
                            is Result.Success<PublicWaypoint> -> {
                                _event.emit(AppEvent.PublicWaypointCreated(result.data))
                            }
                        }
                    }
                }


            }
        }
    }

}