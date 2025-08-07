package appcup.uom.polaris.core.presentation.map

import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace

sealed interface MapActions {
    data class OnSearchQueryChanged(val searchQuery: String): MapActions
    data class OnSelectedPlaceChanged(val selectedPlace: AutocompletePlace?): MapActions
    data class OnTrackingUserChanged(val isTrackingUser: Boolean): MapActions

}