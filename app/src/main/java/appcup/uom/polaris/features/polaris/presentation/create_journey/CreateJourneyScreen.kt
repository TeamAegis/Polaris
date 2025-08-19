package appcup.uom.polaris.features.polaris.presentation.create_journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.presentation.components.LoadingOverlay
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisInputField
import appcup.uom.polaris.core.presentation.components.PolarisTopAppBar
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.chat.presentation.chat.ChatBottomSheet
import appcup.uom.polaris.features.chat.presentation.chat.ChatViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message.ConversationalAIMessage
import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.polaris.domain.Waypoint
import appcup.uom.polaris.features.polaris.domain.WaypointType
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.WaypointSelectorBottomSheet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun CreateJourneyScreen(
    viewModel: CreateJourneyViewModel = koinViewModel(),
    conversationAIViewModel: ConversationalAIViewModel,
    chatViewModel: ChatViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(
        state.value.startingWaypoint,
        state.value.endingWaypoint,
        state.value.intermediateWaypoints,
        state.value.polyline
    ) {
        val boundsBuilder = LatLngBounds.builder()
        boundsBuilder.include(state.value.startingMarkerState.position)
        if (state.value.endingMarkerState != null)
            boundsBuilder.include(state.value.endingMarkerState!!.position)
        state.value.intermediateMarkerStates.forEach { markerState ->
            boundsBuilder.include(markerState.position)
        }
        state.value.polyline.forEach {
            boundsBuilder.include(it)
        }

        val bounds = boundsBuilder.build()

        val padding = 100

        state.value.cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
            durationMs = 200
        )
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is CreateJourneyEvent.OnError -> {
                    snackbarHostState.showSnackbar(it.message)
                }

                is CreateJourneyEvent.OnJourneyCreated -> {
                    onBack()
                }
            }
        }
    }

    CreateJourneyScreenImpl(
        state = state.value,
        conversationAIViewModel = conversationAIViewModel,
        chatViewModel = chatViewModel,
        snackbarHostState = snackbarHostState,
    ) { action ->
        when (action) {
            CreateJourneyAction.OnBackClicked -> {
                onBack()
            }

            else -> {
                viewModel.onAction(action)
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalUuidApi::class
)
@Composable
fun CreateJourneyScreenImpl(
    state: CreateJourneyState,
    conversationAIViewModel: ConversationalAIViewModel,
    chatViewModel: ChatViewModel,
    snackbarHostState: SnackbarHostState,
    onAction: (CreateJourneyAction) -> Unit
) {

    val chatWithAIBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sendMessageToLiveAgentBottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (state.isWaypointSelectorVisible) {
        WaypointSelectorBottomSheet(
            waypointType = state.waypointType
        ) { placeInfo ->
            if (placeInfo != null) {
                onAction(CreateJourneyAction.OnWaypointSelectorResult(placeInfo))
            }
            onAction(
                CreateJourneyAction.OnWaypointSelectorVisibilityChanged(
                    false,
                    WaypointType.START
                )
            )
        }
    }


    Scaffold(
        modifier = Modifier
            .imePadding()
            .statusBarsPadding()
            .fillMaxSize(),
        topBar = {
            PolarisTopAppBar(
                "Create Journey",
                navigationIcon = {
                    PolarisIconButton(
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                    ) {
                        onAction(CreateJourneyAction.OnBackClicked)
                    }
                }
            ) { }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .polarisDropShadow(),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                                .clip(RoundedCornerShape(16.dp)),
                            cameraPositionState = state.cameraPositionState,
                            uiSettings = Constants.MAP_PREVIEW_UI_SETTINGS,
                            properties = Constants.MAP_DEFAULT_PROPERTIES
                        ) {
                            Marker(
                                state = state.startingMarkerState,
                            )

                            state.intermediateMarkerStates.forEach { markerState ->
                                Marker(
                                    state = markerState
                                )

                            }

                            if (state.endingMarkerState != null)
                                Marker(
                                    state = state.endingMarkerState,
                                )

                            if (state.polyline.isNotEmpty() && (state.endingMarkerState != null || state.intermediateWaypoints.isNotEmpty())) {
                                Polyline(
                                    state.polyline,
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        "Journey Details",
                        fontWeight = FontWeight.Bold,
                        fontStyle = MaterialTheme.typography.titleLarge.fontStyle
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PolarisInputField(
                            label = "Journey Name",
                            value = state.journeyName,
                            onValueChange = {
                                onAction(CreateJourneyAction.OnJourneyNameChanged(it))
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Words
                            )
                        )
                        PolarisInputField(
                            label = "Journey Description",
                            value = state.journeyDescription,
                            onValueChange = {
                                onAction(CreateJourneyAction.OnJourneyDescriptionChanged(it))
                            },
                            minLines = 3,
                            maxLines = 5,
                            singleLine = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Default,
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        "Preferences",
                        fontWeight = FontWeight.Bold,
                        fontStyle = MaterialTheme.typography.titleLarge.fontStyle
                    )
                }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Preferences.entries.forEach { preference ->
                            val selected = state.selectedPreferences.contains(preference)
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    if (selected) {
                                        onAction(CreateJourneyAction.OnPreferencesRemoved(preference))
                                    } else {
                                        onAction(CreateJourneyAction.OnPreferencesAdded(preference))
                                    }
                                },
                                label = { Text(preference.label) },
                                leadingIcon = {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Localized description",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        "Waypoints",
                        fontWeight = FontWeight.Bold,
                        fontStyle = MaterialTheme.typography.titleLarge.fontStyle
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    WaypointCard(
                        title = "Start",
                        waypoint = state.startingWaypoint,
                        onClick = {
                            onAction(
                                CreateJourneyAction.OnWaypointSelectorVisibilityChanged(
                                    true,
                                    WaypointType.START
                                )
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(
                    state.intermediateWaypoints.size,
                    key = { index -> state.intermediateWaypoints[index].id }) { index ->
                    Spacer(modifier = Modifier.height(4.dp))

                    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
                        SwipeToDismissBoxValue.Settled,
                        SwipeToDismissBoxDefaults.positionalThreshold
                    )
                    SwipeToDismissBox(
                        state = swipeToDismissBoxState,
                        onDismiss = {
                            if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                                onAction(CreateJourneyAction.OnIntermediateWaypointRemoved(index))
                            }
                        },
                        backgroundContent = {
                            when (swipeToDismissBoxState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove item",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.Red)
                                            .wrapContentSize(Alignment.CenterStart)
                                            .padding(12.dp),
                                        tint = Color.White
                                    )
                                }

                                SwipeToDismissBoxValue.EndToStart -> {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove item",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.Red)
                                            .wrapContentSize(Alignment.CenterEnd)
                                            .padding(12.dp),
                                        tint = Color.White
                                    )
                                }

                                SwipeToDismissBoxValue.Settled -> {}
                            }
                        }
                    ) {
                        WaypointCard(
                            title = "Intermediary",
                            waypoint = state.intermediateWaypoints[index],
                            onClick = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                onAction(
                                    CreateJourneyAction.OnWaypointSelectorVisibilityChanged(
                                        true,
                                        WaypointType.INTERMEDIATE
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Add Waypoint")
                        }

                        OutlinedButton(
                            onClick = {

                            }
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Generate")
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    WaypointCard(
                        title = "End",
                        waypoint = state.endingWaypoint,
                        onClick = {
                            onAction(
                                CreateJourneyAction.OnWaypointSelectorVisibilityChanged(
                                    true,
                                    WaypointType.END
                                )
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    Button(
                        onClick = {
                            onAction(CreateJourneyAction.OnCreateJourneyClicked)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .polarisDropShadow()
                            .clip(RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        if (state.isCreatingJourney) {
                            Text(text = "Creating...")
                            Spacer(modifier = Modifier.width(16.dp))
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .height(16.dp)
                                    .width(16.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(text = "Create Journey")
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(128.dp))
                }
            }

            if (state.isSendMessageToAIBottomSheetOpen) {
                ChatBottomSheet(
                    onDismiss = {
                        onAction(CreateJourneyAction.OnSendMessageToAIBottomSheetClicked)
                    },
                    viewModel = chatViewModel,
                    snackbarHostState = snackbarHostState,
                    sheetState = chatWithAIBottomSheetState
                )
            }
            if (state.isSendMessageToLiveAgentBottomSheetOpen) {
                ConversationalAIMessage(
                    conversationAIViewModel,
                    snackbarHostState,
                    sendMessageToLiveAgentBottomSheetState
                ) {
                    onAction(CreateJourneyAction.OnSendMessageToLiveAgentBottomSheetClicked)
                }
            }
            HorizontalFloatingToolbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -ScreenOffset)
                    .polarisDropShadow(),
                expanded = state.isToolbarExpanded,
                trailingContent = {
                    Row {
                        IconButton(onClick = {
                            onAction(CreateJourneyAction.OnSendMessageToAIBottomSheetClicked)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Message,
                                contentDescription = "Localized description"
                            )
                        }

                        IconButton(
                            onClick = {
                                onAction(CreateJourneyAction.OnSendMessageToLiveAgentBottomSheetClicked)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CallMade,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
                content = {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {
                            onAction(CreateJourneyAction.OnToolbarExpandedChanged(!state.isToolbarExpanded))
                        },
                    ) {
                        Icon(
                            if (state.isToolbarExpanded)
                                Icons.Default.ArrowBackIosNew
                            else
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        }
    }

    LoadingOverlay(state.isCreatingJourney)
}

@Composable
fun WaypointCard(
    title: String,
    waypoint: Waypoint?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .polarisDropShadow(),
        shape = RoundedCornerShape(16.dp),
        onClick = {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                waypoint?.let {
                    Text(it.name, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        it.address ?: "Unknown Address",
                        style = MaterialTheme.typography.bodySmall
                    )
                } ?: Text("Tap to select", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
