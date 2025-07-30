package appcup.uom.polaris.features.conversational_ai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.domain.ValidationEvent
import appcup.uom.polaris.features.conversational_ai.data.ConversationalAI
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.conversational_ai.utils.PermissionResultCallback
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationalAIViewModel(
    private val permissionBridge: PermissionBridge
): ViewModel() {
    private val _state = MutableStateFlow(ConversationalAIState())
    val state = _state.asStateFlow()

    private val _validationEvent = MutableSharedFlow<ValidationEvent>()
    val validationEvent = _validationEvent.asSharedFlow()

    private val conversationalAI = ConversationalAI()

    init {
        _state.update {
            it.copy(isRecordAudioPermissionGranted = permissionBridge.isRecordAudioPermissionGranted())
        }
    }

    fun onAction(action: ConversationalAIAction) {
        when (action) {
            is ConversationalAIAction.OnBotAudioLevel -> {
                _state.update {
                    it.copy(botAudioLevel = action.level)
                }
            }
            ConversationalAIAction.OnBotReady -> {
                _state.update {
                    it.copy(isBotReady = true)
                }
            }
            ConversationalAIAction.OnBotStartedSpeaking -> {
                _state.update {
                    it.copy(isBotSpeaking = true, isUserSpeaking = false)
                }
            }
            ConversationalAIAction.OnBotStoppedSpeaking -> {
                _state.update {
                    it.copy(isBotSpeaking = false, botAudioLevel = 0f)
                }
            }
            is ConversationalAIAction.OnConnectionStateChanged -> {
                _state.update {
                    it.copy(connectionState = action.state)
                }
            }
            is ConversationalAIAction.OnUserAudioLevel -> {
                _state.update {
                    it.copy(userAudioLevel = action.level)
                }
            }
            ConversationalAIAction.OnUserStartedSpeaking -> {
                _state.update {
                    it.copy(isUserSpeaking = true, isBotSpeaking = false)
                }
            }
            ConversationalAIAction.OnUserStoppedSpeaking -> {
                _state.update {
                    it.copy(isUserSpeaking = false, userAudioLevel = 0f)
                }
            }
            ConversationalAIAction.StartRecording -> {
                if (!permissionBridge.isRecordAudioPermissionGranted()) {
                    requestPermission()
                    return
                }
                _state.update {
                    it.copy(
                        isRecording = true,
                        connectionState = ConversationalAIConnectionState.Connecting
                    )
                }
                startConversation()
            }
            ConversationalAIAction.StopRecording -> {
                conversationalAI.stop()
                _state.update {
                    it.copy(
                        isRecording = false,
                        isBotReady = false,
                        connectionState = ConversationalAIConnectionState.Idle,
                        isUserSpeaking = false,
                        isBotSpeaking = false,
                        userAudioLevel = 0f,
                        botAudioLevel = 0f
                    )
                }
            }
        }
    }

    private fun requestPermission() {
        permissionBridge
            .requestRecordAudioPermission(object : PermissionResultCallback {
                override fun onPermissionGranted() {
                    _state.update {
                        it.copy(isRecordAudioPermissionGranted = permissionBridge.isRecordAudioPermissionGranted())
                    }
                    onAction(ConversationalAIAction.StartRecording)
                }

                override fun onPermissionDenied(
                    isPermanentDenied: Boolean
                ) {
                    _state.update {
                        it.copy(isRecordAudioPermissionGranted = permissionBridge.isRecordAudioPermissionGranted())
                    }
                    _validationEvent.tryEmit(ValidationEvent.Error("Record Audio Permission Denied"))

                }
            })
    }

    private fun startConversation() {
        conversationalAI.start(
            onBotReady = {
                onAction(ConversationalAIAction.OnBotReady)
            },
            onBotStartedSpeaking = {
                onAction(ConversationalAIAction.OnBotStartedSpeaking)
            },
            onBotStoppedSpeaking = {
                onAction(ConversationalAIAction.OnBotStoppedSpeaking)
            },
            onConnected = {
                onAction(ConversationalAIAction.OnConnectionStateChanged(ConversationalAIConnectionState.Connected))
            },
            onDisconnected = {
                onAction(ConversationalAIAction.OnConnectionStateChanged(ConversationalAIConnectionState.Idle))
            },
            onRemoteAudioLevel = { level ->
                onAction(ConversationalAIAction.OnBotAudioLevel(level.coerceIn(0f, 1f)))
            },
            onError = { error ->
                viewModelScope.launch {
                    _validationEvent.emit(ValidationEvent.Error(error))
                    onAction(ConversationalAIAction.OnConnectionStateChanged(ConversationalAIConnectionState.Idle))
                }
            },
            onUserStartedSpeaking = {
                onAction(ConversationalAIAction.OnUserStartedSpeaking)
            },
            onUserStoppedSpeaking = {
                onAction(ConversationalAIAction.OnUserStoppedSpeaking)
            },
            onUserAudioLevel = {
                onAction(ConversationalAIAction.OnUserAudioLevel(it.coerceIn(0f, 1f)))
            },
            onFunctionCall = { func, args, onResult ->
                viewModelScope.launch {
                    EventBus.emit(Event.OnFunctionCall(func, args, onResult))
                }
            }
        )
    }

}