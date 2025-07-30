package appcup.uom.polaris.features.auth.presentation.change_password

sealed interface ChangePasswordAction {
    data class OnPasswordChanged(val password: String) : ChangePasswordAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : ChangePasswordAction
    data class OnPasswordVisibilityChanged(val isVisible: Boolean) : ChangePasswordAction
    data class OnConfirmPasswordVisibilityChanged(val isVisible: Boolean) : ChangePasswordAction
    object OnChangePasswordClicked : ChangePasswordAction
    object OnBackClicked : ChangePasswordAction
}