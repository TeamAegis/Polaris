package appcup.uom.polaris.features.auth.presentation.display_name

sealed class DisplayNameEvent {
    data class Error(val message: String): DisplayNameEvent()
    object DisplayNameSuccessfullyChanged: DisplayNameEvent()
}