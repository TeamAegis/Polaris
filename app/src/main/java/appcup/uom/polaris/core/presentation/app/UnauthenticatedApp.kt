package appcup.uom.polaris.core.presentation.app

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import appcup.uom.polaris.features.auth.presentation.forgot_password.ForgotPasswordScreen
import appcup.uom.polaris.features.auth.presentation.login.LoginScreen
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationNavArgs
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationScreen
import appcup.uom.polaris.features.auth.presentation.register.RegisterScreen
import appcup.uom.polaris.features.auth.presentation.reset_password.ResetPasswordScreen
import appcup.uom.polaris.features.auth.presentation.start.StartScreen
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallHandler
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalUuidApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UnauthenticatedApp(
    viewModel: AppViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(Screen.Start)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AppEvent.LocationPermissionDenied -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Location permission is required to use the app.")
                    }
                }

                AppEvent.LocationPermissionDeniedPermanent -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Location permission is required. Please enable it from settings.")
                    }
                }

                else -> {}
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

    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
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
                            navigateToLogin = {
                                if (state.hasLocationPermission) {
                                    backStack.add(Screen.Login)
                                } else {
                                    viewModel.onAction(AppAction.RequestLocationPermission)
                                }

                            },
                            navigateToRegister = {
                                if (state.hasLocationPermission) {
                                    backStack.add(Screen.Register)
                                } else {
                                    viewModel.onAction(AppAction.RequestLocationPermission)
                                }
                            }
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


                    entry<Screen.Register> {
                        RegisterScreen(
                            onBack = { backStack.removeLastOrNull() },
                            navigateToOtpConfirmRegistration = { email ->
                                backStack.add(Screen.OtpConfirmRegistration(email))
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

                    entry<Screen.OtpConfirmRegistration> { screen ->
                        OtpConfirmRegistrationScreen(
                            viewModel = koinViewModel {
                                parametersOf(OtpConfirmRegistrationNavArgs(email = screen.email))
                            },
                            onBack = { backStack.removeLastOrNull() },
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            )
        }
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

