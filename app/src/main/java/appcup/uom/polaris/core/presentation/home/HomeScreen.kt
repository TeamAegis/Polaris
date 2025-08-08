package appcup.uom.polaris.core.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.ChatBar
import appcup.uom.polaris.core.presentation.components.PolarisInputField
import appcup.uom.polaris.core.presentation.components.searchBar
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
    var query by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                searchBar(
                    query = query,
                    onQueryChange = {
                            newQuery -> query = newQuery
                    },
                    placeHolder = "Search..."
                )
                ChatBar(
                    query = query,
                    onQueryChange = {
                            newQuery -> query = newQuery
                    },
                    placeHolder = "Chatbot..."
                )
            }

        },
        modifier = Modifier
            .fillMaxSize()
    ) { contentPadding ->
        //Text("Home Screen1", modifier = Modifier.padding(contentPadding))
        Text("Search item: $query", modifier = Modifier.padding(contentPadding))
    }

    LoadingOverlay(isLoading = state.isLoading)
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun HomePreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                PolarisInputField(
                    value = "",
                    onValueChange = {

                    },
                    label = "Title"
                )
                PolarisInputField(
                    value = "",
                    onValueChange = {

                    },
                    label = "Title"
                )
                PolarisInputField(
                    value = "",
                    onValueChange = {

                    },
                    label = "Title"
                )
            }
        }
    }
}