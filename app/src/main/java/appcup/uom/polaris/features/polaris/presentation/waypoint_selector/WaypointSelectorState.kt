package appcup.uom.polaris.features.polaris.presentation.waypoint_selector

import appcup.uom.polaris.features.polaris.domain.Waypoint
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState

data class WaypointSelectorState(
    val predictions: List<AutocompletePrediction> = emptyList(),
    val selectedPlace: AutocompletePlace? = null,
    val expanded: Boolean = false,
    val isLoading: Boolean = false,

    val waypointMarkerState: MarkerState = MarkerState(),
    val waypointCameraPositionState: CameraPositionState = CameraPositionState(),

    val selectedWaypoint: Waypoint? = Waypoint(),
)
