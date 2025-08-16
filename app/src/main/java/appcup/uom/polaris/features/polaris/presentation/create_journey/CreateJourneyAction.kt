package appcup.uom.polaris.features.polaris.presentation.create_journey

import appcup.uom.polaris.features.polaris.domain.Waypoint
import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.polaris.domain.WaypointType

sealed interface CreateJourneyAction {
    object OnBackClicked : CreateJourneyAction
    data class OnToolbarExpandedChanged(val isExpanded: Boolean) : CreateJourneyAction
    object OnSendMessageToAIBottomSheetClicked : CreateJourneyAction
    object OnSendMessageToLiveAgentBottomSheetClicked : CreateJourneyAction
    data class OnJourneyNameChanged(val name: String) : CreateJourneyAction
    data class OnJourneyDescriptionChanged(val description: String) : CreateJourneyAction
    data class OnPreferencesAdded(val preference: Preferences) : CreateJourneyAction
    data class OnPreferencesRemoved(val preference: Preferences) : CreateJourneyAction
    data class OnWaypointSelectorVisibilityChanged(val isVisible: Boolean, val waypointType: WaypointType) : CreateJourneyAction
    data class OnWaypointSelectorResult(val waypoint: Waypoint) : CreateJourneyAction

    data class OnIntermediateWaypointRemoved(val index: Int) : CreateJourneyAction

    object OnIntermediateWaypointGenerate : CreateJourneyAction

    object OnCreateJourneyClicked : CreateJourneyAction

    object OnSuggestedNameClicked : CreateJourneyAction
    object OnSuggestedDescriptionClicked : CreateJourneyAction

}