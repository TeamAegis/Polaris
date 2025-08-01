package appcup.uom.polaris.features.auth.presentation.reset_password

sealed class ResetPasswordEvent {
    data class Error(val message: String): ResetPasswordEvent()
    object Success: ResetPasswordEvent()
}