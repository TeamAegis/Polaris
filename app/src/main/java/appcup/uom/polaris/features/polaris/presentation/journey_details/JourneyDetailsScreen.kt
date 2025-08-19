@file:OptIn(ExperimentalTime::class)

package appcup.uom.polaris.features.polaris.presentation.journey_details


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisTopAppBar
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.polaris.presentation.journey_details.components.JourneyDetailsCard
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.time.ExperimentalTime

@Composable
fun JourneyDetailsScreen(
    viewModel: JourneyDetailsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    JourneyDetailsScreenImpl(
        state = state.value,
        onAction = { action ->
            when (action) {
                JourneyDetailsAction.OnBackClicked -> {
                    onBack()
                }
                is JourneyDetailsAction.OnDeleteClicked -> {
                    viewModel.onAction(JourneyDetailsAction.OnDeleteClicked {
                        onBack()
                    })
                }
                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )

}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JourneyDetailsScreenImpl(
    state: JourneyDetailsState,
    onAction: (JourneyDetailsAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .imePadding()
            .statusBarsPadding()
            .fillMaxSize(),
        topBar = {
            Column {
                PolarisTopAppBar(
                    title = "Journey Details",
                    navigationIcon = {
                        PolarisIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            onAction(JourneyDetailsAction.OnBackClicked)
                        }
                    }
                ) { }
                if (state.isLoading) {
                    LinearWavyProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        },
    ) { contentPadding ->
        if (!state.isLoading) LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            //1. Journey Details Card
            item {
                state.journey?.let { journey ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .polarisDropShadow(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        JourneyDetailsCard(journey = journey)
                    }
                }
            }

//            //2. Journey Name
//            item {
//                state.journey?.let { journey ->
//                    Text(
//                        text = journey.name,
//                        style = MaterialTheme.typography.headlineSmall,
//                        color = MaterialTheme.colorScheme.onSurface,
//                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
//                    )
//                }
//            }
//
//
//            //3. Journey Description
//            item {
//                state.journey?.let { journey ->
//                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
//                        Text(
//                            text = journey.description,
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//            }


            //2. Journey Name and Description
            item {
                state.journey?.let { journey ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .polarisDropShadow(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = journey.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = journey.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            //3. Waypoints
            if (state.waypoints.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .polarisDropShadow(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Waypoints",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
//                            Spacer(modifier = Modifier.height(8.dp))

                            state.waypoints.forEach { waypoint ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = waypoint.name ?: "Unnamed Waypoint",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            //4. Memories
            if (state.memories.isNotEmpty()) {
                item {
                    Text(
                        text = "Memories",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.height(400.dp)
                    ) {
                        items(state.memories.size) { index ->
                            val memory = state.memories[index]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .polarisDropShadow(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                AsyncImage(
                                    model = File(memory.path),
                                    contentDescription = "Memory photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

