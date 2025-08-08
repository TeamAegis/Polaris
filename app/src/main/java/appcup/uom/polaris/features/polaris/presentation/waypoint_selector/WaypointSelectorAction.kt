package appcup.uom.polaris.features.polaris.presentation.waypoint_selector

import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace

sealed interface WaypointSelectorAction {
    object OnDismiss: WaypointSelectorAction
    object OnConfirm: WaypointSelectorAction
    data class OnSearchQueryChanged(val searchQuery: String): WaypointSelectorAction
    data class OnSelectedPlaceChanged(val selectedPlace: AutocompletePlace?): WaypointSelectorAction
    data class OnSearchExpandedChanged(val expanded: Boolean): WaypointSelectorAction
    object SetToCurrentLocation: WaypointSelectorAction
    data class OnMapClick(val latitude: Double, val longitude: Double): WaypointSelectorAction
    data class OnPoiClick(val placeId: String): WaypointSelectorAction

}