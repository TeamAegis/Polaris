package appcup.uom.polaris.features.polaris.presentation.journey_details


import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

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

@Composable
fun JourneyDetailsScreenImpl(
    state: JourneyDetailsState,
    onAction: (JourneyDetailsAction) -> Unit
) {
    Button(onClick = {
        onAction(JourneyDetailsAction.OnDeleteClicked())
    }) {
        Text("Delete")
    }

}
