package appcup.uom.polaris.core.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    HomeScreenImpl(
        state = state,
        onAction = { action ->

        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenImpl(
    state: HomeState,
    onAction: (HomeActions) -> Unit
) {
    Scaffold(
        topBar = {},
        modifier = Modifier
            .fillMaxSize()
    ) { contentPadding ->
        Text("Home Screen", modifier = Modifier.padding(contentPadding))
    }

    LoadingOverlay(isLoading = state.isLoading)
}