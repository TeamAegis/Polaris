package appcup.uom.polaris.core.presentation.settings

sealed interface SettingsAction {
    object OnBackClicked : SettingsAction
    object OnLogoutClicked : SettingsAction

    data class OnThemeChanged(val theme: AppTheme) : SettingsAction
    data class OnThemeBottomSheetToggled(val show: Boolean) : SettingsAction
    object OnChangeDisplayNameClicked : SettingsAction
    object OnChangePasswordClicked : SettingsAction

    object OnRefreshList : SettingsAction

}