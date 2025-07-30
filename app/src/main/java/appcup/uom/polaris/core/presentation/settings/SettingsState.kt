package appcup.uom.polaris.core.presentation.settings

import appcup.uom.polaris.core.extras.theme.SeedColor

data class SettingsState(
    val theme: AppTheme = AppTheme.System,
    val isThemeBottomSheetVisible: Boolean = false,
    val isAmoled: Boolean = false,
    val themeColor: SeedColor = SeedColor.CrimsonForge,
    val isColorBottomSheetVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isReauthenticationNonceSent: Boolean = false
)

enum class AppTheme {
    Light, Dark, System
}