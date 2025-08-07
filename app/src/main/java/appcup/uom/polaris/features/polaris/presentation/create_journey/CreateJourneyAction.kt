package appcup.uom.polaris.features.polaris.presentation.create_journey

import appcup.uom.polaris.features.polaris.domain.Preferences

sealed interface CreateJourneyAction {
    object OnBackClicked : CreateJourneyAction
    data class OnToolbarExpandedChanged(val isExpanded: Boolean) : CreateJourneyAction
    object OnSendMessageToAIBottomSheetClicked : CreateJourneyAction
    object OnSendMessageToLiveAgentBottomSheetClicked : CreateJourneyAction
    data class OnJourneyNameChanged(val name: String) : CreateJourneyAction
    data class OnJourneyDescriptionChanged(val description: String) : CreateJourneyAction
    data class OnPreferencesAdded(val preference: Preferences) : CreateJourneyAction
    data class OnPreferencesRemoved(val preference: Preferences) : CreateJourneyAction
    data class OnStartingLocationCustomChanged(val isCustom: Boolean) : CreateJourneyAction
    data class OnEndingLocationCustomChanged(val isCustom: Boolean) : CreateJourneyAction
    data class OnStartingLocationVisibilityChanged(val isVisible: Boolean) : CreateJourneyAction
    data class OnEndingLocationVisibilityChanged(val isVisible: Boolean) : CreateJourneyAction

}