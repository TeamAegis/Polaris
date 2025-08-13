package appcup.uom.polaris.core.presentation.memories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.Memory
import appcup.uom.polaris.core.presentation.components.PolarisTopAppBar
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@Composable
fun MemoriesScreen(
    viewModel: MemoriesViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MemoriesScreenImpl(
        state = state,
        onAction = { action ->

        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreenImpl(
    state: MemoriesState,
    onAction: (MemoriesActions) -> Unit
) {
    Scaffold(
        topBar = {
            PolarisTopAppBar(
                "Memories",
                navigationIcon = {}
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { contentPadding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                bottom = 128.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = state.memories.size,
                key = { index -> state.memories[index].id }) { index ->
                MemoryGridItem(state.memories[index], onClick = {})
            }
        }
    }

    LoadingOverlay(isLoading = state.isLoading)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryGridItem(
    memory: Memory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        AsyncImage(
            model = File(memory.path),
            contentDescription = "Memory photo",
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {

                    }
                )
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}