package appcup.uom.polaris.core.presentation.memories.memory

import android.net.Uri

sealed class MemoryAction {
    data class OnImageCaptured(val uri: Uri) : MemoryAction()
    data class SaveMemory(val latitude: Double, val longitude: Double, val currentJourneyId: String?) : MemoryAction()
    object DismissBottomSheet : MemoryAction()
}