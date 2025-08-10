package appcup.uom.polaris.core.presentation.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.extras.theme.map_style
import appcup.uom.polaris.core.presentation.components.LoadingOverlay
import appcup.uom.polaris.core.presentation.map.components.MapOverlayControls
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(
        state.allMyWaypoints
    ) {
        if (state.allMyWaypoints.isEmpty()) return@LaunchedEffect
        val boundsBuilder = LatLngBounds.builder()
        state.allMyWaypoints.forEach { waypoint ->
            boundsBuilder.include(LatLng(waypoint.latitude, waypoint.longitude))
        }

        val bounds = boundsBuilder.build()
        val padding = 300

        state.currentCameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
            durationMs = 200
        )
    }

    MapScreenImpl(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenImpl(
    state: MapState,
    onAction: (MapActions) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { contentPadding ->
        Box {

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = state.currentCameraPositionState,
                uiSettings = if (state.isTrackingUser) Constants.MAP_PREVIEW_UI_SETTINGS else MapUiSettings(
                    compassEnabled = false,
                    indoorLevelPickerEnabled = false,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = false,
//                rotationGesturesEnabled = false,
//                scrollGesturesEnabled = false,
//                scrollGesturesEnabledDuringRotateOrZoom = false,
//                tiltGesturesEnabled = false,
                    zoomControlsEnabled = false,
//                zoomGesturesEnabled = false,
                ),
                properties = MapProperties(
                    latLngBoundsForCameraTarget = Constants.MAP_LAT_LNG_BOUNDS,
                    isBuildingEnabled = true,
                    mapStyleOptions = MapStyleOptions(map_style)
                )
            ) {

                Marker(
                    state = state.currentMarkerState
                )

                state.allMyWaypoints.forEach { waypoint ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                waypoint.latitude,
                                waypoint.longitude
                            )
                        )
                    )
                }

                state.discoveredPublicWaypoints.forEach { waypoint ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                waypoint.latitude,
                                waypoint.longitude
                            )
                        )
                    )
                }
            }
        }
        MapOverlayControls(
            state = state,
            modifier = Modifier.fillMaxSize(),
            onToggleMap = {
                onAction(MapActions.OnTrackingUserChanged(!state.isTrackingUser))
            },
            onCompassClick = {
                onAction(MapActions.OnCompassClick)
            }
        )

    }

    LoadingOverlay(state.isAnimatingCamera, 0f)
}
