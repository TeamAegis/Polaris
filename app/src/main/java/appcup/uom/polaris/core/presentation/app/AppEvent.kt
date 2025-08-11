package appcup.uom.polaris.core.presentation.app

sealed class AppEvent {
    object CameraPermissionDenied: AppEvent()
    object CameraPermissionDeniedPermanent: AppEvent()
    object CameraPermissionGranted: AppEvent()
    object LocationPermissionDenied: AppEvent()
    object LocationPermissionDeniedPermanent: AppEvent()
    object LocationPermissionGranted: AppEvent()

}