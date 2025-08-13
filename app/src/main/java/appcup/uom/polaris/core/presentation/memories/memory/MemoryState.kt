package appcup.uom.polaris.core.presentation.memories.memory

import android.net.Uri

data class MemoryState(
    val isLoading: Boolean = false,
    val capturedImageUri: Uri? = null,
    val showBottomSheet: Boolean = false,
    val isSaving: Boolean = false,
)