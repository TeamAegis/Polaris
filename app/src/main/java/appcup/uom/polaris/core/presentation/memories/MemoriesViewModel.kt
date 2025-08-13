package appcup.uom.polaris.core.presentation.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.MemoryRepository
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class MemoriesViewModel(
    private val memoryRepository: MemoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MemoriesState())
    val state = _state.asStateFlow()

    init {
        memoryRepository.getAllMemory().onEach { memories ->
            _state.update {
                it.copy(
                    memories = memories
                )
            }
        }.launchIn(viewModelScope)

    }

    fun onAction(action: MemoriesActions) {

    }
}