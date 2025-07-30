package appcup.uom.polaris.features.auth.presentation.reset_password

data class ResetPasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false
)
