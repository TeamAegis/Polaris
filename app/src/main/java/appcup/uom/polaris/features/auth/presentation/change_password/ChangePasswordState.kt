package appcup.uom.polaris.features.auth.presentation.change_password

data class ChangePasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false
)
