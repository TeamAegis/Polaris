package appcup.uom.polaris.core.presentation.app.utils

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.core.net.toUri

object NullableUriSerializer : KSerializer<Uri?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NullableUri", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uri?) {
        encoder.encodeString(value?.toString() ?: "")
    }

    override fun deserialize(decoder: Decoder): Uri? {
        val uriString = decoder.decodeString()
        return if (uriString.isBlank()) null else uriString.toUri()
    }
}
