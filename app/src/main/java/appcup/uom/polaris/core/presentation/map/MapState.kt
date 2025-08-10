package appcup.uom.polaris.core.presentation.map

import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.features.polaris.domain.Journey
import appcup.uom.polaris.features.polaris.domain.JourneyStatus
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint
import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import appcup.uom.polaris.features.polaris.domain.Waypoint
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class MapState(
    val isTrackingUser: Boolean = false,
    val isAnimatingCamera: Boolean = false,
    val bearing: Float = 0f,


    val currentLocation: Waypoint = Waypoint(),
    val currentMarkerState: MarkerState = MarkerState(),
    val currentCameraPositionState: CameraPositionState = CameraPositionState().apply {
        this.position = CameraPosition.fromLatLngZoom(
            LatLng(0.0,0.0),
            Constants.MAP_DEFAULT_ZOOM
        )
    },


    val allMyWaypoints: List<PersonalWaypoint> = emptyList(),
    val publicWaypoints: List<PublicWaypoint> = emptyList(),
    val discoveredPublicWaypoints: List<PublicWaypoint> = emptyList(),


    val selectedJourney: Journey? = Journey(
        Uuid.parse("74fd1c95-829f-41f0-9d20-2b0d1a4bce3a"),
        "Test",
        "Test",
        listOf(Preferences.FOOD, Preferences.ATTRACTIONS),
        "rphyB_w{~IeAw@cBpDkAnC{AbGaAtAkAnAt@x@YRmBpBbAt@cAu@oE~Fc@`@OHaCd@HPnBjBsAjAq@hAcBjBQOpLfJhN_TvEkHlDuFd@u@",
        JourneyStatus.NOT_STARTED,
        Uuid.parse("a96830c4-4a22-42ce-9d70-ca3b15ab0848"),
    ),
    val waypointsForSelectedJourney: List<PersonalWaypoint> = emptyList(),
)