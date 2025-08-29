package appcup.uom.polaris.features.qr_code_analyzer

sealed class QRScannerEvent {
    data class OnError(val message: String) : QRScannerEvent()
    object OnTransactionSuccess : QRScannerEvent()
}