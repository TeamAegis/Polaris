package appcup.uom.polaris.features.chat.presentation.chat

sealed class ChatEvent {
    data class Error(val message: String) : ChatEvent()
}