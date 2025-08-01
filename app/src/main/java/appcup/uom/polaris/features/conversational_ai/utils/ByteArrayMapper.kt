package appcup.uom.polaris.features.conversational_ai.utils

import kotlinx.serialization.json.*
import java.util.Base64

fun ByteArray.toJsonElement(): JsonElement {
    val base64 = Base64.getEncoder().encodeToString(this)
    return JsonPrimitive(base64)
}

fun JsonElement.toByteArray(): ByteArray {
    val base64 = this.jsonPrimitive.content
    return Base64.getDecoder().decode(base64)
}
