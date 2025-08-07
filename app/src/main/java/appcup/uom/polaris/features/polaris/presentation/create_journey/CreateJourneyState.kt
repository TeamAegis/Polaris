package appcup.uom.polaris.features.polaris.presentation.create_journey

import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.polaris.domain.Waypoint
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
    val isStartingLocationCustom: Boolean = false,
    val isEndingLocationCustom: Boolean = false,

    val startingLocation: Waypoint = Waypoint(),
    val startingMarkerState: MarkerState = MarkerState(),
    val startingCameraPositionState: CameraPositionState = CameraPositionState(),
    val isStartingLocationSelectorVisible: Boolean = false,


    val endingLocation: Waypoint = Waypoint(),
    val endingMarkerState: MarkerState = MarkerState(),
    val endingCameraPositionState: CameraPositionState = CameraPositionState(),
    val isEndingLocationSelectorVisible: Boolean = false,
)
