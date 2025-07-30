package appcup.uom.polaris.features.auth.presentation.otp_reauthenticate

sealed interface OtpReauthenticateAction {
    object OnConfirmClicked : OtpReauthenticateAction
    data class OnEnterNumber(val number: Int?, val index: Int): OtpReauthenticateAction
    data class OnChangeFieldFocused(val index: Int): OtpReauthenticateAction
    data object OnKeyboardBack: OtpReauthenticateAction
    object OnBackClicked : OtpReauthenticateAction
}