package appcup.uom.polaris.features.polaris.presentation.waypoint_selector


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.components.MapSearchBar
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import org.koin.androidx.compose.koinViewModel

@Composable
fun WaypointSelectorDialog(
    viewModel: WaypointSelectorViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val searchState = viewModel.searchState.collectAsStateWithLifecycle()

    WaypointSelectorDialogImpl(
        state = state.value,
        searchState = searchState.value,
        onAction = { action ->
            when (action) {
                WaypointSelectorAction.OnDismiss -> {
                    onDismiss()
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
fun WaypointSelectorDialogImpl(
    state: WaypointSelectorState,
    searchState: TextFieldState,
    onAction: (WaypointSelectorAction) -> Unit
) {
    if (state.isDismissDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                onAction(WaypointSelectorAction.OnDismissDialogVisibilityChanged(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(WaypointSelectorAction.OnDismissDialogVisibilityChanged(false))
                        onAction(WaypointSelectorAction.OnDismiss)
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(WaypointSelectorAction.OnDismissDialogVisibilityChanged(false))
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Dismiss")
            },
            text = {
                Text("Are you sure you want to dismiss?")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null
                )
            }
        )
    }

    Dialog(
        onDismissRequest = {
            onAction(WaypointSelectorAction.OnDismissDialogVisibilityChanged(true))
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Scaffold(
            topBar = {
                MapSearchBar(
                    textFieldState = searchState,
                    onQueryChange = {
                        onAction(WaypointSelectorAction.OnSearchQueryChanged(it))
                    },
                    onSearch = { autocompletePlace: AutocompletePlace? ->

                    },
                    predictions = state.predictions.map { it.toPlaceDetails() },
                    selectedPlace = state.selectedPlace,
                    modifier = Modifier
                        .fillMaxSize(),
                    onSelected = { autocompletePlace: AutocompletePlace ->
                        onAction(WaypointSelectorAction.OnSelectedPlaceChanged(autocompletePlace))
                    },
                )
            },
            bottomBar = {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                            top = 8.dp
                        )
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Text("Long press to select starting location")
                }
            }
        ) { contentPadding ->
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                uiSettings = MapUiSettings(
                )
            ) {

            }
        }
    }
}