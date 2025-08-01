package appcup.uom.polaris.core.extras.transport

import ai.pipecat.client.types.Value
import android.Manifest
import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresPermission
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.SendAudioData
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.SendCameraStream
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.SendFunctionResponse
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.SendUserMessage
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.SendUserTextMessage
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.SetMicMute
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.Stop
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.WebsocketClosed
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.WebsocketFailed
import appcup.uom.polaris.core.extras.transport.GeminiClient.ClientThreadEvent.WebsocketMessage
import appcup.uom.polaris.core.extras.transport.RealtimeInputRequest.RealtimeInput
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.math.sqrt

private const val API_URL =
    "https://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent"

private const val TAG = "GeminiClient"

private val JSON = Json { ignoreUnknownKeys = true }

private fun <E> AtomicReference<E?>.take(): E? = getAndSet(null)

@Serializable
private data class SetupRequest(
    val setup: Value
)

@Serializable
private data class ClientContentRequest(
    @SerialName("client_content")
    val clientContent: ClientContent
) {
    @Serializable
    data class ClientContent(
        val turns: List<Turn>,
        @SerialName("turn_complete")
        val turnComplete: Boolean
    ) {
        @Serializable
        data class Turn(
            val role: String,
            val parts: List<TurnPart>
        )
    }
}

@Serializable
private data class RealtimeInputRequest(
    val realtimeInput: RealtimeInput
) {
    @Serializable
    data class RealtimeInput(
        val audio: InlineData? = null,
        val video: InlineData? = null,
        val text: String? = null
    )
}

@Serializable
private data class ToolResponseRequest(
    val toolResponse: ToolResponse
) {
    @Serializable
    data class ToolResponse(
        val functionResponses: List<FunctionResponses>? = null
    ) {
        @Serializable
        data class FunctionResponses(
            val id: String? = null,
            val name: String,
            val response: JsonElement,
            val willContinue: Boolean? = null
        )
    }
}

@Serializable
private data class IncomingMessage(
    val setupComplete: SetupComplete? = null,
    val serverContent: ServerContent? = null,
    val toolCall: ToolCall? = null,
) {
    @Serializable
    data class SetupComplete(
        val dummyField: Boolean = false
    )

    @Serializable
    data class ServerContent(
        val modelTurn: ModelTurn? = null,
        val turnComplete: Boolean = false,
        val interrupted: Boolean = false
    ) {
        @Serializable
        data class ModelTurn(
            val parts: List<TurnPart>
        )
    }

    @Serializable
    data class ToolCall(
        val functionCalls: List<FunctionCall>
    ) {
        @Serializable
        data class FunctionCall(
            val name: String,
            @SerialName("args")
            val arguments: Value.Object,
            val id: String
        )
    }
}

@Serializable
private data class TurnPart(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
private data class InlineData(
    val mimeType: String,
    val data: String
) {
    companion object {
        fun ofPcm(sampleRateHz: Int, data: ByteArray) = InlineData(
            mimeType = "audio/pcm;rate=$sampleRateHz",
            data = Base64.encodeToString(data, Base64.NO_WRAP)
        )

        fun ofJpeg(data: ByteArray) = InlineData(
            mimeType = "image/jpeg",
            data = Base64.encodeToString(data, Base64.NO_WRAP)
        )
    }
}

private data class AppendedMessage(
    val role: String,
    val content: String
)

internal class GeminiClient private constructor(
    private val onSendUserTextMessage: (String) -> Unit,
    private val onSendUserMessage: (AppendedMessage) -> Unit,
    private val onSendFunctionResponse: (String, String, Value.Object) -> Unit,
    private val onSendCameraStream: (ByteArray) -> Unit,
    private val onClose: () -> Unit,
    private val setMicMuted: (Boolean) -> Unit,
    private val isMicMuted: () -> Boolean,
    private val setCameraEnabled: (Boolean) -> Unit,
    private val isCameraEnabled: () -> Boolean
) {
    data class Config(
        val apiKey: String,
        val modelConfig: Value,
        val initialMessage: String?,
        val initialMicEnabled: Boolean,
    )

    interface Listener {
        fun onConnected()
        fun onSessionEnded(reason: String, t: Throwable?)
        fun onBotTalking(isTalking: Boolean)
        fun onUserTalking(isTalking: Boolean)
        fun onUserAudioLevel(level: Float)
        fun onBotAudioLevel(level: Float)
        fun onFunctionCall(id: String, name: String, args: Value.Object)
    }

    private sealed interface ClientThreadEvent {
        class SendAudioData(val buf: ByteArray) : ClientThreadEvent
        class SendCameraStream(val buf: ByteArray) : ClientThreadEvent
        class SendUserTextMessage(val msg: String) : ClientThreadEvent
        class SendUserMessage(val msg: AppendedMessage) : ClientThreadEvent
        class SendFunctionResponse(val id: String, val name: String, val response: Value.Object) :
            ClientThreadEvent

        data object Stop : ClientThreadEvent
        data object WebsocketClosed : ClientThreadEvent
        class WebsocketFailed(val t: Throwable) : ClientThreadEvent
        class WebsocketMessage(val msg: IncomingMessage, val originalText: String) :
            ClientThreadEvent

        class SetMicMute(val muted: Boolean) : ClientThreadEvent
        class SetCameraEnabled(val enabled: Boolean) : ClientThreadEvent
    }

    companion object {

        @RequiresPermission(Manifest.permission.RECORD_AUDIO)
        fun connect(config: Config, listener: Listener): GeminiClient {

            val events = LinkedBlockingQueue<ClientThreadEvent>()
            val isMicMutedRef = AtomicReference<(() -> Boolean)?>(null)
            val isCameraEnabledRef = AtomicReference<(() -> Boolean)?>(null)

            fun postEvent(event: ClientThreadEvent) = events.put(event)

            thread(name = "GeminiClient") {
                val client: OkHttpClient = OkHttpClient.Builder().build()

                val inputSampleRate = 16000

                val audioInCallback =
                    AtomicReference<((ByteArray) -> Unit)?>(null)

                val audioIn = AudioIn(
                    inputSampleRate,
                    onAudioCaptured = { audioInCallback.get()?.invoke(it) },
                    onAudioLevelUpdate = listener::onUserAudioLevel,
                    onError = {},
                    onStopped = {},
                    initialMicEnabled = config.initialMicEnabled
                )
                val audioOut = AudioOut(
                    sampleRateHz = 24000,
                    onAudioLevelUpdate = listener::onBotAudioLevel
                )

                var isBotTalking = false
                var isUserTalking = false

                isMicMutedRef.set { audioIn.muted }

                val listenerRef = AtomicReference(listener)

                val request: Request = Request.Builder()
                    .url(
                        API_URL.toHttpUrl().newBuilder()
                            .addQueryParameter("key", config.apiKey)
                            .build()
                    )
                    .build()

                val ws = client.newWebSocket(request, object : WebSocketListener() {

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        Log.i(TAG, "Websocket onClosed($code, $reason)")
                        postEvent(WebsocketClosed)
                    }

                    override fun onClosing(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String
                    ) {
                        Log.i(TAG, "Websocket onClosing($code, $reason)")
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?
                    ) {
                        Log.e(TAG, "Websocket onFailure", t)
                        postEvent(WebsocketFailed(t))
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        onIncomingMessage(text)
                    }

                    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                        onIncomingMessage(bytes.utf8())
                    }

                    @SuppressLint("MissingPermission")
                    fun onIncomingMessage(msg: String) {
                        val data =
                            JSON.decodeFromString<IncomingMessage>(
                                msg
                            )
                        postEvent(
                            WebsocketMessage(
                                msg = data,
                                originalText = msg
                            )
                        )
                    }

                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        Log.i(TAG, "Websocket onOpen")
                        webSocket.send(
                            JSON.encodeToString(
                                SetupRequest.serializer(),
                                SetupRequest(setup = config.modelConfig)
                            )
                        )
                    }
                })

                fun doSendUserTextMessage(msg: String) {
                    ws.send(
                        JSON.encodeToString(
                            RealtimeInputRequest.serializer(),
                            RealtimeInputRequest(
                                RealtimeInput(
                                    text = msg
                                )

                            )
                        )
                    )
                }

                fun doSendUserMessage(msg: AppendedMessage) {
                    ws.send(
                        JSON.encodeToString(
                            ClientContentRequest.serializer(),
                            ClientContentRequest(
                                clientContent = ClientContentRequest.ClientContent(
                                    turns = listOf(
                                        ClientContentRequest.ClientContent.Turn(
                                            role = msg.role,
                                            parts = listOf(TurnPart(text = msg.content))
                                        )
                                    ),
                                    turnComplete = true
                                )
                            )
                        )
                    )
                }

                fun doSendFunctionResponse(id: String, name: String, response: Value.Object) {
                    ws.send(
                        JSON.encodeToString(
                            ToolResponseRequest.serializer(),
                            ToolResponseRequest(
                                toolResponse = ToolResponseRequest.ToolResponse(
                                    functionResponses = listOf(
                                        ToolResponseRequest.ToolResponse.FunctionResponses(
                                            id = id,
                                            name = name,
                                            response = JSON.encodeToJsonElement(response)
                                        )
                                    )
                                )
                            )
                        )
                    )
                }

                fun stopAudio() {
                    audioIn.stop()
                    audioOut.interrupt()
                    audioOut.stop()
                }

                while (true) {
                    when (val event = events.take()) {
                        is SendAudioData -> {
                            ws.send(
                                JSON.encodeToString(
                                    RealtimeInputRequest.serializer(),
                                    RealtimeInputRequest(
                                        RealtimeInput(
                                            audio = InlineData.ofPcm(
                                                inputSampleRate,
                                                event.buf
                                            )
                                        )
                                    )
                                )
                            )
                        }

                        is SendCameraStream -> {
                            ws.send(
                                JSON.encodeToString(
                                    RealtimeInputRequest.serializer(),
                                    RealtimeInputRequest(
                                        RealtimeInput(
                                            video = InlineData.ofJpeg(event.buf)
                                        )
                                    )
                                )
                            )
                        }


                        is SendUserTextMessage -> {
                            doSendUserTextMessage(event.msg)
                        }

                        is SendUserMessage -> {
                            doSendUserMessage(event.msg)
                        }

                        is SendFunctionResponse -> {
                            doSendFunctionResponse(event.id, event.name, event.response)
                        }

                        Stop -> {
                            stopAudio()
                            ws.close(1000, "user requested close")
                        }

                        WebsocketClosed -> {
                            stopAudio()
                            listenerRef.take()?.onSessionEnded("websocket closed", null)
                            return@thread
                        }

                        is WebsocketFailed -> {
                            stopAudio()
                            listenerRef.take()?.onSessionEnded("websocket failed", event.t)
                            return@thread
                        }

                        is WebsocketMessage -> {

                            val data = event.msg

                            if (data.setupComplete != null) {
                                Log.i(TAG, "Setup complete")

                                if (config.initialMessage != null) {
                                    doSendUserMessage(
                                        AppendedMessage(
                                            content = config.initialMessage,
                                            role = "user"
                                        )
                                    )
                                }

                                listenerRef.get()?.onConnected()

                                audioInCallback.set { buf ->
                                    postEvent(SendAudioData(buf))
                                }

                            } else if (data.serverContent != null) {

                                if (data.serverContent.modelTurn != null) {

                                    if (!isBotTalking) {
                                        isBotTalking = true
                                        listenerRef.get()?.onBotTalking(true)
                                    }
                                    if (isUserTalking) {
                                        isUserTalking = false
                                        listenerRef.get()?.onUserTalking(false)
                                    }

                                    data.serverContent.modelTurn.parts.forEach { part ->

                                        if (part.text != null) {
                                            Log.i(
                                                TAG,
                                                "Model text: " + part.text
                                            )
                                        }

                                        part.inlineData?.let { inlineData ->
                                            audioOut.write(
                                                Base64.decode(
                                                    inlineData.data,
                                                    Base64.DEFAULT
                                                )
                                            )
                                        }
                                    }
                                }

                                if (data.serverContent.interrupted) {
                                    Log.i(TAG, "User interrupted model output")
                                    isBotTalking = false
                                    isUserTalking = true
                                    listenerRef.get()?.onUserTalking(true)
                                    listenerRef.get()?.onBotTalking(false)
                                    audioOut.interrupt()
                                }

                                if (data.serverContent.turnComplete) {
                                    Log.i(TAG, "Model turn complete")

                                    if (isBotTalking) {
                                        isBotTalking = false
                                        listenerRef.get()?.onBotTalking(false)
                                    }

                                    if (!isUserTalking) {
                                        isUserTalking = true
                                        listenerRef.get()?.onUserTalking(true)
                                    }
                                }
                            } else if (data.toolCall != null) {
                                data.toolCall.functionCalls.forEach { call ->
                                    Log.i(TAG, "Function call: $call")
                                    listenerRef.get()?.onFunctionCall(
                                        id = call.id,
                                        name = call.name,
                                        args = call.arguments
                                    )
                                }
                            } else {
                                Log.e(
                                    TAG,
                                    "Unknown message type: ${event.originalText}"
                                )
                            }
                        }

                        is SetMicMute -> {
                            audioIn.muted = event.muted
                        }

                        is ClientThreadEvent.SetCameraEnabled -> {
                            isCameraEnabledRef.set { event.enabled }
                        }
                    }
                }
            }

            return GeminiClient(
                onSendUserMessage = { text ->
                    postEvent(SendUserMessage(text))
                },
                onSendFunctionResponse = { id, name, response ->
                    postEvent(SendFunctionResponse(id, name, response))
                },
                onClose = {
                    postEvent(Stop)
                },
                isMicMuted = {
                    isMicMutedRef.get()?.invoke() ?: false
                },
                setMicMuted = {
                    postEvent(SetMicMute(it))
                },
                onSendUserTextMessage = {
                    postEvent(SendUserTextMessage(it))
                },
                onSendCameraStream = {
                    postEvent(SendCameraStream(it))
                },
                setCameraEnabled = {
                    postEvent(ClientThreadEvent.SetCameraEnabled(it))
                },
                isCameraEnabled = {
                    isCameraEnabledRef.get()?.invoke() ?: false
                }
            )
        }

        fun calculateAudioLevel(pcmData: ByteArray): Float {
            var sumSquares = 0.0
            val numSamples = pcmData.size / 2

            for (i in 0 until numSamples) {
                val low = pcmData[i * 2].toInt() and 0xFF
                val high = pcmData[i * 2 + 1].toInt() and 0xFF
                val sample = ((high shl 8) or low).toShort()
                val sampleValue = sample.toDouble()
                sumSquares += sampleValue * sampleValue
            }

            val rms = sqrt(sumSquares / numSamples)
            return (rms / 32768.0).toFloat().coerceIn(0.0f, 1.0f)
        }
    }

    var micMuted: Boolean
        get() = isMicMuted()
        set(muted) {
            setMicMuted(muted)
        }

    var cameraEnabled: Boolean
        get() = isCameraEnabled()
        set(enabled) {
            setCameraEnabled(enabled)
        }

    fun sendCameraStream(buf: ByteArray) = onSendCameraStream(buf)

    fun sendUserTextMessage(msg: String) = onSendUserTextMessage(msg)

    fun sendUserMessage(role: String, content: String) =
        onSendUserMessage(AppendedMessage(role = role, content = content))

    fun sendFunctionResponse(id: String, name: String, response: Value.Object) =
        onSendFunctionResponse(id, name, response)

    fun close() = onClose()
}