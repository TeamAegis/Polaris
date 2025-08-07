package appcup.uom.polaris.features.polaris.presentation.create_journey

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedToggleButton
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.extras.theme.PolarisDarkColorScheme
import appcup.uom.polaris.core.extras.theme.PolarisLightColorScheme
import appcup.uom.polaris.core.extras.theme.PolarisTheme
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisInputField
import appcup.uom.polaris.core.presentation.components.PolarisTopAppBar
import appcup.uom.polaris.core.presentation.components.Robot
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.chat.presentation.chat.ChatBottomSheet
import appcup.uom.polaris.features.chat.presentation.chat.ChatViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message.ConversationalAIMessage
import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.WaypointSelectorAction
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.WaypointSelectorDialog
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateJourneyScreen(
    viewModel: CreateJourneyViewModel = koinViewModel(),
    conversationAIViewModel: ConversationalAIViewModel,
    chatViewModel: ChatViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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

    if (state.isStartingLocationSelectorVisible) {
        WaypointSelectorDialog {
            onAction(CreateJourneyAction.OnStartingLocationVisibilityChanged(false))
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
                        icon =
                            {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                    ) {
                        onAction(CreateJourneyAction.OnBackClicked)
                    }
                })
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
                            }
                        )
                        PolarisInputField(
                            label = "Journey Description",
                            value = state.journeyDescription,
                            onValueChange = {
                                onAction(CreateJourneyAction.OnJourneyDescriptionChanged(it))
                            },
                            minLines = 3
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
                        "Start & End Locations",
                        fontWeight = FontWeight.Bold,
                        fontStyle = MaterialTheme.typography.titleLarge.fontStyle
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Start",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                fontStyle = MaterialTheme.typography.titleMedium.fontStyle
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                                ElevatedToggleButton(
                                    checked = !state.isStartingLocationCustom,
                                    onCheckedChange = {
                                        onAction(
                                            CreateJourneyAction.OnStartingLocationCustomChanged(
                                                false
                                            )
                                        )
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedLeadingButtonShapes()

                                ) {
                                    Icon(
                                        Icons.Default.MyLocation,
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text("Current")
                                }

                                ElevatedToggleButton(
                                    checked = state.isStartingLocationCustom,
                                    onCheckedChange = {
                                        onAction(
                                            CreateJourneyAction.OnStartingLocationCustomChanged(
                                                true
                                            )
                                        )
                                        onAction(
                                            CreateJourneyAction.OnStartingLocationVisibilityChanged(
                                                true
                                            )
                                        )
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedTrailingButtonShapes()

                                ) {
                                    Icon(
                                        Icons.Default.LocationSearching,
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text("Custom")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(16.dp),
                            onClick = {
                                onAction(
                                    CreateJourneyAction.OnStartingLocationVisibilityChanged(
                                        true
                                    )
                                )
                            }
                        ) {
                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9f)
                                    .clip(
                                        if (state.isStartingLocationCustom)
                                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                        else
                                            RoundedCornerShape(16.dp)
                                    ),
                                cameraPositionState = state.startingCameraPositionState,
                                uiSettings = MapUiSettings(
                                    compassEnabled = false,
                                    indoorLevelPickerEnabled = false,
                                    mapToolbarEnabled = false,
                                    myLocationButtonEnabled = false,
                                    rotationGesturesEnabled = false,
                                    scrollGesturesEnabled = false,
                                    scrollGesturesEnabledDuringRotateOrZoom = false,
                                    tiltGesturesEnabled = false,
                                    zoomControlsEnabled = false,
                                    zoomGesturesEnabled = false,
                                )
                            ) {
                                Marker(
                                    state = state.startingMarkerState,
                                )
                            }
                            if (state.isStartingLocationCustom) {

                            }
                        }

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "End",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                fontStyle = MaterialTheme.typography.titleMedium.fontStyle
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                                ElevatedToggleButton(
                                    checked = !state.isEndingLocationCustom,
                                    onCheckedChange = {

                                        onAction(
                                            CreateJourneyAction.OnEndingLocationCustomChanged(
                                                false
                                            )
                                        )
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedLeadingButtonShapes()

                                ) {
                                    Icon(
                                        Robot,
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text("AI")
                                }

                                ElevatedToggleButton(
                                    checked = state.isEndingLocationCustom,
                                    onCheckedChange = {
                                        onAction(
                                            CreateJourneyAction.OnEndingLocationCustomChanged(
                                                true
                                            )
                                        )
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedTrailingButtonShapes()

                                ) {
                                    Icon(
                                        Icons.Default.LocationSearching,
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text("Custom")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                uiSettings = MapUiSettings(
                                    compassEnabled = false,
                                    indoorLevelPickerEnabled = false,
                                    mapToolbarEnabled = false,
                                    myLocationButtonEnabled = false,
                                    rotationGesturesEnabled = false,
                                    scrollGesturesEnabled = false,
                                    scrollGesturesEnabledDuringRotateOrZoom = false,
                                    tiltGesturesEnabled = false,
                                    zoomControlsEnabled = false,
                                    zoomGesturesEnabled = false,
                                )
                            )
                            Text("Address")
                            Text("More Details")
                        }
                    }
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
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun CreateJourneyScreenPreview() {
    PolarisTheme(
        darkTheme = false,
        dynamicColor = false,
        lightColorScheme = PolarisLightColorScheme,
        darkColorScheme = PolarisDarkColorScheme
    ) {
        Scaffold(
            topBar = {
                PolarisTopAppBar(
                    title = "Create Journey",
                    navigationIcon = {
                        PolarisIconButton(
                            icon = {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            },
                            onClick = {}
                        )
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                stickyHeader {
                    Text(
                        "Journey Details",
                        fontWeight = FontWeight.Bold,
                        fontStyle = MaterialTheme.typography.titleLarge.fontStyle
                    )
                }
                item {
                    Column {
                        PolarisInputField(
                            label = "Journey Name",
                            value = "",
                            onValueChange = {}
                        )
                        PolarisInputField(
                            label = "Journey Description",
                            value = "",
                            onValueChange = {},
                            minLines = 3
                        )
                    }
                }
                stickyHeader {
                    Text("Preferences")
                }
                item {
                    val preferences = remember {
                        mutableStateListOf(
                            "\uD83C\uDF7D\uFE0F Food",
                            "\uD83C\uDFDB\uFE0F Attractions",
                            "\uD83C\uDF3F Nature",
                            "\uD83C\uDFE8 Hotels",
                            "\uD83D\uDECD\uFE0F Shopping",
                            "\uD83D\uDEBB Essentials"
                        )
                    }
                    val selectedPreferences = remember { mutableStateListOf<String>() }
                    FlowRow(
                        verticalArrangement = Arrangement.Top,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        preferences.forEach { preference ->
                            val selected = selectedPreferences.contains(preference)
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    if (selected) {
                                        selectedPreferences.remove(preference)
                                    } else {
                                        selectedPreferences.add(preference)
                                    }
                                },
                                label = { Text(preference) },
                                leadingIcon = {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Localized description",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                stickyHeader {
                    Text("Start & End Locations")
                }
                item {
                    Column {
                        val options = listOf("Current", "Custom")
                        val unCheckedIcons =
                            listOf(Icons.Outlined.MyLocation, Icons.Outlined.LocationSearching)
                        val checkedIcons =
                            listOf(Icons.Filled.MyLocation, Icons.Filled.LocationSearching)
                        var selectedIndex by remember { mutableIntStateOf(0) }
                        val checked = remember { mutableStateListOf(false, false) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text("Start", modifier = Modifier.weight(1f))


                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                            ) {
                                ToggleButton(
                                    checked = selectedIndex == 0,
                                    onCheckedChange = { selectedIndex = 0 },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedLeadingButtonShapes()

                                ) {
                                    Icon(
                                        if (checked[0]) checkedIcons[0] else unCheckedIcons[0],
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text(options[0])
                                }

                                ToggleButton(
                                    checked = selectedIndex == 1,
                                    onCheckedChange = { selectedIndex = 1 },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedTrailingButtonShapes()

                                ) {
                                    Icon(
                                        if (checked[1]) checkedIcons[1] else unCheckedIcons[1],
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text(options[1])
                                }
                            }

                        }


                        val singapore = LatLng(1.35, 103.87)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(singapore, 10f)
                        }
                        Card(
                            modifier = Modifier
                                .padding(0.dp)
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            GoogleMap(
                                cameraPositionState = cameraPositionState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            )
                            Text("Address")
                            Text("More Details")
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                                .polarisDropShadow()
                                .background(MaterialTheme.colorScheme.surface),
                        ) {

                        }

                    }
                }

                item {
                    Column {
                        val options = listOf("AI", "Custom")
                        val unCheckedIcons =
                            listOf(Robot, Icons.Outlined.LocationSearching)
                        val checkedIcons =
                            listOf(Robot, Icons.Filled.LocationSearching)
                        var selectedIndex by remember { mutableIntStateOf(0) }
                        val checked = remember { mutableStateListOf(false, false) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text("End", modifier = Modifier.weight(1f))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                            ) {
                                ToggleButton(
                                    checked = selectedIndex == 0,
                                    onCheckedChange = { selectedIndex = 0 },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedLeadingButtonShapes()

                                ) {
                                    Icon(
                                        if (checked[0]) checkedIcons[0] else unCheckedIcons[0],
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text(options[0])
                                }

                                ToggleButton(
                                    checked = selectedIndex == 1,
                                    onCheckedChange = { selectedIndex = 1 },
                                    modifier = Modifier.semantics {
                                        role = Role.RadioButton
                                    },
                                    shapes =
                                        ButtonGroupDefaults.connectedTrailingButtonShapes()

                                ) {
                                    Icon(
                                        if (checked[1]) checkedIcons[1] else unCheckedIcons[1],
                                        contentDescription = "Localized description",
                                    )
                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                    Text(options[1])
                                }
                            }

                        }


                        val singapore = LatLng(1.35, 103.87)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(singapore, 10f)
                        }
                        Card(
                            modifier = Modifier
                                .padding(0.dp)
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            GoogleMap(
                                cameraPositionState = cameraPositionState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            )
                            Text("Address")
                            Text("More Details")
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                                .polarisDropShadow()
                                .background(MaterialTheme.colorScheme.surface),
                        ) {

                        }

                    }
                }
                stickyHeader {
                    Text("Waypoint(s)")
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp)
                            .polarisDropShadow()
                            .clip(RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Localized description"
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Add a Waypoint")
                    }
                }
            }
        }
    }
}
