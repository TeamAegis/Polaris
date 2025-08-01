package appcup.uom.polaris.features.polaris.presentation.create_journey

data class CreateJourneyState(
    val isToolbarExpanded: Boolean = true,
    val isSendMessageToAIBottomSheetOpen: Boolean = false,
    val isSendMessageToLiveAgentBottomSheetOpen: Boolean = false,
    val liveAgentMessage: String = "",
    val isLoading: Boolean = false,
)
