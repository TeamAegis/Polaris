package appcup.uom.polaris.core.domain

sealed class ValidationEvent {
    object Success: ValidationEvent()
    data class Error(val message: String): ValidationEvent()
}