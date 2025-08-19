package appcup.uom.polaris.core.presentation.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.quest.domain.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoreViewModel(userRepository: UserRepository, val questRepository: QuestRepository) : ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    fun onAction(action: MoreActions) {
        when(action) {
            MoreActions.OnRefreshList -> {
                viewModelScope.launch {
                    questRepository.createQuests()

                    val quests = questRepository.fetchDailyQuest()
                }
            }
            else -> {}
        }
    }
}