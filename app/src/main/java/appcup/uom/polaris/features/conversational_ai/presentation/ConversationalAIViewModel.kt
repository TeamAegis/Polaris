package appcup.uom.polaris.features.conversational_ai.presentation

import ai.pipecat.client.result.RTVIError
import ai.pipecat.client.result.Result
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.features.conversational_ai.data.ConversationalAI
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai.ConversationalAIAction
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai.ConversationalAIConnectionState
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai.ConversationalAIState
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message.ConversationalAIMessageAction
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message.ConversationalAIMessageState
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.conversational_ai.utils.PermissionResultCallback
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ConversationalAIViewModel(
    private val conversationalAI: ConversationalAI,
    private val permissionBridge: PermissionBridge
) : ViewModel() {
    private val _state = MutableStateFlow(ConversationalAIState())
    val state = _state.asStateFlow()

    private val _messageState = MutableStateFlow(ConversationalAIMessageState())
    val messageState = _messageState.asStateFlow()

    private val _event = MutableSharedFlow<ConversationalAIEvent>()
    val event = _event.asSharedFlow()

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
                startConversation {}
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

            is ConversationalAIAction.OnMuteStateChanged -> {
                _state.update {
                    it.copy(isMuted = action.isMuted)
                }
                conversationalAI.enableMic(!action.isMuted)
            }
        }
    }

    fun onMessageAction(action: ConversationalAIMessageAction) {
        when (action) {
            ConversationalAIMessageAction.SendMessage -> {
                _messageState.update {
                    it.copy(isLoading = true)
                }
                if (!_state.value.isRecording) {
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
                    startConversation {
                        sendMessage()
                    }
                } else {
                    sendMessage()
                }
            }

            is ConversationalAIMessageAction.OnMessageChanged -> {
                _messageState.update {
                    it.copy(message = action.message)
                }
            }

            ConversationalAIMessageAction.OnReset -> {
                _messageState.update {
                    ConversationalAIMessageState()
                }
            }

        }
    }
    private fun sendMessage() {
        viewModelScope.launch {
            val res = conversationalAI.sendUserMessage(_messageState.value.message)
            res?.withCallback { it ->
                when (it) {
                    is Result.Err<RTVIError> -> {
                        viewModelScope.launch {
                            _event.emit(ConversationalAIEvent.Error(it.error.description))
                        }
                    }

                    is Result.Ok<Unit> -> {

                    }
                }
                onMessageAction(ConversationalAIMessageAction.OnReset)
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
                    if (isPermanentDenied) {
                        viewModelScope.launch {
                            _event.emit(ConversationalAIEvent.RecordAudioPermissionDeniedPermanently)
                        }
                    } else {
                        viewModelScope.launch {
                            _event.emit(ConversationalAIEvent.RecordAudioPermissionDenied)
                        }
                    }

                }
            })
    }

    private fun startConversation(onReady: () -> Unit) {
        conversationalAI.start(
            onBotReady = {
                onReady()
                onAction(ConversationalAIAction.OnBotReady)
            },
            onBotStartedSpeaking = {
                onAction(ConversationalAIAction.OnBotStartedSpeaking)
            },
            onBotStoppedSpeaking = {
                onAction(ConversationalAIAction.OnBotStoppedSpeaking)
            },
            onConnected = {
                onAction(
                    ConversationalAIAction.OnConnectionStateChanged(
                        ConversationalAIConnectionState.Connected
                    )
                )
            },
            onDisconnected = {
                onAction(
                    ConversationalAIAction.OnConnectionStateChanged(
                        ConversationalAIConnectionState.Idle
                    )
                )
                viewModelScope.launch {
                    _event.emit(ConversationalAIEvent.OnStopRecording)
                }
            },
            onRemoteAudioLevel = { level ->
                onAction(ConversationalAIAction.OnBotAudioLevel(level.coerceIn(0f, 1f)))
            },
            onError = { error ->

                viewModelScope.launch {
                    _event.emit(ConversationalAIEvent.Error(error))
                    onAction(
                        ConversationalAIAction.OnConnectionStateChanged(
                            ConversationalAIConnectionState.Idle
                        )
                    )
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


    //    live translate
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                newSurfaceRequest.resolution.width.toFloat(),
                newSurfaceRequest.resolution.height.toFloat()
            )
        }
    }

    fun yuv420ToNv21(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)

        // U and V are swapped
        val chromaRowStride = image.planes[1].rowStride
        val chromaPixelStride = image.planes[1].pixelStride

        var offset = ySize
        val width = image.width
        val height = image.height

        for (row in 0 until height / 2) {
            for (col in 0 until width / 2) {
                val vuIndex = row * chromaRowStride + col * chromaPixelStride
                nv21[offset++] = vBuffer.get(vuIndex)
                nv21[offset++] = uBuffer.get(vuIndex)
            }
        }

        return nv21
    }


    private fun imageProxyToBase64Jpeg(image: ImageProxy): ByteArray? {
        val nv21 = yuv420ToNv21(image)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 90, out)
        val jpegBytes = out.toByteArray()

        return jpegBytes
    }


    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(ContextCompat.getMainExecutor(appContext)) { imageProxy ->
                    val now = System.currentTimeMillis()
                    if (now - _state.value.lastSent >= 1000) {
                        _state.update {
                            it.copy(lastSent = now)
                        }
                        val payload = imageProxyToBase64Jpeg(imageProxy)
                        if (payload != null) {
                            conversationalAI.sendCameraStream(payload)
                        }
                    }
                    imageProxy.close()
                }
            }

        if (!_state.value.isRecording) {
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
            startConversation {
                conversationalAI.enableCamera(true)
                val camera = processCameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    cameraPreviewUseCase,
                    imageAnalyzer
                )
                cameraControl = camera.cameraControl
            }
        } else {
            conversationalAI.enableCamera(true)
            val camera = processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                cameraPreviewUseCase,
                imageAnalyzer
            )
            cameraControl = camera.cameraControl
        }

        try {
            awaitCancellation()
        } finally {
            conversationalAI.enableCamera(false)
            processCameraProvider.unbindAll()
            cameraControl = null
        }
    }

    fun tapToFocus(tapPosition: Offset) {
        val point = surfaceMeteringPointFactory?.createPoint(tapPosition.x, tapPosition.y)
        if (point != null) {
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }
    }


}