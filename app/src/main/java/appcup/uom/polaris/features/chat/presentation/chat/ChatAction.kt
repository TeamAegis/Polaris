package appcup.uom.polaris.features.chat.presentation.chat

sealed interface ChatAction {
    object OnBackClicked : ChatAction
    data class OnMessageChanged(val message: String) : ChatAction
    data class OnSendMessageClicked(val message: String) : ChatAction
    object OnClearChatHistoryClicked : ChatAction
    data class OnClearChatHistoryDialogVisibilityChanged(val isVisible: Boolean) : ChatAction
}