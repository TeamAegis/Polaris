package appcup.uom.polaris.core.presentation.memories.memory

sealed class MemoryEvent {
    data class OnError(val message: String) : MemoryEvent()
    object OnSuccess : MemoryEvent()
}