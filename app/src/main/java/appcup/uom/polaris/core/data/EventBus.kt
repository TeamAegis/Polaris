package appcup.uom.polaris.core.data

import appcup.uom.polaris.core.domain.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

object EventBus {
    private val _events = MutableSharedFlow<Event>(replay = 0)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    suspend fun emit(event: Event) {
        _events.emit(event)
    }

    suspend fun collectEvents(action: suspend (Event) -> Unit) {
        events.collectLatest { event ->
            action(event)
        }
    }
}