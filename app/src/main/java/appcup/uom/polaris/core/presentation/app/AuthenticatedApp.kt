package appcup.uom.polaris.core.presentation.app

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import appcup.uom.polaris.R
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.presentation.components.AnimatedJourneyProgressTopBar
import appcup.uom.polaris.core.presentation.components.BottomBar
import appcup.uom.polaris.core.presentation.components.FilterFocus
import appcup.uom.polaris.core.presentation.components.JourneyCardPager
import appcup.uom.polaris.core.presentation.components.TrackingWaypointCard
import appcup.uom.polaris.core.presentation.components.draggableWithDynamicFling
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.core.presentation.components.rememberDraggableFlingStateWithDynamicSize
import appcup.uom.polaris.core.presentation.map.MapActions
import appcup.uom.polaris.core.presentation.map.MapScreen
import appcup.uom.polaris.core.presentation.map.MapViewModel
import appcup.uom.polaris.core.presentation.memories.MemoriesScreen
import appcup.uom.polaris.core.presentation.memories.memory.MemoryAction
import appcup.uom.polaris.core.presentation.memories.memory.MemoryBottomSheet
import appcup.uom.polaris.core.presentation.memories.memory.MemoryEvent
import appcup.uom.polaris.core.presentation.memories.memory.MemoryViewModel
import appcup.uom.polaris.core.presentation.more.MoreScreen
import appcup.uom.polaris.core.presentation.settings.SettingsScreen
import appcup.uom.polaris.features.auth.presentation.change_password.ChangePasswordScreen
import appcup.uom.polaris.features.auth.presentation.display_name.DisplayNameScreen
import appcup.uom.polaris.features.auth.presentation.forgot_password.ForgotPasswordScreen
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationNavArgs
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationScreen
import appcup.uom.polaris.features.auth.presentation.otp_reauthenticate.OtpReauthenticateNavArgs
import appcup.uom.polaris.features.auth.presentation.otp_reauthenticate.OtpReauthenticateScreen
import appcup.uom.polaris.features.chat.presentation.chat.ChatScreen
import appcup.uom.polaris.features.chat.presentation.chat.ChatViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.ai_camera.AICameraScreen
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai.ConversationalAI
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai.ConversationalAIAction
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message.ConversationalAIMessageAction
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallHandler
import appcup.uom.polaris.features.polaris.domain.Waypoint
import appcup.uom.polaris.features.polaris.presentation.create_journey.CreateJourneyScreen
import appcup.uom.polaris.features.polaris.presentation.fragments.FragmentsScreen
import appcup.uom.polaris.features.polaris.presentation.journey_details.JourneyDetailsScreen
import appcup.uom.polaris.features.polaris.presentation.journeys.JourneysScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalUuidApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticatedApp(
    viewModel: AppViewModel = koinInject()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val backStack =
        rememberNavBackStack(Screen.Map)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val chatViewModel: ChatViewModel = koinViewModel()


    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AppEvent.CameraPermissionDenied -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Camera permission denied")
                    }
                }

                AppEvent.CameraPermissionDeniedPermanent -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Camera permission denied. Please enable it from settings.")
                    }
                }

                AppEvent.LocationPermissionDenied -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Location permission denied")
                    }
                }

                AppEvent.LocationPermissionDeniedPermanent -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Location permission denied. Please enable it from settings.")
                    }
                }

                is AppEvent.PublicWaypointCreated -> {
                    backStack.add(Screen.Fragments(event.waypoint.id!!.toString()))
                }

                is AppEvent.OnError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.error)
                    }
                }

                else -> {}
            }
        }
    }


    val conversationAIViewModel: ConversationalAIViewModel = koinViewModel()
    val conversationAIState = conversationAIViewModel.state.collectAsStateWithLifecycle()

    val mapViewModel: MapViewModel = koinViewModel()
    val mapState = mapViewModel.state.collectAsStateWithLifecycle()

    val functionCallHandler = FunctionCallHandler(navBackStack = backStack)
    LaunchedEffect(Unit) {
        EventBus.collectEvents { event ->
            when (event) {
                is Event.OnFunctionCall -> {
                    functionCallHandler.handleFunctionCall(event.func, event.args, event.onResult)
                }

                is Event.OnGetAvailableJourneys -> {
                    event.onResult(mapViewModel.getStartableJourneys())
                }

                is Event.OnGetUserLocation -> {
                    mapViewModel.getUserCurrentLocation(event.onResult)
                }

                is Event.OnSearchNearbyPlaces -> {
                    event.onResult(mapViewModel.getNearbyPlaces(event.radius))
                }

                is Event.OnSearchPlaces -> {
                    event.onResult(mapViewModel.searchPlaces(event.searchQuery))
                }

                is Event.OnSendWaypoint -> {
                    mapViewModel.onWaypointReceived(event.placeId, event.onResult)
                }

                is Event.OnStartJourney -> {
                    event.onResult(mapViewModel.startJourney(event.journeyId))
                }

                is Event.OnStopJourney -> {
                    event.onResult(mapViewModel.stopJourney())
                }

                is Event.OnWaypointUnlocked -> {
                    conversationAIViewModel.onMessageAction(
                        ConversationalAIMessageAction.OnWaypointUnlocked(
                            event.message
                        )
                    )
                }

                else -> {}
            }
        }
    }


    val memoryViewModel: MemoryViewModel = koinViewModel()
    val memoryState = memoryViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val createImageFile = remember {
        {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "MEMORY_$timeStamp"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(imageFileName, ".jpg", storageDir)
        }
    }
    var tempImageFile by remember { mutableStateOf<File?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && tempImageUri != null) {
            memoryViewModel.onAction(MemoryAction.OnImageCaptured(tempImageUri!!))
        }
    }

    val isJourneyInProgress =
        mapState.value.selectedJourney != null && mapState.value.waypointsForSelectedJourney.isNotEmpty()



    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding(),
        topBar = {
            if (isJourneyInProgress) {
                AnimatedJourneyProgressTopBar(
                    targetProgress = mapState.value.waypointsForSelectedJourney.filter { it.isUnlocked }.size.toFloat() / mapState.value.waypointsForSelectedJourney.size,
                    journeyName = mapState.value.selectedJourney!!.name,
                    onStopJourney = {
                        mapViewModel.onAction(MapActions.OnStopJourneyClicked)
                    }
                )
            }
        },
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = backStack.last() is Screen.Map && mapState.value.selectedJourney == null && mapState.value.startableJourneys.isNotEmpty() && mapState.value.shouldShowStartJourneyDialog,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    JourneyCardPager(
                        journeys = mapState.value.startableJourneys,
                        onStartJourney = { journey ->
                            mapViewModel.onAction(MapActions.OnStartJourneyClicked(journey))
                        },
                        onViewDetails = {
                            backStack.add(Screen.JourneyDetails(it.id!!.toString()))
                        }
                    )
                }

                AnimatedVisibility(mapState.value.isSelectedWaypointCardVisible && backStack.last() is Screen.Map) {
                    TrackingWaypointCard(
                        waypoint = if (mapState.value.selectedWaypoint != null) mapState.value.selectedWaypoint!! else Waypoint(),
                        isLoading = mapState.value.selectedWaypoint == null,
                        weather = mapState.value.selectedWeatherData,
                        onDismiss = {
                            mapViewModel.onAction(MapActions.OnTrackingWaypointCardDismissed)
                        },
                        onViewMore = {
                            backStack.add(Screen.JourneyDetails(mapState.value.selectedWaypoint!!.id.toString()))
                        }
                    )
                }

                AnimatedVisibility(
                    visible = when (backStack.last()) {
                        Screen.Map, Screen.Journeys, Screen.Memories, Screen.More -> true
                        else -> false
                    },
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomBar(navBackStack = backStack)
                }
            }
        },
        floatingActionButton = {
            if (backStack.last() is Screen.Map && !conversationAIState.value.isRecording) {
                val primaryColor = MaterialTheme.colorScheme.primary
                FloatingActionButtonMenu(
                    modifier = Modifier
                        .absoluteOffset(
                            x = 16.dp,
                            y = 16.dp
                        ),
                    expanded = state.value.isFabMenuExpanded,
                    button = {
                        ToggleFloatingActionButton(
                            containerColor = { primaryColor },
                            modifier = Modifier
                                .semantics {
                                    traversalIndex = -1f
                                    stateDescription =
                                        if (state.value.isFabMenuExpanded) "Expanded" else "Collapsed"
                                    contentDescription = "Toggle menu"
                                }
                                .animateFloatingActionButton(
                                    visible = true,
                                    alignment = Alignment.BottomEnd,
                                ),
                            checked = state.value.isFabMenuExpanded,
                            onCheckedChange = {
                                viewModel.onAction(
                                    AppAction.OnFabMenuExpanded(
                                        it
                                    )
                                )
                            },
                        ) {
                            val imageVector by remember {
                                derivedStateOf {
                                    if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                                }
                            }
                            Icon(
                                painter = rememberVectorPainter(imageVector),
                                contentDescription = null,
                                modifier = Modifier.animateIcon({ checkedProgress }),
                            )
                        }
                    },
                ) {
                    FabMenuItem.entries.forEachIndexed { i, item ->
                        FloatingActionButtonMenuItem(
                            modifier = Modifier.semantics {
                                isTraversalGroup = true
                                if (i == FabMenuItem.entries.size - 1) {
                                    customActions = listOf(
                                        CustomAccessibilityAction(
                                            label = "Close menu",
                                            action = {
                                                viewModel.onAction(
                                                    AppAction.OnFabMenuExpanded(
                                                        false
                                                    )
                                                )
                                                true
                                            },
                                        )
                                    )
                                }
                            },
                            onClick = {
                                viewModel.onAction(AppAction.OnFabMenuExpanded(false))
                                when (item) {
                                    FabMenuItem.AI_CAMERA -> {
                                        if (!state.value.hasCameraPermission) {
                                            viewModel.onAction(AppAction.RequestCameraPermission)
                                        } else {
                                            backStack.add(Screen.AICamera)
                                        }
                                    }

                                    FabMenuItem.VoiceAssistant -> {
                                        conversationAIViewModel.onAction(ConversationalAIAction.StartRecording)
                                    }

                                    FabMenuItem.CreateFragment -> {
                                        if (mapViewModel.canUserCreatePublicWaypoint()) {
                                            viewModel.onAction(AppAction.OnCreatePublicWaypointClicked)
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Cannot create new fragment: existing fragment nearby. Add to it instead.")
                                            }
                                        }
                                    }
                                }
                            },
                            icon = { Icon(item.imageVector, contentDescription = null) },
                            text = { Text(text = item.label) },
                        )
                    }
                }
            }

            if (backStack.last() is Screen.Map && conversationAIState.value.isRecording) {
                ConversationalAI(
                    viewModel = conversationAIViewModel,
                    snackbarHostState = snackbarHostState
                )
            }

            if (backStack.last() is Screen.Journeys) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        if (!state.value.hasLocationPermission) {
                            viewModel.onAction(AppAction.RequestLocationPermission)
                        } else {
                            scope.launch {
                                EventBus.emit(
                                    Event.OnCreateJourneyBottomSheetVisibilityChanged(
                                        true
                                    )
                                )
                            }
                            backStack.add(Screen.CreateJourney)
                        }
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NavDisplay(
                modifier = Modifier.padding(top = if (isJourneyInProgress) innerPadding.calculateTopPadding() - 16.dp else 0.dp),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                },
                popTransitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                },
                predictivePopTransitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                },
                entryProvider = entryProvider {


                    entry<Screen.ChangeDisplayName> {
                        DisplayNameScreen(
                            onBack = { backStack.removeLastOrNull() },
                            snackbarHostState = snackbarHostState
                        )
                    }


                    entry<Screen.ChangePassword> {
                        ChangePasswordScreen(
                            onBack = { message ->
                                backStack.removeLastOrNull()
                                if (message != null) {
                                    scope.launch { snackbarHostState.showSnackbar(message) }
                                }
                            },
                            navigateToReauthenticate = { message, password, confirmPassword ->
                                backStack.add(
                                    Screen.OtpReauthenticate(password, confirmPassword)
                                )
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    entry<Screen.ForgotPassword> {
                        ForgotPasswordScreen(
                            onBack = { backStack.removeLastOrNull() },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    entry<Screen.CreateJourney> {
                        CreateJourneyScreen(
                            conversationAIViewModel = conversationAIViewModel,
                            chatViewModel = chatViewModel,
                            snackbarHostState = snackbarHostState,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<Screen.Map> {
                        MapScreen(
                            viewModel = mapViewModel,
                            snackbarHostState = snackbarHostState,
                            onFragmentClicked = { waypoint ->
                                backStack.add(Screen.Fragments(waypoint.id!!.toString()))
                            }
                        )
                    }

                    entry<Screen.Memories> {
                        MemoriesScreen(snackbarHostState = snackbarHostState)
                    }

                    entry<Screen.More> {
                        MoreScreen(
                            navigateToSettings = { backStack.add(Screen.Settings) },
                            navigateToChat = { backStack.add(Screen.Chat) },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    entry<Screen.Settings> {
                        SettingsScreen(
                            onBack = { backStack.removeLastOrNull() },
                            navigateToChangeDisplayName = { backStack.add(Screen.ChangeDisplayName) },
                            navigateToChangePassword = { backStack.add(Screen.ChangePassword) },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    entry<Screen.AICamera> {
                        AICameraScreen(
                            viewModel = conversationAIViewModel,
                            onBack = { backStack.removeLastOrNull() })
                    }

                    entry<Screen.OtpConfirmRegistration> { screen ->
                        OtpConfirmRegistrationScreen(
                            viewModel = koinViewModel {
                                parametersOf(OtpConfirmRegistrationNavArgs(email = screen.email))
                            },
                            onBack = { backStack.removeLastOrNull() },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    entry<Screen.OtpReauthenticate> { screen ->
                        OtpReauthenticateScreen(
                            viewModel = koinViewModel {
                                parametersOf(
                                    OtpReauthenticateNavArgs(
                                        screen.password,
                                        screen.confirmPassword
                                    )
                                )
                            },
                            onBack = { message ->
                                backStack.removeLastOrNull()
                                if (message != null) {
                                    backStack.removeLastOrNull()
                                    scope.launch { snackbarHostState.showSnackbar(message) }
                                }
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    entry<Screen.Chat> {
                        ChatScreen(
                            viewModel = chatViewModel,
                            onBack = { backStack.removeLastOrNull() },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    entry<Screen.Journeys> {
                        JourneysScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onJourneyClick = { id ->
                                backStack.add(Screen.JourneyDetails(id.toString()))
                            },
                        )
                    }

                    entry<Screen.JourneyDetails> {
                        JourneyDetailsScreen(
                            viewModel = koinViewModel {
                                parametersOf(
                                    it.journeyId
                                )
                            },
                            journeyIdInProgress = mapState.value.selectedJourney?.id?.toString(),
                            onBack = { backStack.removeLastOrNull() },
                            onJourneyStart = { journeyId ->
                                scope.launch {
                                    mapViewModel.startJourney(journeyId)
                                }
                            }
                        )
                    }

                    entry<Screen.Fragments> {
                        FragmentsScreen(
                            snackBarHostState = snackbarHostState,
                            onBack = { backStack.removeLastOrNull() },
                            viewModel = koinViewModel {
                                parametersOf(it.publicWaypointId)
                            }
                        )
                    }
                }

            )
            val scope = rememberCoroutineScope()
            var initialElementHeight by remember { mutableStateOf(56.dp) }
            val density = LocalDensity.current
            val draggableState = rememberDraggableFlingStateWithDynamicSize(
                padding = 16.dp,
                initialElementHeight = initialElementHeight
            )
            val animatedBearing =
                rememberSmoothBearing(mapState.value.currentCameraPositionState.position.bearing)

            VerticalFloatingToolbar(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .onSizeChanged { size ->
                        val newHeight = with(density) {
                            size.width.toDp()
                        }
                        if (newHeight != initialElementHeight) {
                            initialElementHeight = newHeight
                        }
                    }
                    .offset {
                        IntOffset(
                            draggableState.offsetX.value.roundToInt(),
                            draggableState.offsetY.value.roundToInt()
                        )
                    }
                    .draggableWithDynamicFling(draggableState, scope)
                    .polarisDropShadow(),
                expanded = state.value.isControlPanelExpanded,
                leadingContent = {
                    if (conversationAIState.value.isRecording) {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                conversationAIViewModel.onAction(
                                    ConversationalAIAction.OnMuteStateChanged(
                                        !conversationAIState.value.isMuted
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (conversationAIState.value.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                                contentDescription = if (conversationAIState.value.isMuted) "Unmute" else "Mute"
                            )
                        }
                    }

                    if (backStack.last() is Screen.Map && conversationAIState.value.isRecording) {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                if (!state.value.hasCameraPermission) {
                                    viewModel.onAction(AppAction.RequestCameraPermission)
                                } else {
                                    backStack.add(Screen.AICamera)
                                }
                            }
                        ) {
                            Icon(
                                //insert nav images here


                                imageVector = FilterFocus,
                                contentDescription = "AI Camera"

                            )
                        }
                    }

                    if (backStack.last() !is Screen.Map) {
                        Column {
                            ConversationalAI(
                                viewModel = conversationAIViewModel,
                                snackbarHostState = snackbarHostState
                            )
                            if (backStack.last() !is Screen.AICamera) {
                                IconButton(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    onClick = {
                                        if (!state.value.hasCameraPermission) {
                                            viewModel.onAction(AppAction.RequestCameraPermission)
                                        } else {
                                            backStack.add(Screen.AICamera)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = FilterFocus,
                                        contentDescription = "AI Camera"
                                    )
                                }
                            }
                        }
                    } else {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                mapViewModel.onAction(MapActions.OnCompassClicked)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .rotate(animatedBearing)
                                    .size(32.dp),
                                painter = painterResource(id = R.drawable.icon_star),

                                contentDescription = "Explore"
                            )
                        }

                        IconButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                mapViewModel.onAction(MapActions.OnTrackingUserChanged(!mapState.value.isTrackingUser))
                            }
                        ) {
                            Icon(
                                imageVector = if (mapState.value.isTrackingUser) Icons.Default.MyLocation else Icons.Default.LocationSearching,
                                contentDescription = if (mapState.value.isTrackingUser) "Stop tracking" else "Start tracking"
                            )
                        }

                        if (backStack.last() is Screen.Map && mapState.value.selectedJourney == null) {
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    mapViewModel.onAction(MapActions.OnToggleShowStartJourneyDialog)
                                }
                            ) {
                                Icon(
                                    imageVector = if (mapState.value.shouldShowStartJourneyDialog) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                                    contentDescription = if (mapState.value.shouldShowStartJourneyDialog) "Hide start journey dialog" else "Show start journey dialog"
                                )
                            }
                        }

                        IconButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                mapViewModel.onAction(MapActions.OnToggleQuests)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = if (mapState.value.isQuestsVisible) {

                                    painterResource(id = R.drawable.icon_scroll)

                                } else {
                                    painterResource(id = R.drawable.icon_danger)
                                },
                                contentDescription = if (mapState.value.isQuestsVisible) "Hide quests" else "Show quests"
                            )
                        }


                        IconButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                if (!state.value.hasCameraPermission) {
                                    viewModel.onAction(AppAction.RequestCameraPermission)
                                } else {
                                    try {
                                        tempImageFile = createImageFile()
                                        tempImageUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            tempImageFile!!
                                        )
                                        cameraLauncher.launch(tempImageUri!!)
                                    } catch (e: Exception) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(e.message ?: "Error")
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(

                                painter = painterResource(id = R.drawable.icon_apature),
                                modifier = Modifier.size(24.dp),
                                contentDescription = "Take a picture"
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
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            viewModel.onAction(AppAction.OnControlPanelExpandedChanged)
                        }
                    ) {
                        if (state.value.isControlPanelExpanded) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Expand toolbar"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Collapse toolbar"
                            )
                        }
                    }
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        memoryViewModel.event.collect { event ->
            when (event) {
                is MemoryEvent.OnError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }

                MemoryEvent.OnSuccess -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Memory saved successfully")
                    }
                }
            }
        }
    }


    if (memoryState.value.showBottomSheet) {
        MemoryBottomSheet(
            imageUri = memoryState.value.capturedImageUri,
            isSaving = memoryState.value.isSaving,
            onSave = {
                memoryViewModel.onAction(
                    MemoryAction.SaveMemory(
                        mapState.value.currentLocation.latitude,
                        mapState.value.currentLocation.longitude,
                        mapState.value.selectedJourney?.id?.toString()
                    )
                )
            },
            onDismiss = { memoryViewModel.onAction(MemoryAction.DismissBottomSheet) }
        )
    }
    StatusBarProtection(
        color = MaterialTheme.colorScheme.surfaceContainer,
    )
}

@Composable
private fun StatusBarProtection(
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    heightProvider: () -> Float = calculateGradientHeight(),
) {

    Canvas(Modifier.fillMaxSize()) {
        val calculatedHeight = heightProvider()
        val gradient = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 1f), color.copy(alpha = .8f), Color.Transparent
            ), startY = 0f, endY = calculatedHeight
        )
        drawRect(
            brush = gradient,
            size = Size(size.width, calculatedHeight),
        )
    }
}

@Composable
fun calculateGradientHeight(): () -> Float {
    val statusBars = WindowInsets.statusBars
    val density = LocalDensity.current
    return { statusBars.getTop(density).times(1.2f) }
}

@Composable
fun rememberSmoothBearing(targetBearing: Float): Float {
    val animated by animateFloatAsState(
        targetValue = targetBearing - 45.0f,
        animationSpec = tween(durationMillis = 10, easing = FastOutSlowInEasing)
    )
    return animated
}


