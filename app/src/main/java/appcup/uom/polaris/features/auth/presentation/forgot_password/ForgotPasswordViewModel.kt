package appcup.uom.polaris.features.auth.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.ValidationEvent
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

    private val _validationEvent = MutableSharedFlow<ValidationEvent>()
    val validationEvent = _validationEvent.asSharedFlow()

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
                            _validationEvent.emit(ValidationEvent.Error(result.error.message))
                        }
                        is Result.Success -> {
                            _validationEvent.emit(ValidationEvent.Success)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}