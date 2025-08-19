package appcup.uom.polaris.core.presentation.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.ui.graphics.vector.ImageVector
import appcup.uom.polaris.core.presentation.components.FilterFocus
import appcup.uom.polaris.core.presentation.components.Robot

data class AppState(
    val isFabMenuExpanded: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val hasLocationPermission: Boolean = false,

    val isControlPanelExpanded: Boolean = true
)

enum class FabMenuItem(val imageVector: ImageVector, val label: String) {
    LiveTranslate(FilterFocus, "Live Translate"),
    VoiceAssistant(Robot, "Voice Assistant"),
    CreateFragment(Icons.Default.Public, "Create Fragment"),
}