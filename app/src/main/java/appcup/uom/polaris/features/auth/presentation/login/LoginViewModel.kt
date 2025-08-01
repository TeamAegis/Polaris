package appcup.uom.polaris.features.auth.presentation.login

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

class LoginViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    fun onAction(action: LoginAction) {
        when(action) {
            is LoginAction.OnEmailChanged -> {
                _state.update {
                    it.copy(email = action.email)
                }
            }
            LoginAction.OnLoginClicked -> {
                _state.update {
                    it.copy(isLoading = true)
                }

                viewModelScope.launch {
                    val res = userRepository.login(
                        email = _state.value.email,
                        password = _state.value.password
                    )
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    when(res) {
                        is Result.Error<DataError.Local> -> {
                            _event.emit(LoginEvent.Error(res.error.message))
                        }
                        is Result.Success<*> -> {
                            _event.emit(LoginEvent.LoginSuccess)
                        }
                    }
                }
            }
            is LoginAction.OnPasswordChanged -> {
                _state.update {
                    it.copy(password = action.password)
                }
            }

            is LoginAction.OnPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isPasswordVisible = action.isPasswordVisible)
                }
            }
            else -> {}
        }
    }
}