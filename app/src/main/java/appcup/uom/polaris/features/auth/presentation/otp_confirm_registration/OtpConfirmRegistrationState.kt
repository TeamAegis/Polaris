package appcup.uom.polaris.features.auth.presentation.otp_confirm_registration

data class OtpConfirmRegistrationState(
    val code: List<Int?> = (1..6).map { null },
    val focusedIndex: Int? = null,
    val isLoading: Boolean = false
)
