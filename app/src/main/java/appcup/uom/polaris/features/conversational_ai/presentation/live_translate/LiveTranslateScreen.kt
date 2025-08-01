package appcup.uom.polaris.features.conversational_ai.presentation.live_translate

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel

@Composable
fun LiveTranslateScreen(
    viewModel: ConversationalAIViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }



    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
        )
    }

}