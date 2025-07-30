package appcup.uom.polaris.features.auth.presentation.login

sealed interface LoginAction {
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    data class OnPasswordVisibilityChanged(val isPasswordVisible: Boolean): LoginAction
    object OnLoginClicked : LoginAction
    object OnForgotPasswordClicked : LoginAction
    object OnBackClicked : LoginAction
}