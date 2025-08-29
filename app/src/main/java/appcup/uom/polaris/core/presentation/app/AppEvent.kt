package appcup.uom.polaris.core.presentation.app

import appcup.uom.polaris.features.polaris.domain.PublicWaypoint

sealed class AppEvent {
    data class OnError(val error: String): AppEvent()
    object CameraPermissionDenied: AppEvent()
    object CameraPermissionDeniedPermanent: AppEvent()
    object CameraPermissionGranted: AppEvent()
    object LocationPermissionDenied: AppEvent()
    object LocationPermissionDeniedPermanent: AppEvent()
    object LocationPermissionGranted: AppEvent()
    data class PublicWaypointCreated(val waypoint: PublicWaypoint): AppEvent()

}