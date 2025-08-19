@file:OptIn(ExperimentalUuidApi::class)

package appcup.uom.polaris.features.polaris.presentation.journeys

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisInputField
import appcup.uom.polaris.features.polaris.domain.JourneyStatus
import appcup.uom.polaris.features.polaris.presentation.journeys.components.EmptyJourneyList
import appcup.uom.polaris.features.polaris.presentation.journeys.components.JourneyCard
import appcup.uom.polaris.features.polaris.presentation.journeys.components.NotFoundJourneyList
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun JourneysScreen(
    onBack: () -> Unit,
    onJourneyClick: (Uuid) -> Unit,
    viewModel: JourneysViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    JourneysScreenImpl(
        state = state.value,
        onAction = { action ->
            when (action) {
                JourneysAction.OnBackClicked -> {
                    onBack()
                }

                is JourneysAction.OnJourneyClicked -> {
                    onJourneyClick(action.journeyId)
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}

@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class
)
@Composable
fun JourneysScreenImpl(
    state: JourneysState,
    onAction: (JourneysAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .imePadding(),
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                PolarisInputField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = "Search journeys",
                    value = state.searchQuery,
                    onValueChange = { onAction(JourneysAction.OnSearchQueryChanged(it)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    )
                )

                Spacer(Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                    )
                ) {
                    item {
                        val selected = state.selectedStatus == null
                        FilterChip(
                            selected = selected,
                            leadingIcon = {
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = { onAction(JourneysAction.OnStatusSelected(null)) },
                            label = { Text("All") }
                        )
                    }

                    items(JourneyStatus.entries.size) { index ->
                        val status = JourneyStatus.entries[index]
                        val selected =
                            state.selectedStatus != null && state.selectedStatus == status
                        FilterChip(
                            leadingIcon = {
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            selected = selected,
                            onClick = { onAction(JourneysAction.OnStatusSelected(status)) },
                            label = { Text(status.label) }
                        )
                    }
                }
                if (state.isLoading)
                    LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (!state.filteredJourneys.isEmpty() && !state.isLoading) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = 128.dp
                    )
                ) {
                    items(
                        count = state.filteredJourneys.size,
                        key = { index -> state.filteredJourneys[index].id!! }) { index ->
                        JourneyCard(state.filteredJourneys[index]) {
                            onAction(JourneysAction.OnJourneyClicked(state.filteredJourneys[index].id!!))
                        }
                    }
                }

            }
            if (state.filteredJourneys.isEmpty() && !state.isLoading) {
                if (state.searchQuery.isBlank() && state.selectedStatus == null)
                    EmptyJourneyList()
                else
                    NotFoundJourneyList()
            }
        }
    }
}
