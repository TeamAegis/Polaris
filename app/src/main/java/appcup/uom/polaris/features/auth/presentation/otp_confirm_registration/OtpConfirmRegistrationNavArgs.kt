package appcup.uom.polaris.features.auth.presentation.otp_confirm_registration

import kotlinx.serialization.Serializable

@Serializable
data class OtpConfirmRegistrationNavArgs(
    val email: String
)
