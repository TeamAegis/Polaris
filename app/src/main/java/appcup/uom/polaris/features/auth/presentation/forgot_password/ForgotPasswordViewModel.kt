package appcup.uom.polaris.features.auth.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ForgotPasswordEvent>()
    val event = _event.asSharedFlow()

    fun onAction(action: ForgotPasswordAction) {
        when(action) {
            is ForgotPasswordAction.OnEmailChanged -> {
                _state.update {
                    it.copy(email = action.email)
                }
            }
            ForgotPasswordAction.OnForgotPasswordClicked -> {
                _state.update {
                    it.copy(isLoading = true)
                }
                viewModelScope.launch {
                    val result = userRepository.forgotPassword(state.value.email)
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    when(result) {
                        is Result.Error -> {
                            _event.emit(ForgotPasswordEvent.Error(result.error.message))
                        }
                        is Result.Success -> {
                            _event.emit(ForgotPasswordEvent.PasswordResetEmailSent)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}