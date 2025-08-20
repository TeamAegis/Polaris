package appcup.uom.polaris.core.presentation.map

import android.annotation.SuppressLint
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.R
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.extras.theme.map_style
import appcup.uom.polaris.core.presentation.components.LoadingOverlay
import appcup.uom.polaris.features.polaris.domain.PersonalWaypoint
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import appcup.uom.polaris.features.polaris.domain.WaypointType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    snackbarHostState: SnackbarHostState,
    onFragmentClicked: (PublicWaypoint) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(
        state.allMyWaypoints
    ) {
        if (state.selectedJourney != null) return@LaunchedEffect
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
            when (action) {
                is MapActions.OnFragmentClicked -> {
                    if (viewModel.isFragmentInteractable(action.waypoint)) {
                        onFragmentClicked(action.waypoint)
                    }
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
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
                    text = "Congratulations! You've successfully completed the journey.",
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
            ),
            onPOIClick = { poi ->
                onAction(
                    MapActions.OnPersonalWaypointClicked(
                        PersonalWaypoint(
                            latitude = poi.latLng.latitude,
                            longitude = poi.latLng.longitude,
                            placeId = poi.placeId
                        )
                    )
                )
                true
            }
        ) {

            PulseComponentAnimation(
                latLng = LatLng(
                    state.currentMarkerState.position.latitude,
                    state.currentMarkerState.position.longitude
                )
            )

            if (state.selectedJourney == null) {
                state.allMyWaypoints.forEach { waypoint ->
                    MarkerComposable(
                        state = MarkerState(
                            position = LatLng(
                                waypoint.latitude,
                                waypoint.longitude
                            )
                        ),
                        onClick = {
                            onAction(MapActions.OnPersonalWaypointClicked(waypoint))
                            true
                        },
//                        anchor = Offset(1.0f, 1.1f)
                    ) {
                        Image(
                            painter = painterResource(
                                when (waypoint.type) {
                                    WaypointType.START -> R.drawable.icon_start
                                    WaypointType.INTERMEDIATE -> if (waypoint.isUnlocked) R.drawable.icon_unlocked else R.drawable.icon_intermidiate
                                    WaypointType.END -> R.drawable.icon_end
                                    WaypointType.CURRENT_LOCATION -> R.drawable.icon_start
                                    WaypointType.FRAGMENT -> R.drawable.icon_fragment
                                    WaypointType.QUEST_WAYPOINT -> R.drawable.icon_quest
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            state.discoveredPublicWaypoints.forEach { waypoint ->
                MarkerComposable(
                    state = MarkerState(
                        position = LatLng(
                            waypoint.latitude,
                            waypoint.longitude
                        )
                    ),
//                    anchor = Offset(1.0f, 1.1f),
                    onClick = {
                        onAction(MapActions.OnFragmentClicked(waypoint))
                        true
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_fragment),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            if (state.selectedJourney != null) {
                state.waypointsForSelectedJourney.forEach { waypoint ->
                    MarkerComposable(
                        state = MarkerState(
                            position = LatLng(
                                waypoint.latitude,
                                waypoint.longitude
                            )
                        ),
                        onClick = {
                            onAction(MapActions.OnPersonalWaypointClicked(waypoint))
                            true
                        },
//                        anchor = Offset(1.0f, 1.1f)
                    ) {
                        Image(
                            painter = painterResource(
                                when (waypoint.type) {
                                    WaypointType.START -> R.drawable.icon_start
                                    WaypointType.INTERMEDIATE -> if (waypoint.isUnlocked) R.drawable.icon_unlocked else R.drawable.icon_intermidiate
                                    WaypointType.END -> R.drawable.icon_end
                                    WaypointType.CURRENT_LOCATION -> R.drawable.icon_start
                                    WaypointType.FRAGMENT -> R.drawable.icon_fragment
                                    WaypointType.QUEST_WAYPOINT -> R.drawable.icon_quest
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Polyline(
                    points = PolyUtil.decode(state.selectedJourney.encodedPolyline)
                )
            }

            if (state.isQuestsVisible) {
                state.quests.forEach { quest ->
                    MarkerComposable(
                        state = MarkerState(
                            position = LatLng(
                                quest.latitude,
                                quest.longitude
                            )
                        ),
//                        anchor = Offset(1.0f, 1.1f)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon_quest),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

    }


    LoadingOverlay(state.isAnimatingCamera, 0f)
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun PulseComponentAnimation(
    maxPulseSize: Float = 50f,
    minPulseSize: Float = 0f,
    latLng: LatLng
) {

    val infiniteTransition = rememberInfiniteTransition(label = "PulseComponentAnimation")

    val radius by infiniteTransition.animateFloat(
        initialValue = minPulseSize,
        targetValue = maxPulseSize,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000), repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ), label = "alpha"
    )

    val newLat = latLng.latitude + -0.00004
    val newLng = latLng.longitude

    MarkerComposable(
        state = MarkerState(position = LatLng(newLat, newLng)),
        flat = true
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(color = Color(0xff2FAA59), shape = CircleShape)
                .border(width = 2.dp, color = Color.White, shape = CircleShape)
        )
    }

    Circle(
        center = latLng,
        clickable = false,
        fillColor = Color(0xff2FAA59).copy(alpha = alpha),
        radius = radius.toDouble(),
        strokeColor = Color.Transparent,
        strokeWidth = 0f,
        tag = "",
        onClick = { }
    )
}