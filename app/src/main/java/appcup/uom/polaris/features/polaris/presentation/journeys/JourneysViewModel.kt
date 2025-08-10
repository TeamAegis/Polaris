package appcup.uom.polaris.features.polaris.presentation.journeys

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class JourneysViewModel(
    polarisRepository: PolarisRepository
): ViewModel() {
    private val _state = MutableStateFlow(JourneysState())
    val state = _state.asStateFlow()

    private var channelJob: Job? = null

    init {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        channelJob = polarisRepository.getJourneys()
            .onEach { journeys ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        journeys = journeys,
                        filteredJourneys = journeys.filter { journey ->
                            journey.name.contains(_state.value.searchQuery, ignoreCase = true) &&
                                    (_state.value.selectedStatus == null || journey.status == _state.value.selectedStatus)
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: JourneysAction) {
        when (action) {
            is JourneysAction.OnSearchQueryChanged -> {
                _state.update {
                    it.copy(
                        searchQuery = action.searchQuery,
                        filteredJourneys = _state.value.journeys.filter { journey ->
                            journey.name.contains(action.searchQuery.trim(), ignoreCase = true) && (_state.value.selectedStatus == null || (_state.value.selectedStatus != null && journey.status == _state.value.selectedStatus))
                        }
                    )
                }
            }
            is JourneysAction.OnStatusSelected -> {
                _state.update {
                    it.copy(
                        selectedStatus = action.status,
                        filteredJourneys = _state.value.journeys.filter { journey ->
                            journey.name.contains(_state.value.searchQuery.trim(), ignoreCase = true) && (action.status == null || (action.status != null && journey.status == action.status))
                        })
                }
            }
            else -> {}
        }

    }

    override fun onCleared() {
        super.onCleared()
        channelJob?.cancel()
    }


}