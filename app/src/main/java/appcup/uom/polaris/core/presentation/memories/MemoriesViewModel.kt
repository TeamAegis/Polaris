package appcup.uom.polaris.core.presentation.memories

import androidx.lifecycle.ViewModel
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MemoriesViewModel(userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(MemoriesState())
    val state = _state.asStateFlow()

    fun onAction(action: MemoriesActions) {

    }
}