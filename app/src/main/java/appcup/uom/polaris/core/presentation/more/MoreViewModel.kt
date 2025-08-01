package appcup.uom.polaris.core.presentation.more

import androidx.lifecycle.ViewModel
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MoreViewModel(userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    fun onAction(action: MoreActions) {
        when(action) {
            else -> {}
        }
    }
}