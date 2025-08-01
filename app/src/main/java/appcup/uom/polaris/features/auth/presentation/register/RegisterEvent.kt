package appcup.uom.polaris.features.auth.presentation.register

sealed class RegisterEvent {
    object Success: RegisterEvent()
    data class Error(val message: String): RegisterEvent()
}