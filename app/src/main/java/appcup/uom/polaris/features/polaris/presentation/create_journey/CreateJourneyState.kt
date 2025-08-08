package appcup.uom.polaris.features.polaris.presentation.create_journey

import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.polaris.domain.Waypoint
import appcup.uom.polaris.features.polaris.domain.WaypointSelectorType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlin.uuid.ExperimentalUuidApi

data class CreateJourneyState @OptIn(ExperimentalUuidApi::class) constructor(
    val isToolbarExpanded: Boolean = true,
    val isSendMessageToAIBottomSheetOpen: Boolean = false,
    val isSendMessageToLiveAgentBottomSheetOpen: Boolean = false,
    val liveAgentMessage: String = "",
    val isLoading: Boolean = false,


    val journeyName: String = "",
    val journeyDescription: String = "",
    val selectedPreferences: List<Preferences> = emptyList(),

    val startingMarkerState: MarkerState = MarkerState(),
    val endingMarkerState: MarkerState? = null,
    val intermediateMarkerStates: List<MarkerState> = emptyList(),


    val cameraPositionState: CameraPositionState = CameraPositionState(),


    val isWaypointSelectorVisible: Boolean = false,

    val startingWaypoint: Waypoint = Waypoint(),
    val intermediateWaypoints: List<Waypoint> = listOf(),
    val endingWaypoint: Waypoint = Waypoint().copy(
        address = "Set destination"
    ),
    val waypointSelectorType: WaypointSelectorType = WaypointSelectorType.STARTING,

    val polyline: List<LatLng> = emptyList(),
)
