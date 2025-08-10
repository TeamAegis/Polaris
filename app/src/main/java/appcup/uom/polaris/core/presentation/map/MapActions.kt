package appcup.uom.polaris.core.presentation.map

sealed interface MapActions {
    data class OnTrackingUserChanged(val isTrackingUser: Boolean) : MapActions
    object OnCompassClick : MapActions

}