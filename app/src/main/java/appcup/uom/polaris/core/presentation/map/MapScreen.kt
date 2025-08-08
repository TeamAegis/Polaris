package appcup.uom.polaris.core.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.extras.theme.map_style
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.components.MapSearchBar
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(state.currentCameraPositionState.isMoving) {
        when (state.currentCameraPositionState.cameraMoveStartedReason) {
            CameraMoveStartedReason.GESTURE -> {
                viewModel.onAction(MapActions.OnTrackingUserChanged(false))
            }

            else -> {}
        }
    }

    MapScreenImpl(
        state = state,
        searchState = searchState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenImpl(
    state: MapState,
    searchState: TextFieldState,
    onAction: (MapActions) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    Scaffold(
        topBar = {
            MapSearchBar(
                textFieldState = searchState,
                onQueryChange = {
                    onAction(MapActions.OnSearchQueryChanged(it))
                },
                onSearch = { autocompletePlace: AutocompletePlace? ->

                },
                predictions = state.predictions.map { it.toPlaceDetails() },
                selectedPlace = state.selectedPlace,
                modifier = Modifier
                    .fillMaxSize(),
                onSelected = { autocompletePlace: AutocompletePlace ->
                    onAction(MapActions.OnSelectedPlaceChanged(autocompletePlace))
                },
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { contentPadding ->
        GoogleMap(
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = state.currentCameraPositionState,
            uiSettings = MapUiSettings(
                compassEnabled = false,
//                indoorLevelPickerEnabled = false,
//                mapToolbarEnabled = false,
//                myLocationButtonEnabled = false,
//                rotationGesturesEnabled = false,
//                scrollGesturesEnabled = false,
//                scrollGesturesEnabledDuringRotateOrZoom = false,
//                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
//                zoomGesturesEnabled = false,
            ),
            onMyLocationButtonClick = {


                true
            },
            properties = MapProperties(
                isBuildingEnabled = true,
                isMyLocationEnabled = true,
                mapStyleOptions = MapStyleOptions(map_style)
            )
        ) {
//            Marker(
//                state = state.currentMarkerState
//            )
        }
    }

    LoadingOverlay(isLoading = state.isLoading)
}
