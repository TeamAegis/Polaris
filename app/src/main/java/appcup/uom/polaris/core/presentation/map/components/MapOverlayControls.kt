package appcup.uom.polaris.core.presentation.map.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import appcup.uom.polaris.core.presentation.map.MapState

@Composable
fun MapOverlayControls(
    modifier: Modifier = Modifier,
    state: MapState,
    onToggleMap: (() -> Unit)? = null,
    onCompassClick: (() -> Unit)? = null
) {

    val rawBearing = state.currentCameraPositionState.position.bearing
    val animatedBearing = rememberSmoothBearing(rawBearing)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .align(Alignment.TopEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AnimatedVisibility(
                visible = state.isTrackingUser || animatedBearing != -45.0f,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                CompassButton(
                    bearing = animatedBearing,
                    onClick = { onCompassClick?.invoke() }
                )
            }

        }

        Column(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 104.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            TrackingFab(
                isTracking = state.isTrackingUser,
                onToggle = {
                    onToggleMap?.invoke()
                }
            )
        }
    }
}

@Composable
fun TrackingFab(
    isTracking: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    FloatingActionButton(
        onClick = onToggle,
        modifier = modifier.size(size),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(8.dp)
    ) {
        val icon: ImageVector =
            if (isTracking) Icons.Default.MyLocation else Icons.Default.LocationSearching
        val description = if (isTracking) "Stop tracking" else "Start tracking"
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}

@Composable
fun CompassButton(
    modifier: Modifier = Modifier,
    bearing: Float,
    onClick: () -> Unit = {},
    size: Dp = 44.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .semantics { contentDescription = "Compass" }
    ) {
        Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            modifier = Modifier
                .size(size * 0.6f)
                .rotate(bearing)
        )
    }
}

@Composable
fun rememberSmoothBearing(targetBearing: Float): Float {
    val animated by animateFloatAsState(
        targetValue = targetBearing - 45.0f,
        animationSpec = tween(durationMillis = 10, easing = FastOutSlowInEasing)
    )
    return animated
}

