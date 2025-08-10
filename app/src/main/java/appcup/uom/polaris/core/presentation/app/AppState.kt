package appcup.uom.polaris.core.presentation.app

import androidx.compose.ui.graphics.vector.ImageVector
import appcup.uom.polaris.core.presentation.components.FilterFocus
import appcup.uom.polaris.core.presentation.components.Journals
import appcup.uom.polaris.core.presentation.components.Robot

data class AppState(
    val isAuthenticated: Boolean = false,
    val isFabMenuExpanded: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val hasLocationPermission: Boolean = false
)

enum class FabMenuItem(val imageVector: ImageVector, val label: String) {
    LiveTranslate(FilterFocus, "Live Translate"),
    VoiceAssistant(Robot, "Voice Assistant"),
    Journeys(Journals, "Journeys"),
}