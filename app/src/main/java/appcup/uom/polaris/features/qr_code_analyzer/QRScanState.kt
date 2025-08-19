package appcup.uom.polaris.features.qr_code_analyzer

sealed interface QRScanState {
    object Scanning : QRScanState
    object Success : QRScanState
    data class Error(val message: String) : QRScanState
    object InsufficientPoints : QRScanState
    object TransactionSuccess : QRScanState
}