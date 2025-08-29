package appcup.uom.polaris.features.polaris.presentation.create_journey

import appcup.uom.polaris.features.polaris.domain.Journey

sealed class CreateJourneyEvent {
    data class OnError(val message: String) : CreateJourneyEvent()
    data class OnJourneyCreated(val journey: Journey) : CreateJourneyEvent()
}