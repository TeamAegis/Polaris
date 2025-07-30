package appcup.uom.polaris.core.presentation.more

import androidx.lifecycle.ViewModel
import appcup.uom.polaris.core.domain.ValidationEvent
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class MoreViewModel(userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    private val _validationEvent = MutableSharedFlow<ValidationEvent>()
    val validationEvent = _validationEvent.asSharedFlow()

    fun onAction(action: MoreActions) {
        when(action) {
            else -> {}
        }
    }
}