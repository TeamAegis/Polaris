package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class PublicWaypoint(
    val id: Uuid,
    @SerialName("place_id")
    val placeId: String? = null,
    val name: String? = null,
    val address: String? = null,
    val longitude: Double,
    val latitude: Double
)