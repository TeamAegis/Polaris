package appcup.uom.polaris.core.presentation.map

import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import appcup.uom.polaris.features.polaris.domain.Waypoint
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState

data class MapState(
    val currentLocation: Waypoint = Waypoint(),
    val currentMarkerState: MarkerState = MarkerState(),
    val currentCameraPositionState: CameraPositionState = CameraPositionState().apply {
        this.position = CameraPosition.fromLatLngZoom(
            LatLng(0.0,0.0),
            Constants.MAP_DEFAULT_ZOOM
        )
    },
    val isTrackingUser: Boolean = false,
    val isAnimatingCamera: Boolean = false,
    val bearing: Float = 0f,

    val allMyWaypoints: List<PersonalWaypoint> = emptyList(),
    val publicWaypoints: List<PublicWaypoint> = emptyList(),
    val discoveredPublicWaypoints: List<PublicWaypoint> = emptyList(),
)