package appcup.uom.polaris.core.presentation.app

sealed class AppEvent {
    object Authenticated: AppEvent()
    object Unauthenticated: AppEvent()
    object CameraPermissionDenied: AppEvent()
    object CameraPermissionDeniedPermanent: AppEvent()
    object CameraPermissionGranted: AppEvent()
}