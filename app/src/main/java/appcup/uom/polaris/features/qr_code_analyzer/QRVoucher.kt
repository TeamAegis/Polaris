package appcup.uom.polaris.features.qr_code_analyzer

import kotlinx.serialization.Serializable

// Data classes for QR voucher
@Serializable
data class QRVoucher(
    val title: String,
    val description: String,
    val pointsRequired: Int
)

