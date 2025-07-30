package appcup.uom.polaris.features.conversational_ai.presentation

data class ConversationalAIState(
    val isRecordAudioPermissionGranted: Boolean = false,
    val isRecording: Boolean = false,
    val isBotReady: Boolean = false,
    val isBotSpeaking: Boolean = false,
    val isUserSpeaking: Boolean = false,
    val botAudioLevel: Float = 0f,
    val userAudioLevel: Float = 0f,
    val connectionState: ConversationalAIConnectionState = ConversationalAIConnectionState.Idle
)

enum class ConversationalAIConnectionState {
    Connecting,
    Connected,
    Idle
}