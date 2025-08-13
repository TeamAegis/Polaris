package appcup.uom.polaris.core.presentation.memories

import appcup.uom.polaris.Memory

data class MemoriesState(
    val memories: List<Memory> = emptyList(),
    val isLoading: Boolean = false
)