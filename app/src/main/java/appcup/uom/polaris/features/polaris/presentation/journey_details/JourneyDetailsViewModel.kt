package appcup.uom.polaris.features.polaris.presentation.journey_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.MemoryRepository
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class JourneyDetailsViewModel(
    private val journeyId: String,
    private val polarisRepository: PolarisRepository,
    private val memoryRepository: MemoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(JourneyDetailsState())
    val state = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            val journeyUuid = Uuid.parse(journeyId)
            val resultJourney = polarisRepository.getJourney(journeyUuid)
            val resultWaypoints = polarisRepository.getPersonalWaypoints(journeyUuid)
            val memories = memoryRepository.getMemories(journeyId)

            when (resultJourney) {
                is Result.Error<DataError.JourneyError> -> {}
                is Result.Success<Journey> -> {
                    _state.update {
                        it.copy(
                            journey = resultJourney.data
                        )
                    }
                }
            }

            when (resultWaypoints) {
                is Result.Error<*> -> {}
                is Result.Success<List<PersonalWaypoint>> -> {
                    _state.update {
                        it.copy(
                            waypoints = resultWaypoints.data
                        )
                    }
                }
            }

            _state.update {
                it.copy(
                    memories = memories,
                    isLoading = false
                )
            }

        }
    }

    fun onAction(action: JourneyDetailsAction) {
        when (action) {
            is JourneyDetailsAction.OnDeleteClicked -> {
                _state.update {
                    it.copy(
                        isDeleteDialogVisible = false
                    )
                }
                viewModelScope.launch {
                    if (_state.value.journey == null) return@launch
                    if (_state.value.journey!!.id == null) return@launch
                    polarisRepository.deleteJourney(_state.value.journey!!.id!!)
                    action.onDelete()
                }

            }
            is JourneyDetailsAction.OnDeleteDialogVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isDeleteDialogVisible = action.isVisible
                    )
                }
            }

            else -> {}
        }

    }

}