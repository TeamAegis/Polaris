package appcup.uom.polaris.features.auth.presentation.login

sealed class LoginEvent {
    data class Error(val message: String) : LoginEvent()
    object LoginSuccess : LoginEvent()
}