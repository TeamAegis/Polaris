package appcup.uom.polaris.core.presentation.memories.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.Memory
import appcup.uom.polaris.core.domain.MemoryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MemoryViewModel(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MemoryState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MemoryEvent>()
    val event = _event.asSharedFlow()

    fun onAction(action: MemoryAction) {
        when (action) {
            is MemoryAction.OnImageCaptured -> {
                _state.value = _state.value.copy(
                    capturedImageUri = action.uri,
                    showBottomSheet = true
                )
            }

            is MemoryAction.SaveMemory -> {
                saveMemory(
                    latitude = action.latitude,
                    longitude = action.longitude,
                    currentJourneyId = action.currentJourneyId
                )
            }

            is MemoryAction.DismissBottomSheet -> {
                _state.value = _state.value.copy(
                    showBottomSheet = false,
                    capturedImageUri = null
                )
            }

            else -> {}
        }
    }

    private fun saveMemory(
        latitude: Double,
        longitude: Double,
        currentJourneyId: String?
    ) {
        val uri = _state.value.capturedImageUri ?: return

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isSaving = true)

                val savedPath = memoryRepository.saveImage(uri)

                val memory = Memory(
                    id = 0,
                    latitude = latitude,
                    longitude = longitude,
                    path = savedPath,
                    journey_id = currentJourneyId
                )

                memoryRepository.createMemory(memory)

                _state.value = _state.value.copy(
                    isSaving = false,
                    showBottomSheet = false,
                    capturedImageUri = null
                )

                _event.emit(MemoryEvent.OnSuccess)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                )
                _event.emit(MemoryEvent.OnError("Failed to save memory: ${e.message}"))

            }
        }
    }
}