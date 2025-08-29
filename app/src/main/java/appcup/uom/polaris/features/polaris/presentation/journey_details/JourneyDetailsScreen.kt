@file:OptIn(ExperimentalTime::class)

package appcup.uom.polaris.features.polaris.presentation.journey_details


import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisTopAppBar
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.polaris.domain.JourneyStatus
import appcup.uom.polaris.features.polaris.presentation.journey_details.components.JourneyDetailsCard
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.time.ExperimentalTime

@Composable
fun JourneyDetailsScreen(
    viewModel: JourneyDetailsViewModel = koinViewModel(),
    journeyIdInProgress: String?,
    onBack: () -> Unit,
    onJourneyStart: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    JourneyDetailsScreenImpl(
        state = state.value,
        journeyIdInProgress = journeyIdInProgress,
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

                JourneyDetailsAction.OnStartClicked -> {
                    onJourneyStart(state.value.journeyId)
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JourneyDetailsScreenImpl(
    state: JourneyDetailsState,
    journeyIdInProgress: String? = null,
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { contentPadding ->
        if (!state.isLoading) {
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Journey Cover Card
                item {
                    state.journey?.let { journey ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            JourneyDetailsCard(journey = journey)
                        }
                    }
                }

                // Journey Name + Description
                item {
                    state.journey?.let { journey ->
                        Text(
                            text = journey.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                        )
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.elevatedCardColors().copy(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExpandableDescription(journey.description)
                            }
                        }
                    }
                }

                if (state.journeyId != journeyIdInProgress && state.journey?.status != JourneyStatus.COMPLETED)
                    item {
                        Button(
                            onClick = {
                                onAction(JourneyDetailsAction.OnStartClicked)
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .polarisDropShadow()
                                .clip(RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                            )
                            Text(text = "Start Journey")
                        }
                    }

                // Waypoints
                if (state.waypoints.isNotEmpty()) {
                    item { SectionHeader(title = "Waypoints") }
                    items(state.waypoints.size) { index ->
                        val waypoint = state.waypoints[index]
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors().copy(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = waypoint.name.takeIf { !it.isNullOrBlank() }
                                            ?: waypoint.address.takeIf { !it.isNullOrBlank() }
                                            ?: "Unknown",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    waypoint.address?.let {
                                        if (it.isNotBlank() && it != waypoint.name) {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Memories
                if (state.memories.isNotEmpty()) {
                    item { SectionHeader(title = "Memories") }
                    item {
                        val carouselState = rememberCarouselState { state.memories.size }
                        HorizontalMultiBrowseCarousel(
                            state = carouselState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(vertical = 16.dp),
                            preferredItemWidth = 220.dp,
                            itemSpacing = 12.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) { index ->
                            val memory = state.memories[index]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .polarisDropShadow(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                AsyncImage(
                                    model = File(memory.path),
                                    contentDescription = "Memory photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                if (journeyIdInProgress == null || state.journeyId != journeyIdInProgress)
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                onAction(JourneyDetailsAction.OnDeleteDialogVisibilityChanged(true))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Delete Journey")
                        }
                    }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    if (state.isDeleteDialogVisible) {
        AlertDialog(
            text = {
                Text(text = "Are you sure you want to delete this journey?")
            },
            onDismissRequest = {
                onAction(JourneyDetailsAction.OnDeleteDialogVisibilityChanged(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(
                            JourneyDetailsAction.OnDeleteClicked {
                                onAction(JourneyDetailsAction.OnBackClicked)
                            }
                        )
                    }
                ) {
                    Text(text = "Delete")
                }

            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(JourneyDetailsAction.OnDeleteDialogVisibilityChanged(false))
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "Delete Journey")
            },
        )
    }
}


// Expandable description composable
@Composable
fun ExpandableDescription(description: String) {
    var expanded by remember { mutableStateOf(false) }
    val maxLines = if (expanded) Int.MAX_VALUE else 3

    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .animateContentSize()
            .clickable { expanded = !expanded }
            .padding(top = 4.dp)
    )
}

// Reusable section header
@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}
