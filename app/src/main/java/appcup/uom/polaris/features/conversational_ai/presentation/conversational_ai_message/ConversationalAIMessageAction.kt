package appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message

sealed interface ConversationalAIMessageAction {
    data class OnMessageChanged(val message: String) : ConversationalAIMessageAction
    object SendMessage : ConversationalAIMessageAction
    object OnReset : ConversationalAIMessageAction
}