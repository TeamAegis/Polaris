package appcup.uom.polaris.features.qr_code_analyzer

// State management
data class QRScannerState(
    val scanState: QRScanState = QRScanState.Scanning,
    val scannedVoucher: QRVoucher? = null,
    val userPoints: Int = 0,
    val isProcessingTransaction: Boolean = false,
    val showConfirmation: Boolean = false
)