package appcup.uom.polaris.core.presentation.map

import appcup.uom.polaris.features.polaris.domain.Waypoint
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState

data class MapState(
    val isMapInitialized: Boolean = false,
    val predictions: List<AutocompletePrediction> = emptyList(),
    val selectedPlace: AutocompletePlace? = null,
    val isLoading: Boolean = false,

    val currentLocation: Waypoint = Waypoint(),
    val currentMarkerState: MarkerState = MarkerState(),
    val currentCameraPositionState: CameraPositionState = CameraPositionState().apply {
        this.position = CameraPosition(
            LatLng(0.0,0.0),
            18f,
            67.5f,
            0f
        )
    },
    val isTrackingUser: Boolean = true,


    // create journey
    val isCreateJourneyEnabled: Boolean = true,
    val isCreateJourneyBottomSheetVisible: Boolean = true,
    val startingWaypoint: Waypoint = Waypoint(),
    val intermediateWaypoints: List<Waypoint> = listOf(),
    val endingWaypoint: Waypoint = Waypoint()
)