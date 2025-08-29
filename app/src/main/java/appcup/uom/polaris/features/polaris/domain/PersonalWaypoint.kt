package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)
@Serializable
data class PersonalWaypoint(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val id: Uuid? = null,
    @SerialName("place_id")
    val placeId: String? = null,
    val name: String? = null,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    @SerialName("is_unlocked")
    val isUnlocked: Boolean,
    val type: WaypointType,
    @SerialName("user_id")
    val userId: Uuid,
    @SerialName("journey_id")
    val journeyId: Uuid
) {
    constructor(longitude: Double, latitude: Double, placeId: String) : this(
        id = null,
        placeId = placeId,
        name = null,
        address = null,
        latitude = latitude,
        longitude = longitude,
        isUnlocked = false,
        type = WaypointType.INTERMEDIATE,
        userId = Uuid.random(),
        journeyId = Uuid.random()
    )
}

@OptIn(ExperimentalUuidApi::class)
fun PersonalWaypoint.toWaypoint(): Waypoint
    = Waypoint().copy(
        id = this.id ?: Uuid.NIL,
        placeId = this.placeId,
        name = this.name ?: "",
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        waypointType = this.type
    )