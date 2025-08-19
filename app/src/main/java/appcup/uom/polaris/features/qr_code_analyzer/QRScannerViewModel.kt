package appcup.uom.polaris.features.qr_code_analyzer

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.features.auth.domain.UserRepository
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

// ViewModel
class QRScannerViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QRScannerState())
    val state = _state.asStateFlow()

    private val _event = Channel<QRScannerEvent>()
    val event = _event.receiveAsFlow()

    private val jsonParser = Json { ignoreUnknownKeys = true }

    // Camera setup
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    init {
        loadUserPoints()
    }

    fun onAction(action: QRScannerAction) {
        when (action) {
            QRScannerAction.StartScanning -> {
                _state.update { it.copy(scanState = QRScanState.Scanning) }
            }

            QRScannerAction.ResetScanner -> {
                _state.update {
                    it.copy(
                        scanState = QRScanState.Scanning,
                        scannedVoucher = null,
                        showConfirmation = false
                    )
                }
            }

            QRScannerAction.ConfirmTransaction -> {
                processTransaction()
            }

            QRScannerAction.CancelTransaction -> {
                _state.update { it.copy(showConfirmation = false) }
            }

            is QRScannerAction.OnBarcodeDetected -> {
                onBarcodeDetected(action.barcodes)
            }
        }
    }

    private fun loadUserPoints() {
        _state.update { it.copy(userPoints = StaticData.user.points) }
    }

    private fun onBarcodeDetected(barcodes: List<Barcode>) {
        viewModelScope.launch {
            if (barcodes.isEmpty()) {
                _state.update { it.copy(scanState = QRScanState.Error("No QR code detected")) }
                return@launch
            }

            barcodes.forEach { barcode ->
                barcode.rawValue?.let { barcodeValue ->
                    try {
                        val voucher: QRVoucher = jsonParser.decodeFromString(barcodeValue)

                        if (_state.value.userPoints >= voucher.pointsRequired) {
                            _state.update {
                                it.copy(
                                    scanState = QRScanState.Success,
                                    scannedVoucher = voucher,
                                    showConfirmation = true
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    scanState = QRScanState.InsufficientPoints,
                                    scannedVoucher = voucher
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(scanState = QRScanState.Error("Invalid QR code format"))
                        }
                    }
                    return@launch
                }
            }

            _state.update { it.copy(scanState = QRScanState.Error("No valid QR code data")) }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun processTransaction() {
        val voucher = _state.value.scannedVoucher ?: return

        viewModelScope.launch {
            _state.update { it.copy(isProcessingTransaction = true) }

            try {
                // Process the transaction
                userRepository.usePoints(
                    points = voucher.pointsRequired
                )
//                userRepository.redeemVoucherWithPoints(
//                    voucherTitle = voucher.title,
//                    pointsRequired = voucher.pointsRequired
//                )

                // Update user points
                StaticData.user = StaticData.user.copy(
                    points = StaticData.user.points - voucher.pointsRequired
                )
                _state.update {
                    it.copy(
                        userPoints = StaticData.user.points,
                        scanState = QRScanState.TransactionSuccess,
                        isProcessingTransaction = false,
                        showConfirmation = false
                    )
                }

                _state.update {
                    QRScannerState()
                }

                _event.send(QRScannerEvent.OnTransactionSuccess)
                loadUserPoints()
            } catch (e: Exception) {
                _state.update { it.copy(isProcessingTransaction = false) }
                _event.send(QRScannerEvent.OnError(e.message ?: "Transaction failed"))
            }
        }
    }

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)

        val barcodeAnalyzer = BarcodeAnalyzer { barcodes ->
            onAction(QRScannerAction.OnBarcodeDetected(barcodes))
        }

        val imageAnalysis =
            ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(appContext), barcodeAnalyzer)
                }

        try {
            processCameraProvider.unbindAll()
            processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                cameraPreviewUseCase,
                imageAnalysis
            )
        } catch (e: Exception) {
            _event.send(QRScannerEvent.OnError("Camera setup failed"))
        }

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }
}