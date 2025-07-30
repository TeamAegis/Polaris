package appcup.uom.polaris.features.auth.presentation.register

sealed interface RegisterAction {
    data class OnNameChanged(val name: String) : RegisterAction
    data class OnEmailChanged(val email: String) : RegisterAction
    data class OnPasswordChanged(val password: String) : RegisterAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : RegisterAction
    data class OnPasswordVisibilityChanged(val isVisible: Boolean) : RegisterAction
    data class OnConfirmPasswordVisibilityChanged(val isVisible: Boolean) : RegisterAction
    object OnRegisterClicked : RegisterAction
    object OnBackClicked : RegisterAction
}