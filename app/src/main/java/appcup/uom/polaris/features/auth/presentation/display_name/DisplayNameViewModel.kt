package appcup.uom.polaris.features.auth.presentation.display_name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.auth.domain.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisplayNameViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(DisplayNameState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<DisplayNameEvent>()
    val event = _event.asSharedFlow()

    init {
        _state.update {
            it.copy(currentName = StaticData.user.name)
        }
    }

    fun onAction(action: DisplayNameAction) {
        when(action) {
            is DisplayNameAction.OnDisplayNameChanged -> {
                _state.update {
                    it.copy(name = action.name)
                }
            }
            DisplayNameAction.OnSaveClicked -> {
                _state.update {
                    it.copy(isLoading = true)
                }

                viewModelScope.launch {
                    val res = userRepository.updateDisplayName(_state.value.name)
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    when(res) {
                        is Result.Error<DataError.Local> -> {
                            _event.emit(DisplayNameEvent.Error(res.error.message))
                        }
                        is Result.Success<Unit> -> {
                            _event.emit(DisplayNameEvent.DisplayNameSuccessfullyChanged)
                            _state.update {
                                it.copy(currentName = _state.value.name)
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}