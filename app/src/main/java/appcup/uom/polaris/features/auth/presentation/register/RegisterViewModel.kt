package appcup.uom.polaris.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<RegisterEvent>()
    val event = _event.asSharedFlow()

    fun onAction(action: RegisterAction) {
        when(action) {
            is RegisterAction.OnConfirmPasswordChanged -> {
                _state.update {
                    it.copy(confirmPassword = action.confirmPassword)
                }
            }
            is RegisterAction.OnConfirmPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isConfirmPasswordVisible = action.isVisible)
                }
            }
            is RegisterAction.OnEmailChanged -> {
                _state.update {
                    it.copy(email = action.email)
                }
            }
            is RegisterAction.OnNameChanged -> {
                _state.update {
                    it.copy(name = action.name)
                }
            }
            is RegisterAction.OnPasswordChanged -> {
                _state.update {
                    it.copy(password = action.password)
                }
            }
            is RegisterAction.OnPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isPasswordVisible = action.isVisible)
                }
            }
            RegisterAction.OnRegisterClicked -> {
                _state.update {
                    it.copy(isLoading = true)
                }
                viewModelScope.launch {
                    val res = userRepository.register(
                        name = _state.value.name,
                        email = _state.value.email,
                        password = _state.value.password,
                        confirmPassword = _state.value.confirmPassword
                    )
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    when(res) {
                        is Result.Error<DataError.AuthError> -> {
                            _event.emit(RegisterEvent.Error(res.error.message))
                        }
                        is Result.Success<Unit> -> {
                            _event.emit(RegisterEvent.Success)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}