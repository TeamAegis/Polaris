package appcup.uom.polaris.features.polaris.presentation.journey_details

sealed class JourneyDetailsEvent {
    data class OnError(val message: String): JourneyDetailsEvent()
}