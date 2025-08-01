package appcup.uom.polaris.core.presentation.map

import androidx.lifecycle.ViewModel
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel(userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    fun onAction(action: MapActions) {

    }
}