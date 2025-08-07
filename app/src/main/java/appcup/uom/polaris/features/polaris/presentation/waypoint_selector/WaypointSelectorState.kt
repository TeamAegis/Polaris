package appcup.uom.polaris.features.polaris.presentation.waypoint_selector

import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace

data class WaypointSelectorState(
    val predictions: List<AutocompletePrediction> = emptyList(),
    val selectedPlace: AutocompletePlace? = null,
    val isDismissDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
)
