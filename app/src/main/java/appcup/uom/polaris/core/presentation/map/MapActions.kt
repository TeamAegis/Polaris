package appcup.uom.polaris.core.presentation.map

import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint

sealed interface MapActions {
    data class OnTrackingUserChanged(val isTrackingUser: Boolean) : MapActions
    object OnCompassClicked : MapActions
    data class OnStartJourneyClicked(val journey: Journey) : MapActions
    object OnStopJourneyClicked : MapActions
    object OnToggleShowStartJourneyDialog : MapActions
    data class OnJourneyCompletedDialogVisibilityChanged(val isVisible: Boolean) : MapActions

    data class OnPersonalWaypointClicked(val waypoint: PersonalWaypoint) : MapActions
    object OnTrackingWaypointCardDismissed : MapActions

}