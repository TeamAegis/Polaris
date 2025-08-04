package appcup.uom.polaris.features.conversational_ai.presentation

sealed class ConversationalAIEvent {
    data class Error(val message: String) : ConversationalAIEvent()
    object RecordAudioPermissionDenied : ConversationalAIEvent()
    object RecordAudioPermissionDeniedPermanently : ConversationalAIEvent()
    object OnStopRecording : ConversationalAIEvent()
}