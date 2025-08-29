package appcup.uom.polaris.features.polaris.presentation.journeys

import appcup.uom.polaris.features.polaris.domain.JourneyStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface JourneysAction {
    object OnBackClicked: JourneysAction
    @OptIn(ExperimentalUuidApi::class)
    data class OnJourneyClicked(val journeyId: Uuid): JourneysAction
    data class OnStatusSelected(val status: JourneyStatus?): JourneysAction
    data class OnSearchQueryChanged(val searchQuery: String): JourneysAction
}