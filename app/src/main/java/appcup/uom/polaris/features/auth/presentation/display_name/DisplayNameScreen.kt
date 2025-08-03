package appcup.uom.polaris.features.auth.presentation.display_name

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisLargeTopAppBar
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DisplayNameScreen(
    viewModel: DisplayNameViewModel = koinViewModel(),
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                DisplayNameEvent.DisplayNameSuccessfullyChanged -> {
                    snackbarHostState.showSnackbar("Display Name Changed")
                }

                is DisplayNameEvent.Error -> {
                    snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    DisplayNameScreenImpl(
        state = state.value,
        onAction = { action ->
            when (action) {
                DisplayNameAction.OnBackClicked -> {
                    onBack()
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
fun DisplayNameScreenImpl(
    state: DisplayNameState,
    onAction: (DisplayNameAction) -> Unit
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
                title = "Change Display Name",
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
                        onAction(DisplayNameAction.OnBackClicked)
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 0.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your current display name is:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = state.currentName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    onAction(DisplayNameAction.OnDisplayNameChanged(name = it))
                }, modifier = Modifier
                    .padding(16.dp, 32.dp, 16.dp, 0.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
                },
                label = {
                    Text(text = "Display Name")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onAction(DisplayNameAction.OnSaveClicked)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 48.dp)
                    .polarisDropShadow()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.isLoading) {
                    Text(text = "Saving...")
                    Spacer(modifier = Modifier.width(16.dp))
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = "Save")
                }

            }
        }
    }


    LoadingOverlay(isLoading = state.isLoading)
}