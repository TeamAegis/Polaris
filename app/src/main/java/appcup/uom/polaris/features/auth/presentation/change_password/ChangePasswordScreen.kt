package appcup.uom.polaris.features.auth.presentation.change_password

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.LoadingOverlay
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisLargeTopAppBar
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel = koinViewModel(),
    onBack: (String?) -> Unit,
    navigateToReauthenticate: (String, String, String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ChangePasswordEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                ChangePasswordEvent.PasswordSuccessfullyChanged -> {
                    onBack("Password Successfully Changed")
                }

                ChangePasswordEvent.ReauthenticationRequired -> {
                    navigateToReauthenticate(
                        "Please reauthenticate using the OTP sent to your email.",
                        state.value.password,
                        state.value.confirmPassword
                    )
                }
            }
        }
    }

    ChangePasswordScreenImpl(
        state = state.value,
        onAction = { action ->
            when (action) {
                ChangePasswordAction.OnBackClicked -> {
                    onBack(null)
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreenImpl(
    state: ChangePasswordState,
    onAction: (ChangePasswordAction) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        flingAnimationSpec = rememberSplineBasedDecay(),
        state = rememberTopAppBarState()
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PolarisLargeTopAppBar(
                title = "Change Password",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    PolarisIconButton(
                        icon = {
                            Icon(
                                tint = MaterialTheme.colorScheme.primary,
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    ) {
                        onAction(ChangePasswordAction.OnBackClicked)
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    onAction(ChangePasswordAction.OnPasswordChanged(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 32.dp, 16.dp, 0.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password, contentDescription = null)
                },
                singleLine = true,
                label = {
                    Text(text = "Password")
                },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (state.isPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description =
                        if (state.isPasswordVisible) "Hide password" else "Show password"
                    IconButton(onClick = {
                        onAction(ChangePasswordAction.OnPasswordVisibilityChanged(!state.isPasswordVisible))
                    }) {
                        Icon(imageVector = image, description)
                    }
                }
            )

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = {
                    onAction(ChangePasswordAction.OnConfirmPasswordChanged(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 0.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password, contentDescription = null)
                },
                singleLine = true,
                label = {
                    Text(text = "Confirm Password")
                },
                visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (state.isConfirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description =
                        if (state.isConfirmPasswordVisible) "Hide password" else "Show password"
                    IconButton(onClick = {
                        onAction(ChangePasswordAction.OnConfirmPasswordVisibilityChanged(!state.isConfirmPasswordVisible))
                    }) {
                        Icon(imageVector = image, description)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onAction(ChangePasswordAction.OnChangePasswordClicked)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 48.dp)
                    .polarisDropShadow()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
            ) {
                if (state.isLoading) {
                    Text(text = "Changing Password...")
                    Spacer(modifier = Modifier.width(16.dp))
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = "Change Password")
                }

            }

        }
    }

    LoadingOverlay(state.isLoading)
}