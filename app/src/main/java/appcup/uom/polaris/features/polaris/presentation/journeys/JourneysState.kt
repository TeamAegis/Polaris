package appcup.uom.polaris.features.polaris.presentation.journeys

import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.JourneyStatus

data class JourneysState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedStatus: JourneyStatus? = null,
    val journeys: List<Journey> = emptyList(),
    val filteredJourneys: List<Journey> = emptyList()
)
