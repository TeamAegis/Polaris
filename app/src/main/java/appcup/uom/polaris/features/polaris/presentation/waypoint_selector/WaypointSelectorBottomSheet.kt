package appcup.uom.polaris.features.polaris.presentation.waypoint_selector


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.features.polaris.domain.Waypoint
import appcup.uom.polaris.features.polaris.domain.WaypointType
import appcup.uom.polaris.features.polaris.presentation.components.AnimatedMarkerIcon
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.components.MapSearchBar
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.components.WaypointCard
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun WaypointSelectorBottomSheet(
    viewModel: WaypointSelectorViewModel = koinViewModel(),
    waypointType: WaypointType,
    onDismiss: (Waypoint?) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val searchState = viewModel.searchState.collectAsStateWithLifecycle()

    WaypointSelectorBottomSheetImpl(
        state = state.value,
        waypointType = waypointType,
        searchState = searchState.value,
        onAction = { action ->
            when (action) {
                WaypointSelectorAction.OnConfirm -> {
                    onDismiss(state.value.selectedWaypoint?.copy(id = Uuid.random()))
                }

                WaypointSelectorAction.OnDismiss -> {
                    onDismiss(null)
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WaypointSelectorBottomSheetImpl(
    state: WaypointSelectorState,
    waypointType: WaypointType,
    searchState: TextFieldState,
    onAction: (WaypointSelectorAction) -> Unit
) {

    ModalBottomSheet(
        sheetGesturesEnabled = false,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = {
            onAction(WaypointSelectorAction.OnDismiss)
        },
        dragHandle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Small drag handle visual
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Title
                Text(
                    text = "Select Location",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Tick or Close icon
                IconButton(
                    onClick = {
                        onAction(WaypointSelectorAction.OnConfirm)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check, // Change to Icons.Default.Close if needed
                        contentDescription = "Confirm"
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                MapSearchBar(
                    textFieldState = searchState,
                    isSearching = state.isSearching,
                    onQueryChange = {
                        onAction(WaypointSelectorAction.OnSearchQueryChanged(it))
                    },
                    onSearch = { autocompletePlace: AutocompletePlace? ->
                        onAction(WaypointSelectorAction.OnSelectedPlaceChanged(autocompletePlace))
                    },
                    predictions = state.predictions.map { it.toPlaceDetails() },
                    selectedPlace = state.selectedPlace,
                    modifier = Modifier
                        .fillMaxSize(),
                    onSelected = { autocompletePlace: AutocompletePlace ->
                        onAction(WaypointSelectorAction.OnSelectedPlaceChanged(autocompletePlace))
                    },
                    expanded = state.expanded,
                    onExpandedChange = {
                        onAction(WaypointSelectorAction.OnSearchExpandedChanged(it))
                    }
                )
            },
            floatingActionButton = {
                if (!state.expanded) {
                    FloatingActionButton(
                        onClick = {
                            onAction(WaypointSelectorAction.SetToCurrentLocation)
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Filled.LocationOn, "Current Location")
                    }
                }
            },
            bottomBar = {
                if (state.selectedWaypoint != null && !state.expanded) {
                    WaypointCard(state.selectedWaypoint)
                }
            }
        ) { contentPadding ->
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = state.waypointCameraPositionState,
                properties = Constants.MAP_DEFAULT_PROPERTIES,
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    indoorLevelPickerEnabled = false,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                ),
                onMapClick = { location ->
                    onAction(
                        WaypointSelectorAction.OnMapClick(
                            location.latitude,
                            location.longitude
                        )
                    )
                },
                onPOIClick = { poi ->
                    onAction(WaypointSelectorAction.OnPoiClick(poi.placeId))
                }
            ) {
//                MarkerComposable(
//                    state = state.waypointMarkerState
//                ) {
//                    AnimatedMarkerIcon(waypointType)
//                }
                Marker(
                    state = state.waypointMarkerState,
                )
            }
        }
    }
}