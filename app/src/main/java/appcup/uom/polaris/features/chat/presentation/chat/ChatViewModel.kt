package appcup.uom.polaris.features.chat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.chat.domain.ChatRepository
import appcup.uom.polaris.features.chat.domain.Message
import appcup.uom.polaris.features.chat.domain.Role
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ChatEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            chatRepository.initialize()

            chatRepository.getChatHistory().collect { messages ->
                _state.update {
                    it.copy(messages = messages, isLoading = false)
                }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnMessageChanged -> {
                _state.update {
                    it.copy(message = action.message)
                }
            }

            is ChatAction.OnSendMessageClicked -> {
                if (action.message.isNotBlank()) {
                    _state.update {
                        it.copy(
                            messages = it.messages + Message(
                                Role.USER, it.message, StaticData.user.id,
                                Uuid.random()
                            ), isTyping = true, message = ""
                        )
                    }
                }
                viewModelScope.launch {
                    val result = chatRepository.sendMessage(action.message)
                    when (result) {
                        is Result.Error<DataError.Local> -> {
                            _state.update {
                                it.copy(isTyping = false)
                            }
                            _event.emit(ChatEvent.Error(result.error.message))
                        }

                        is Result.Success<*> -> {
                            _state.update {
                                it.copy(isTyping = false)
                            }
                        }
                    }
                }
            }

            ChatAction.OnClearChatHistoryClicked -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(messages = emptyList())
                    }
                    val result = chatRepository.clearChatHistory()
                    when (result) {
                        is Result.Error<DataError.Local> -> {
                            _event.emit(ChatEvent.Error(result.error.message))
                        }

                        is Result.Success<*> -> {
                            _state.update {
                                it.copy(messages = emptyList())
                            }
                        }
                    }
                }
            }

            is ChatAction.OnClearChatHistoryDialogVisibilityChanged -> {
                if (_state.value.messages.isEmpty() && action.isVisible) return
                _state.update {
                    it.copy(isClearChatHistoryDialogVisible = action.isVisible)
                }
            }

            else -> {}
        }

    }
}