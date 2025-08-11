package appcup.uom.polaris.core.presentation.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.extras.theme.map_style
import appcup.uom.polaris.core.presentation.components.LoadingOverlay
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun MapScreen(
    viewModel: MapViewModel,
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

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                MapEvent.OnJourneyCompleted -> {
                    viewModel.onAction(MapActions.OnJourneyCompletedDialogVisibilityChanged(true))
                }
            }
        }
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

    if (state.isJourneyCompleted) {
        AlertDialog(
            onDismissRequest = {
                onAction(MapActions.OnJourneyCompletedDialogVisibilityChanged(false))
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "Journey Complete!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Congratulations! You've successfully completed \"${state.selectedJourney!!.name}\".",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAction(MapActions.OnJourneyCompletedDialogVisibilityChanged(false))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Okay")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { contentPadding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = state.currentCameraPositionState,
            uiSettings = if (state.isTrackingUser) Constants.MAP_PREVIEW_UI_SETTINGS else MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
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

            if (state.selectedJourney == null) {
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

            if (state.selectedJourney != null) {
                state.waypointsForSelectedJourney.forEach { waypoint ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                waypoint.latitude,
                                waypoint.longitude
                            )
                        )
                    )

                }
                Polyline(
                    points = PolyUtil.decode(state.selectedJourney.encodedPolyline)
                )
            }
        }

    }

    LoadingOverlay(state.isAnimatingCamera, 0f)
}
