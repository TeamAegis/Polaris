package appcup.uom.polaris.features.polaris.presentation.journey_details

sealed interface JourneyDetailsAction {
    object OnBackClicked: JourneyDetailsAction
    data class OnDeleteDialogVisibilityChanged(val isVisible: Boolean): JourneyDetailsAction
    data class OnDeleteClicked(val onDelete: () -> Unit = {}): JourneyDetailsAction
}