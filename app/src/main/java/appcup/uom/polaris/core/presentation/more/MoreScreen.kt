package appcup.uom.polaris.core.presentation.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = koinViewModel(),
    navigateToSettings: () -> Unit,
    navigateToChat: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MoreScreenImpl(
        state = state,
        onAction = { action ->
            when (action) {
                MoreActions.OnSettingsClicked -> {
                    navigateToSettings()
                }
                MoreActions.OnChatClicked -> {
                    navigateToChat()
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreenImpl(
    state: MoreState,
    onAction: (MoreActions) -> Unit
) {
    Scaffold(
        topBar = {},
        modifier = Modifier
            .fillMaxSize()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // User Profile Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    text = StaticData.user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = StaticData.user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Navigation
            Button(
                onClick = { onAction(MoreActions.OnSettingsClicked) },
                modifier = Modifier.fillMaxWidth()
                    .polarisDropShadow()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Settings") }

            // chat navigation
            Button(
                onClick = { onAction(MoreActions.OnChatClicked) },
                modifier = Modifier.fillMaxWidth()
                    .polarisDropShadow()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Chat") }
        }
    }

    LoadingOverlay(isLoading = state.isLoading)
}
