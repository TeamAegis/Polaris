package appcup.uom.polaris.features.auth.presentation.reset_password

sealed interface ResetPasswordAction {
    data class OnPasswordChanged(val password: String) : ResetPasswordAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : ResetPasswordAction
    data class OnPasswordVisibilityChanged(val isVisible: Boolean) : ResetPasswordAction
    data class OnConfirmPasswordVisibilityChanged(val isVisible: Boolean) : ResetPasswordAction
    object OnResetClicked : ResetPasswordAction
    object OnBackClicked : ResetPasswordAction
}