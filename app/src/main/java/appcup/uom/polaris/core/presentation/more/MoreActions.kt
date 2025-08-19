package appcup.uom.polaris.core.presentation.more

sealed interface MoreActions {
    object OnSettingsClicked : MoreActions
    object OnChatClicked : MoreActions

    data class OnBottomSheetVisibilityChanged(val visibility: Boolean) : MoreActions
}