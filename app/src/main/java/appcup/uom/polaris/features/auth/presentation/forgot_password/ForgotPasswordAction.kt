package appcup.uom.polaris.features.auth.presentation.forgot_password

sealed interface ForgotPasswordAction {
    data class OnEmailChanged(val email: String) : ForgotPasswordAction
    object OnForgotPasswordClicked : ForgotPasswordAction
    object OnBackClicked : ForgotPasswordAction
}