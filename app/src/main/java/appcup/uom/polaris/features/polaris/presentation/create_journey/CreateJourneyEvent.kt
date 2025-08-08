package appcup.uom.polaris.features.polaris.presentation.create_journey

sealed class CreateJourneyEvent {
    class OnError(val message: String) : CreateJourneyEvent()
}