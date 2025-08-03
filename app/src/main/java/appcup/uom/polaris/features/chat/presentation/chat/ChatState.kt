package appcup.uom.polaris.features.chat.presentation.chat

import appcup.uom.polaris.features.chat.domain.Message

data class ChatState(
    val message: String = "",
    val messages: List<Message> = emptyList(),
    val isClearChatHistoryDialogVisible: Boolean = false,
    val isTyping: Boolean = false,
    val isLoading: Boolean = true,
)
