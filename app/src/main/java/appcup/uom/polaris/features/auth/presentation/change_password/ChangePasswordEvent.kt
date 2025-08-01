package appcup.uom.polaris.features.auth.presentation.change_password

sealed class ChangePasswordEvent {
    data class Error(val message: String) : ChangePasswordEvent()
    object ReauthenticationRequired : ChangePasswordEvent()
    object PasswordSuccessfullyChanged : ChangePasswordEvent()
}