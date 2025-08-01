package appcup.uom.polaris.core.presentation.settings

sealed class SettingsEvent {
    data class Error(val message: String): SettingsEvent()
}