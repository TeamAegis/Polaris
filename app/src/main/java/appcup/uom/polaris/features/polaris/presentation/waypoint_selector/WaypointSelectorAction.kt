package appcup.uom.polaris.features.polaris.presentation.waypoint_selector

import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace

sealed interface WaypointSelectorAction {
    object OnDismiss: WaypointSelectorAction
    data class OnDismissDialogVisibilityChanged(val isDismissDialogVisible: Boolean): WaypointSelectorAction
    data class OnSearchQueryChanged(val searchQuery: String): WaypointSelectorAction
    data class OnSelectedPlaceChanged(val selectedPlace: AutocompletePlace?): WaypointSelectorAction

}