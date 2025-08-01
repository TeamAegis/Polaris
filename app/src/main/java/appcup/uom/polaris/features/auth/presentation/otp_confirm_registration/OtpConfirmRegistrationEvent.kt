package appcup.uom.polaris.features.auth.presentation.otp_confirm_registration

sealed class OtpConfirmRegistrationEvent {
    object Success: OtpConfirmRegistrationEvent()
    data class Error(val message: String): OtpConfirmRegistrationEvent()
}