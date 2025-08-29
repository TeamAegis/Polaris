package appcup.uom.polaris.features.qr_code_analyzer

import com.google.mlkit.vision.barcode.common.Barcode

sealed class QRScannerAction {
    object StartScanning : QRScannerAction()
    object ResetScanner : QRScannerAction()
    object ConfirmTransaction : QRScannerAction()
    object CancelTransaction : QRScannerAction()
    data class OnBarcodeDetected(val barcodes: List<Barcode>) : QRScannerAction()
}