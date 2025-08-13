package appcup.uom.polaris.core.presentation.quest

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import appcup.uom.polaris.core.presentation.map.MapActions
import appcup.uom.polaris.core.presentation.map.MapState
import appcup.uom.polaris.core.presentation.map.MapViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuestScreen(
    viewModel: MapViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
){

}


fun QuestImp(
    state: MapState,
    onAction: (MapActions) -> Unit
) {

}