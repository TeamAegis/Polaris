package appcup.uom.polaris.features.polaris.presentation.waypoint_selector

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class WaypointSelectorViewModel(
    placesClient: PlacesClient
): ViewModel() {

    private val _state = MutableStateFlow(WaypointSelectorState())
    val state = _state.asStateFlow()

    private val _searchState = MutableStateFlow(TextFieldState(""))
    val searchState = _searchState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchState.debounce(500.milliseconds).collect { query: TextFieldState ->
                println(Constants.DEBUG_VALUE + query.text.toString())
                val response = placesClient.awaitFindAutocompletePredictions {
//                locationBias = bias
//                typesFilter = listOf(
//                    PlaceTypes.ESTABLISHMENT
//                )
                    this.query = query.text.toString()
//                countries = listOf("US")
                }
                _state.update {
                    it.copy(
                        predictions = response.autocompletePredictions
                    )
                }
            }
        }
    }

    fun onAction(action: WaypointSelectorAction) {
        when (action) {
            is WaypointSelectorAction.OnDismissDialogVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isDismissDialogVisible = action.isDismissDialogVisible
                    )
                }
            }
            is WaypointSelectorAction.OnSelectedPlaceChanged -> {
                _state.update {
                    it.copy(
                        selectedPlace = action.selectedPlace
                    )
                }
            }
            is WaypointSelectorAction.OnSearchQueryChanged -> {
                _searchState.update {
                    TextFieldState(action.searchQuery)
                }
            }
            else -> {}
        }

    }

}