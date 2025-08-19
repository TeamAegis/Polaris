package appcup.uom.polaris.core.presentation.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.quest.domain.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class MoreViewModel(userRepository: UserRepository, val questRepository: QuestRepository) : ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    quests = questRepository.fetchAllCompletedQuests().sortedByDescending { quest -> Instant.parse(quest.createdDate) },
                )
            }
        }

    }

    fun onAction(action: MoreActions) {
        when(action) {
            MoreActions.OnRefreshList -> {
//                viewModelScope.launch {
//                    questRepository.createQuests()
//
//                    val quests = questRepository.fetchDailyQuest()
//                }
            }
            is MoreActions.OnBottomSheetVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isQuestBottomSheetVisible = action.visibility
                    )
                }
            }
            else -> {}
        }
    }
}