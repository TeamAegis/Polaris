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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.presentation.components.BottomBar
import appcup.uom.polaris.core.presentation.components.BottomBarVisibility
import appcup.uom.polaris.core.presentation.home.HomeScreen
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
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAI
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallHandler
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App(
    viewModel: AppViewModel = koinInject()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val backStack =
        rememberNavBackStack(if (state.value.isAuthenticated) Screen.Home else Screen.Start)

    LaunchedEffect(state.value.isAuthenticated) {
        if (state.value.isAuthenticated) {
            backStack.clear()
            backStack.add(Screen.Home)
        } else {
            backStack.clear()
            backStack.add(Screen.Start)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


//    DisposableEffect(Unit) {
//        ExternalUriHandler.listener = { uri ->
//            println(Constants.DEBUG_VALUE + uri)
//            println(Constants.DEBUG_VALUE + Constants.DEEPLINK_URI_AUTH)
////            deeplink
//            if (uri.startsWith(Constants.DEEPLINK_URI_AUTH)) {
//                Log.i(Constants.DEBUG_VALUE, "App: $uri")
//                backStack.add(Screen.ResetPassword)
//            }
//        }
//        onDispose {
//            ExternalUriHandler.listener = null
//        }
//    }


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

    Scaffold(
        modifier = Modifier.imePadding(),
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = isBottomBarVisible.value,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomBar(navBackStack = backStack)
                }
            }
        },
        floatingActionButton = {
            if (state.value.isAuthenticated) {
                ConversationalAI(
                    snackbarHostState = snackbarHostState
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators =
                listOf(
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
            entryProvider = { key ->
                when (key) {
                    Screen.Start -> NavEntry(key) {
                        StartScreen(
                            navigateToLogin = {
                                backStack.add(Screen.Login)
                            },
                            navigateToRegister = {
                                backStack.add(Screen.Register)
                            }
                        )
                    }

                    Screen.Login -> NavEntry(key) {
                        LoginScreen(
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            onForgotPassword = {
                                backStack.add(Screen.ForgotPassword)
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    Screen.ResetPassword -> NavEntry(key) {
                        ResetPasswordScreen(
                            onBack = { message ->
                                backStack.removeLastOrNull()

                                if (message != null) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    Screen.ChangeDisplayName -> NavEntry(key) {
                        DisplayNameScreen(
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    Screen.Register -> NavEntry(key) {
                        RegisterScreen(
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            navigateToOtpConfirmRegistration = { email ->
                                backStack.add(Screen.OtpConfirmRegistration(email = email))
                            },
                            snackbarHostState = snackbarHostState,
                        )
                    }

                    Screen.ChangePassword -> NavEntry(key) {
                        ChangePasswordScreen(
                            onBack = { message ->
                                backStack.removeLastOrNull()

                                if (message != null) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }
                            },
                            navigateToReauthenticate = { message, password, confirmPassword ->
                                backStack.add(
                                    Screen.OtpReauthenticate(
                                        password = password,
                                        confirmPassword = confirmPassword
                                    )
                                )

                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    Screen.ForgotPassword -> NavEntry(key) {
                        ForgotPasswordScreen(
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    Screen.Home -> NavEntry(key) {
                        HomeScreen(
                            snackbarHostState = snackbarHostState
                        )
                    }

                    Screen.More -> NavEntry(key) {
                        MoreScreen(
                            navigateToSettings = {
                                backStack.add(Screen.Settings)
                            },
                            snackbarHostState = snackbarHostState,
                        )
                    }

                    is Screen.OtpConfirmRegistration -> NavEntry(key) {
                        OtpConfirmRegistrationScreen(
                            viewModel = koinViewModel(
                                parameters = { parametersOf(OtpConfirmRegistrationNavArgs(email = key.email)) }
                            ),
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    is Screen.OtpReauthenticate -> NavEntry(key) {
                        OtpReauthenticateScreen(
                            viewModel = koinViewModel(
                                parameters = {
                                    parametersOf(
                                        OtpReauthenticateNavArgs(
                                            key.password,
                                            key.confirmPassword
                                        )
                                    )
                                }
                            ),
                            onBack = { message ->
                                backStack.removeLastOrNull()
                                if (message != null) {
                                    backStack.removeLastOrNull()
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }
                            },
                            snackbarHostState = snackbarHostState
                        )

                    }

                    Screen.Settings -> NavEntry(key) {
                        SettingsScreen(
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            navigateToChangeDisplayName = {
                                backStack.add(Screen.ChangeDisplayName)
                            },
                            navigateToChangePassword = {
                                backStack.add(Screen.ChangePassword)
                            },
                            snackbarHostState = snackbarHostState,
                        )
                    }

                    else -> throw RuntimeException("Unknown screen: $key")
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
                color.copy(alpha = 1f),
                color.copy(alpha = .8f),
                Color.Transparent
            ),
            startY = 0f,
            endY = calculatedHeight
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


