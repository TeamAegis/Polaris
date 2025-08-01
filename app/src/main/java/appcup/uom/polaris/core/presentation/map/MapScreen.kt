package appcup.uom.polaris.core.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MapScreenImpl(
        state = state,
        onAction = { action ->

        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenImpl(
    state: MapState,
    onAction: (MapActions) -> Unit
) {
    Scaffold(
        topBar = {},
        modifier = Modifier.Companion
            .fillMaxSize()
    ) { contentPadding ->
        val singapore = LatLng(1.35, 103.87)
        val singaporeMarkerState = rememberUpdatedMarkerState(position = singapore)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 10f)
        }
        GoogleMap(
            contentPadding = contentPadding,
            modifier = Modifier.Companion
                .fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = singaporeMarkerState,
                title = "Singapore",
                snippet = "Marker in Singapore"
            )
        }
    }

    LoadingOverlay(isLoading = state.isLoading)
}