package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
@Serializable
data class Waypoint(
    @EncodeDefault(NEVER)
    val id: Uuid? = null,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
) {
    constructor() : this(null, "", "", 0.0, 0.0)
}
