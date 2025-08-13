package appcup.uom.polaris.features.polaris.presentation.journey_details

import appcup.uom.polaris.Memory
import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint

data class JourneyDetailsState(
    val journey: Journey? = null,
    val waypoints: List<PersonalWaypoint> = emptyList(),
    val memories: List<Memory> = emptyList(),
    val isDeleteDialogVisible: Boolean = false,


    val isLoading: Boolean = false
)
