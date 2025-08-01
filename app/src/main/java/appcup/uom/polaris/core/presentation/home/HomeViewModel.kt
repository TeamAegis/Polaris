package appcup.uom.polaris.core.presentation.home

import androidx.lifecycle.ViewModel
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun onAction(action: HomeActions) {

    }
}