package appcup.uom.polaris.features.auth.presentation.otp_confirm_registration

sealed interface OtpConfirmRegistrationAction {
    object OnConfirmClicked : OtpConfirmRegistrationAction
    data class OnEnterNumber(val number: Int?, val index: Int): OtpConfirmRegistrationAction
    data class OnChangeFieldFocused(val index: Int): OtpConfirmRegistrationAction
    data object OnKeyboardBack: OtpConfirmRegistrationAction
    object OnBackClicked : OtpConfirmRegistrationAction
}