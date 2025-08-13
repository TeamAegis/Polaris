package appcup.uom.polaris.core.presentation.settings

data class SettingsState(
    val theme: AppTheme = AppTheme.System,
    val isThemeBottomSheetVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isReauthenticationNonceSent: Boolean = false
)

enum class AppTheme {
    Light, Dark, System
}