package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)
@Serializable
data class PublicWaypoint(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val id: Uuid? = null,
    val address: String? = null,
    val longitude: Double,
    val latitude: Double
)