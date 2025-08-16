package appcup.uom.polaris.features.polaris.presentation.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.polaris.domain.Fragment
import appcup.uom.polaris.features.polaris.domain.FragmentsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class FragmentsViewModel(
    private val publicWaypointId: String,
    private val fragmentsRepository: FragmentsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FragmentsState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<FragmentsEvent>()
    val event = _event.asSharedFlow()

    var job: Job? = null

    init {
        job = viewModelScope.launch {
            fragmentsRepository.getFragments(Uuid.parse(publicWaypointId)).collect { fragments ->
                _state.update { it.copy(fragments = fragments.reversed()) }
            }
        }
    }

    fun onAction(action: FragmentsAction) {
        when (action) {
            FragmentsAction.ShowAddFragmentBottomSheet -> {
                _state.update { it.copy(showAddFragmentBottomSheet = true) }
            }

            FragmentsAction.DismissAddFragmentBottomSheet -> {
                _state.update {
                    it.copy(
                        showAddFragmentBottomSheet = false,
                        newFragmentMessage = "",
                        newFragmentImageUri = null,
                        editingFragment = null
                    )
                }
            }

            is FragmentsAction.OnMessageChanged -> {
                _state.update { it.copy(newFragmentMessage = action.message) }
            }

            is FragmentsAction.OnImageCaptured -> {
                _state.update {
                    it.copy(
                        newFragmentImageUri = action.imageUri,
                        showAddFragmentBottomSheet = true
                    )
                }
            }

            FragmentsAction.OnImageRemoved -> {
                _state.update { it.copy(newFragmentImageUri = null) }
            }

            FragmentsAction.SaveFragment -> {
                saveFragment()
            }

            else -> {}
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun saveFragment() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState.newFragmentMessage.isBlank() && currentState.newFragmentImageUri == null) {
                _event.emit(FragmentsEvent.OnError("Please add a message or photo"))
                return@launch
            }

            _state.update { it.copy(isSaving = true) }

            try {
                val imageUrl = currentState.newFragmentImageUri?.let { uri ->
                    val result = fragmentsRepository.uploadImageFragment(uri)
                    when (result) {
                        is Result.Error<DataError.FragmentError> -> {
                            _event.emit(FragmentsEvent.OnError(result.error.message))
                            null
                        }

                        is Result.Success<String> -> {
                            result.data
                        }
                    }
                }

                fragmentsRepository.addFragment(
                    Fragment(
                        publicWaypointId = Uuid.parse(publicWaypointId),
                        userId = StaticData.user.id,
                        fragmentUrl = imageUrl,
                        message = currentState.newFragmentMessage.takeIf { it.isNotBlank() },
                        createdAt = Clock.System.now()
                    )
                )

                _state.update {
                    it.copy(
                        isSaving = false,
                        showAddFragmentBottomSheet = false,
                        newFragmentMessage = "",
                        newFragmentImageUri = null,
                        editingFragment = null
                    )
                }

                _event.emit(FragmentsEvent.OnFragmentSaved)

            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                _event.emit(FragmentsEvent.OnError(e.message ?: "Failed to save fragment"))
            }
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }

}