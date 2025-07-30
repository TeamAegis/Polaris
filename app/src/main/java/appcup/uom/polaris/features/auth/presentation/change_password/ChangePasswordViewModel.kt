package appcup.uom.polaris.features.auth.presentation.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.ValidationEvent
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(ChangePasswordState())
    val state = _state.asStateFlow()

    private val _validationEvent = MutableSharedFlow<ValidationEvent>()
    val validationEvent = _validationEvent.asSharedFlow()


    fun onAction(action: ChangePasswordAction) {
        when(action) {
            is ChangePasswordAction.OnConfirmPasswordChanged -> {
                _state.update {
                    it.copy(confirmPassword = action.confirmPassword)
                }
            }
            is ChangePasswordAction.OnConfirmPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isConfirmPasswordVisible = action.isVisible)
                }
            }
            is ChangePasswordAction.OnPasswordChanged -> {
                _state.update {
                    it.copy(password = action.password)
                }
            }
            is ChangePasswordAction.OnPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isPasswordVisible = action.isVisible)
                }
            }
            ChangePasswordAction.OnChangePasswordClicked -> {
                _state.update {
                    it.copy(isLoading = true)
                }
                viewModelScope.launch {
                    val res = userRepository.changePassword(
                        password = _state.value.password,
                        confirmPassword = _state.value.confirmPassword
                    )
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    when(res) {
                        is Result.Error<DataError.Local> -> {
                            _validationEvent.emit(ValidationEvent.Error(res.error.message))
                        }
                        is Result.Success<Unit> -> {
                            _validationEvent.emit(ValidationEvent.Success)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}