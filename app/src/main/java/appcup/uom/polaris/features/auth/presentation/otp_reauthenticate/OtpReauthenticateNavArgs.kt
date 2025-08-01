package appcup.uom.polaris.features.auth.presentation.otp_reauthenticate

import kotlinx.serialization.Serializable

@Serializable
data class OtpReauthenticateNavArgs(
    val password: String,
    val confirmPassword: String
)
