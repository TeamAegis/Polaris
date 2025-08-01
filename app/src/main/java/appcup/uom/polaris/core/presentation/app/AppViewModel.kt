package appcup.uom.polaris.core.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.features.auth.domain.User
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.conversational_ai.utils.PermissionResultCallback
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val supabaseClient: SupabaseClient,
    private val permissionBridge: PermissionBridge
) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AppEvent>()
    val event = _event.asSharedFlow()

    init {
        _state.update {
            it.copy(
                hasCameraPermission = permissionBridge.isCameraPermissionGranted()
            )
        }

        viewModelScope.launch {
            supabaseClient.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        when (status.source) {
                            SessionSource.External -> {}
                            else -> {
                                if (!_state.value.isAuthenticated) {
                                    _event.emit(AppEvent.Authenticated)
                                    _state.update {
                                        it.copy(isAuthenticated = true)
                                    }
                                }
                                setUser(status.session.user!!)
                            }
                        }
                    }

                    is SessionStatus.NotAuthenticated -> {
                        _event.emit(AppEvent.Unauthenticated)
                        _state.update {
                            it.copy(isAuthenticated = false)
                        }
                    }

                    is SessionStatus.Initializing -> {}
                    is SessionStatus.RefreshFailure -> {}
                }
            }
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
        }
    }

    private fun setUser(user: UserInfo) {
        StaticData.user = User(
            id = user.id,
            name = user.userMetadata!!.getOrElse("name") { "" }.toString()
                .removeSurrounding("\""),
            email = user.email!!
        )
    }


}