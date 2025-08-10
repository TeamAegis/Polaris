package appcup.uom.polaris.core.presentation.app

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.extras.navigation.rebaseTo
import appcup.uom.polaris.core.presentation.components.BottomBar
import appcup.uom.polaris.core.presentation.components.BottomBarVisibility
import appcup.uom.polaris.core.presentation.map.MapScreen
import appcup.uom.polaris.core.presentation.memories.MemoriesScreen
import appcup.uom.polaris.core.presentation.more.MoreScreen
import appcup.uom.polaris.core.presentation.settings.SettingsScreen
import appcup.uom.polaris.features.auth.presentation.change_password.ChangePasswordScreen
import appcup.uom.polaris.features.auth.presentation.display_name.DisplayNameScreen
import appcup.uom.polaris.features.auth.presentation.forgot_password.ForgotPasswordScreen
import appcup.uom.polaris.features.auth.presentation.login.LoginScreen
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationNavArgs
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationScreen
import appcup.uom.polaris.features.auth.presentation.otp_reauthenticate.OtpReauthenticateNavArgs
import appcup.uom.polaris.features.auth.presentation.otp_reauthenticate.OtpReauthenticateScreen
import appcup.uom.polaris.features.auth.presentation.register.RegisterScreen
import appcup.uom.polaris.features.auth.presentation.reset_password.ResetPasswordScreen
import appcup.uom.polaris.features.auth.presentation.start.StartScreen
import appcup.uom.polaris.features.chat.presentation.chat.ChatScreen
import appcup.uom.polaris.features.chat.presentation.chat.ChatViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai.ConversationalAI
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai.ConversationalAIAction
import appcup.uom.polaris.features.conversational_ai.presentation.live_translate.LiveTranslateScreen
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallHandler
import appcup.uom.polaris.features.polaris.presentation.create_journey.CreateJourneyScreen
import appcup.uom.polaris.features.polaris.presentation.journeys.JourneysScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalUuidApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App(
    viewModel: AppViewModel = koinInject()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val backStack =
        rememberNavBackStack(if (state.value.isAuthenticated) Screen.Map else Screen.Start)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val chatViewModel: ChatViewModel? = if (state.value.isAuthenticated) {
        koinViewModel()
    } else {
        null
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AppEvent.Authenticated -> {
                    backStack.rebaseTo(listOf(Screen.Map))
                }

                AppEvent.Unauthenticated -> {
                    backStack.rebaseTo(listOf(Screen.Start))
                }

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

                AppEvent.CameraPermissionGranted -> {

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

                AppEvent.LocationPermissionGranted -> {

                }
            }
        }
    }


    val functionCallHandler = FunctionCallHandler(navBackStack = backStack)
    LaunchedEffect(Unit) {
        EventBus.collectEvents { event ->
            if (event is Event.OnFunctionCall) {
                functionCallHandler.handleFunctionCall(event.func, event.args, event.onResult)
            }
        }
    }


    val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
    backStack.BottomBarVisibility(isBottomBarVisible)

    val conversationAIViewModel: ConversationalAIViewModel = koinViewModel()
    val conversationAIState = conversationAIViewModel.state.collectAsStateWithLifecycle()




    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding(),
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = isBottomBarVisible.value,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomBar(
                        navBackStack = backStack,
                        state = state.value,
                        onLocationPermissionRequest = {
                            if (!state.value.hasLocationPermission) {
                                viewModel.onAction(AppAction.RequestLocationPermission)
                            }
                        })
                }
            }
        },
        floatingActionButton = {
            if (state.value.isAuthenticated) {
                if ((conversationAIState.value.isRecording || !isBottomBarVisible.value) && backStack.last() !is Screen.Chat) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.wrapContentSize().padding(
                            bottom = if (backStack.last() is Screen.Map) 72.dp else 0.dp
                        ),
                    ) {
                        ConversationalAI(
                            viewModel = conversationAIViewModel,
                            snackbarHostState = snackbarHostState
                        )

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
                    }
                } else if (isBottomBarVisible.value) {
                    val primaryColor = MaterialTheme.colorScheme.primary

                    FloatingActionButtonMenu(
                        modifier = Modifier
                            .absoluteOffset(x = 16.dp, y =  if (backStack.last() is Screen.Map) (-56).dp else 16.dp),
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
                                        FabMenuItem.LiveTranslate -> {
                                            if (!state.value.hasCameraPermission) {
                                                viewModel.onAction(AppAction.RequestCameraPermission)
                                            } else {
                                                backStack.add(Screen.LiveTranslate)
                                            }
                                        }

                                        FabMenuItem.VoiceAssistant -> {
                                            conversationAIViewModel.onAction(ConversationalAIAction.StartRecording)
                                        }

                                        FabMenuItem.Journeys -> {
                                            backStack.add(Screen.Journeys)
                                        }
                                    }
                                },
                                icon = { Icon(item.imageVector, contentDescription = null) },
                                text = { Text(text = item.label) },
                            )
                        }
                    }
                }

            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavDisplay(
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
                entry<Screen.Start> {
                    StartScreen(
                        navigateToLogin = { backStack.add(Screen.Login) },
                        navigateToRegister = { backStack.add(Screen.Register) }
                    )
                }

                entry<Screen.Login> {
                    LoginScreen(
                        onBack = { backStack.removeLastOrNull() },
                        onForgotPassword = { backStack.add(Screen.ForgotPassword) },
                        snackbarHostState = snackbarHostState
                    )
                }

                entry<Screen.ResetPassword> {
                    ResetPasswordScreen(
                        onBack = { message ->
                            backStack.removeLastOrNull()
                            if (message != null) {
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        },
                        snackbarHostState = snackbarHostState
                    )
                }

                entry<Screen.ChangeDisplayName> {
                    DisplayNameScreen(
                        onBack = { backStack.removeLastOrNull() },
                        snackbarHostState = snackbarHostState
                    )
                }

                entry<Screen.Register> {
                    RegisterScreen(
                        onBack = { backStack.removeLastOrNull() },
                        navigateToOtpConfirmRegistration = { email ->
                            backStack.add(Screen.OtpConfirmRegistration(email))
                        },
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
                        chatViewModel = chatViewModel!!,
                        snackbarHostState = snackbarHostState,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }

                entry<Screen.Map> {
                    MapScreen(snackbarHostState = snackbarHostState)
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

                entry<Screen.LiveTranslate> {
                    LiveTranslateScreen(
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
                        viewModel = chatViewModel!!,
                        onBack = { backStack.removeLastOrNull() },
                        snackbarHostState = snackbarHostState
                    )
                }

                entry<Screen.Journeys> {
                    JourneysScreen(
                        onBack = { backStack.removeLastOrNull() },
                        onJourneyClick = {

                        },
                    )
                }
            }

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


