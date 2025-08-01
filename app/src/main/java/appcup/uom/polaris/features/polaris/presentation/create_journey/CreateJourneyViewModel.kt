package appcup.uom.polaris.features.polaris.presentation.create_journey

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateJourneyViewModel: ViewModel() {
    private val _state = MutableStateFlow(CreateJourneyState())
    val state = _state.asStateFlow()

    fun onAction(action: CreateJourneyAction) {
        when (action) {
            is CreateJourneyAction.OnToolbarExpandedChanged -> {
                _state.update {
                    it.copy(
                        isToolbarExpanded = action.isExpanded
                    )
                }
            }

            CreateJourneyAction.OnSendMessageToAIBottomSheetClicked -> {
                _state.update {
                    it.copy(
                        isSendMessageToAIBottomSheetOpen = !_state.value.isSendMessageToAIBottomSheetOpen
                    )
                }
            }

            CreateJourneyAction.OnSendMessageToLiveAgentBottomSheetClicked -> {
                _state.update {
                    it.copy(
                        isSendMessageToLiveAgentBottomSheetOpen = !_state.value.isSendMessageToLiveAgentBottomSheetOpen
                    )
                }
            }
            is CreateJourneyAction.OnLiveAgentMessageChanged -> {
                _state.update {
                    it.copy(
                        liveAgentMessage = action.message
                    )
                }
            }

            CreateJourneyAction.OnSendMessageToLiveAgentClicked -> {

            }

            else -> {}
        }
    }

}