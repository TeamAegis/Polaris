package appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai

sealed class ConversationalAIAction {
    object StartRecording : ConversationalAIAction()
    object StopRecording : ConversationalAIAction()
    object OnBotReady : ConversationalAIAction()
    object OnBotStartedSpeaking : ConversationalAIAction()
    object OnBotStoppedSpeaking : ConversationalAIAction()
    object OnUserStartedSpeaking : ConversationalAIAction()
    object OnUserStoppedSpeaking : ConversationalAIAction()
    data class OnBotAudioLevel(val level: Float) : ConversationalAIAction()
    data class OnUserAudioLevel(val level: Float) : ConversationalAIAction()
    data class OnConnectionStateChanged(val state: ConversationalAIConnectionState) : ConversationalAIAction()
}