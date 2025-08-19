package appcup.uom.polaris.features.arcore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.map.MapViewModel
import io.github.sceneview.ar.ARScene
import io.github.sceneview.node.Node

@Composable
fun ARCoreScreen(
    viewModel: MapViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val nodes = remember {
        mutableListOf<Node>()
    }

    ARScene(
        childNodes = nodes,
        planeRenderer = true
    ) {

    }
}