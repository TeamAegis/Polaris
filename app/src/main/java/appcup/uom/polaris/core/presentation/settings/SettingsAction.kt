package appcup.uom.polaris.core.presentation.settings

import appcup.uom.polaris.core.extras.theme.SeedColor

sealed interface SettingsAction {
    object OnBackClicked : SettingsAction
    object OnLogoutClicked: SettingsAction

    data class OnThemeChanged(val theme: AppTheme) : SettingsAction
    data class OnThemeBottomSheetToggled(val show: Boolean) : SettingsAction
    object OnAmoledChanged: SettingsAction
    data class OnColorChanged(val color: SeedColor) : SettingsAction
    data class OnColorBottomSheetToggled(val show: Boolean) : SettingsAction
    object OnChangeDisplayNameClicked: SettingsAction
    object OnChangePasswordClicked: SettingsAction
}