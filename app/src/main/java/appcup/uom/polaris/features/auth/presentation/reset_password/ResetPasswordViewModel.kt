package appcup.uom.polaris.features.auth.presentation.reset_password

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

class ResetPasswordViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ResetPasswordEvent>()
    val event = _event.asSharedFlow()


    fun onAction(action: ResetPasswordAction) {
        when(action) {
            is ResetPasswordAction.OnConfirmPasswordChanged -> {
                _state.update {
                    it.copy(confirmPassword = action.confirmPassword)
                }
            }
            is ResetPasswordAction.OnConfirmPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isConfirmPasswordVisible = action.isVisible)
                }
            }
            is ResetPasswordAction.OnPasswordChanged -> {
                _state.update {
                    it.copy(password = action.password)
                }
            }
            is ResetPasswordAction.OnPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isPasswordVisible = action.isVisible)
                }
            }
            ResetPasswordAction.OnResetClicked -> {
                _state.update {
                    it.copy(isLoading = true)
                }
                viewModelScope.launch {
                    val res = userRepository.resetPassword(
                        password = _state.value.password,
                        confirmPassword = _state.value.confirmPassword
                    )
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    when(res) {
                        is Result.Error<DataError.AuthError> -> {
                            _event.emit(ResetPasswordEvent.Error(res.error.message))
                        }
                        is Result.Success<Unit> -> {
                            _event.emit(ResetPasswordEvent.Success)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}