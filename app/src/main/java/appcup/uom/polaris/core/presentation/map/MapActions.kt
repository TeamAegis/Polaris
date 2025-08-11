package appcup.uom.polaris.core.presentation.map

import appcup.uom.polaris.features.polaris.domain.Journey

sealed interface MapActions {
    data class OnTrackingUserChanged(val isTrackingUser: Boolean) : MapActions
    object OnCompassClicked : MapActions
    data class OnStartJourneyClicked(val journey: Journey) : MapActions
    object OnStopJourneyClicked : MapActions
    data class OnJourneyCompletedDialogVisibilityChanged(val isVisible: Boolean) : MapActions

}