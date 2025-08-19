package appcup.uom.polaris.core.presentation.map

import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.domain.WeatherData
import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import appcup.uom.polaris.features.polaris.domain.Waypoint
import appcup.uom.polaris.features.quest.domain.Quest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class MapState(
    val isMapLoaded: Boolean = false,

    val isTrackingUser: Boolean = false,
    val isAnimatingCamera: Boolean = false,
    val bearing: Float = 0f,

    val currentLocation: Waypoint = Waypoint(),
    val currentMarkerState: MarkerState = MarkerState(),
    val currentCameraPositionState: CameraPositionState = CameraPositionState().apply {
        this.position = CameraPosition.fromLatLngZoom(
            LatLng(0.0, 0.0),
            Constants.MAP_DEFAULT_ZOOM
        )
    },

    val allMyWaypoints: List<PersonalWaypoint> = emptyList(),
    val publicWaypoints: List<PublicWaypoint> = emptyList(),
    val discoveredPublicWaypoints: List<PublicWaypoint> = emptyList(),

    val shouldShowStartJourneyDialog: Boolean = false,
    val startableJourneys: List<Journey> = emptyList(),
    val selectedJourney: Journey? = null,
    val waypointsForSelectedJourney: List<PersonalWaypoint> = emptyList(),
    val isJourneyCompleted: Boolean = false,

    val isSelectedWaypointCardVisible: Boolean = false,
    val selectedWaypoint: Waypoint? = null,
    val selectedWeatherData: WeatherData? = null,

    val quests: List<Quest> = emptyList(),
    val isQuestsVisible: Boolean = false,
)