package appcup.uom.polaris.features.auth.presentation.display_name

sealed interface DisplayNameAction {
    data class OnDisplayNameChanged(val name: String) : DisplayNameAction
    object OnSaveClicked : DisplayNameAction
    object OnBackClicked : DisplayNameAction
}