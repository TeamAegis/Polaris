package appcup.uom.polaris.features.polaris.presentation.create_journey

sealed interface CreateJourneyAction {
    object OnBackClicked : CreateJourneyAction
    data class OnToolbarExpandedChanged(val isExpanded: Boolean) : CreateJourneyAction
    object OnSendMessageToAIBottomSheetClicked : CreateJourneyAction
    object OnSendMessageToLiveAgentBottomSheetClicked : CreateJourneyAction
    data class OnLiveAgentMessageChanged(val message: String) : CreateJourneyAction
    object OnSendMessageToLiveAgentClicked : CreateJourneyAction
}