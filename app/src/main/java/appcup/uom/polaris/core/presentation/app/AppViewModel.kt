package appcup.uom.polaris.core.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.conversational_ai.utils.PermissionResultCallback
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val permissionBridge: PermissionBridge
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
        }
    }

}