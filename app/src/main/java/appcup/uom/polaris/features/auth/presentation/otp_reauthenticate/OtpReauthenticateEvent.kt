package appcup.uom.polaris.features.auth.presentation.otp_reauthenticate

sealed class OtpReauthenticateEvent {
    data class Error(val message: String): OtpReauthenticateEvent()
    object Success: OtpReauthenticateEvent()
}