package appcup.uom.polaris.features.conversational_ai.data

import ai.pipecat.client.RTVIClient
import ai.pipecat.client.RTVIClientOptions
import ai.pipecat.client.RTVIClientParams
import ai.pipecat.client.RTVIEventCallbacks
import ai.pipecat.client.helper.LLMFunctionCall
import ai.pipecat.client.helper.LLMHelper
import ai.pipecat.client.result.Future
import ai.pipecat.client.result.RTVIError
import ai.pipecat.client.transport.MsgClientToServer
import ai.pipecat.client.types.Participant
import ai.pipecat.client.types.ServiceConfig
import ai.pipecat.client.types.TransportState
import ai.pipecat.client.types.Value
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import appcup.uom.polaris.core.data.AppSecrets
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.toDomainValue
import appcup.uom.polaris.core.data.toPipecatValue
import appcup.uom.polaris.core.extras.transport.GeminiLiveWebsocketTransport
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallAction
import appcup.uom.polaris.features.conversational_ai.utils.toJsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConversationalAI() : KoinComponent {
    val context: Context by inject()
    val json: Json by inject()

    var client: RTVIClient? = null
    var errors: ArrayList<Error> = arrayListOf()
    var mic = true

    private fun <E> Future<E, RTVIError>.displayErrors() = withErrorCallback {
        errors.add(Error(it.description))
    }

    fun start(
        onBotReady: (Boolean) -> Unit,
        onBotStartedSpeaking: () -> Unit,
        onBotStoppedSpeaking: () -> Unit,
        onUserStartedSpeaking: () -> Unit,
        onUserStoppedSpeaking: () -> Unit,
        onConnected: () -> Unit,
        onDisconnected: () -> Unit,
        onRemoteAudioLevel: (Float) -> Unit,
        onUserAudioLevel: (Float) -> Unit,
        onFunctionCall: (FunctionCallAction, appcup.uom.polaris.features.conversational_ai.domain.Value.Object, (Map<String, appcup.uom.polaris.features.conversational_ai.domain.Value>) -> Unit) -> Unit,
        onError: (String) -> Unit
    ) {
        if (client != null) {
            return
        }

        val generationConfig = Value.Object(
            "speech_config" to Value.Object(
                "voice_config" to Value.Object(
                    "prebuilt_voice_config" to Value.Object(
                        "voice_name" to Value.Str("Puck")
                    )
                )
            ),
            "response_modalities" to Value.Array(
                Value.Str("AUDIO")
            )
        )

        val systemInstruction = Value.Object(
            "parts" to Value.Object(
                "text" to Value.Str(Constants.getSystemInstructions())
            )
        )




        val options = RTVIClientOptions(
            params = RTVIClientParams(
                baseUrl = null,
                config = GeminiLiveWebsocketTransport.Companion.buildConfig(
                    apiKey = AppSecrets.geminiLiveApiKey,
                    generationConfig = generationConfig,
                    initialUserMessage = "Hey Polaris!",
                    systemInstruction = systemInstruction,
                    tools = Value.Array(
                        Value.Object(
                            "function_declarations" to Value.Array(
                                values = ConversationalAITools.toolsArray
                            )
                        )
                    )
                )
            )
        )

        val callbacks = object : RTVIEventCallbacks() {
            override fun onTransportStateChanged(state: TransportState) {
                if (state == TransportState.Error) {
                    onError("An unexpected error occurred.")
                }
            }

            override fun onBackendError(message: String) {
                onError("An unexpected error occurred.")
            }

            override fun onBotReady(version: String, config: List<ServiceConfig>) {
                onBotReady(true)
            }


            override fun onBotStartedSpeaking() {
                onBotStartedSpeaking()
            }

            override fun onBotStoppedSpeaking() {
                onBotStoppedSpeaking()
            }

            override fun onUserStartedSpeaking() {
                onUserStartedSpeaking()
            }

            override fun onUserStoppedSpeaking() {
                onUserStoppedSpeaking()
            }

            override fun onConnected() {
                speakerMode()
                onConnected()
            }

            override fun onDisconnected() {
                client?.release()
                client = null
                onDisconnected()
            }

            override fun onUserAudioLevel(level: Float) {
                onUserAudioLevel(level)
            }

            override fun onRemoteAudioLevel(level: Float, participant: Participant) {
                onRemoteAudioLevel(level)
            }
        }

        val client = RTVIClient(
            transport = GeminiLiveWebsocketTransport.Factory(context),
            callbacks = callbacks,
            options = options
        )

        client.registerHelper(
            service = LLMHelper.Companion.TAG,
            helper = LLMHelper(object : LLMHelper.Callbacks() {
                override fun onLLMFunctionCall(
                    func: LLMFunctionCall,
                    onResult: (Value) -> Unit
                ) {
                    val functionCallAction = try {
                        FunctionCallAction.valueOf(func.functionName)
                    } catch (_: IllegalArgumentException) {
                        onError("Unknown function call: ${func.functionName}")
                        return
                    }
                    onFunctionCall(functionCallAction, func.args.toDomainValue() as appcup.uom.polaris.features.conversational_ai.domain.Value.Object) { res ->
                        onResult(Value.Object(res.mapValues { it.value.toPipecatValue() }))
                    }
                }
            })
        )

        client.connect().displayErrors().withErrorCallback {
            callbacks.onDisconnected()
        }

        this.client = client
    }

    fun enableMic(enabled: Boolean) {
        client?.enableMic(enabled)?.displayErrors()
    }

    fun enableCamera(enabled: Boolean) {
        client?.enableCam(enabled)?.displayErrors()
    }

    fun toggleMic() = enableMic(!mic)

    fun stop() {
        client?.disconnect()?.displayErrors()
    }

    fun sendUserMessage(message: String): Future<Unit, RTVIError>? {
        return client?.sendMessage(
            MsgClientToServer(
                id = "",
                type = "user-message",
                data = json.encodeToJsonElement(message)
            )
        )
    }

    fun sendCameraStream(stream: ByteArray): Future<Unit, RTVIError>? {
        return client?.sendMessage(
            MsgClientToServer(
                id = "",
                type = "camera-stream",
                data = stream.toJsonElement()
            )
        )
    }

    @Suppress("DEPRECATION")
    fun speakerMode() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var speakerDevice: AudioDeviceInfo? = null
        val devices: MutableList<AudioDeviceInfo> =
            audioManager.availableCommunicationDevices
        for (device in devices) {
            if (device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                speakerDevice = device
                break
            }
        }
        if (speakerDevice != null) {
            val result = audioManager.setCommunicationDevice(speakerDevice)
            if (!result) {
                errors.add(Error("Failed to set communication device"))
            }
//                    audioManager.clearCommunicationDevice()
        }
    }
}