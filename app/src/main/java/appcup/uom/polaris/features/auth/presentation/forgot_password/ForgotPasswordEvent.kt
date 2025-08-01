package appcup.uom.polaris.features.auth.presentation.forgot_password

sealed class ForgotPasswordEvent {
    data class Error(val message: String) : ForgotPasswordEvent()
    object PasswordResetEmailSent : ForgotPasswordEvent()
}