package appcup.uom.polaris.features.polaris.domain

import android.net.Uri
import appcup.uom.polaris.core.presentation.app.utils.NullableUriSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalSerializationApi::class,ExperimentalUuidApi::class)
data class Waypoint(
    val id: Uuid = Uuid.NIL,
    val placeId: String?,
    val name: String,
    val address: String?,
    val rating: Double?,
    val userRatingsTotal: Double?,
    val openNow: Boolean?,
    val phoneNumber: String?,
    @Serializable(with = NullableUriSerializer::class)
    val websiteUri: Uri?,
    val latitude: Double,
    val longitude: Double,
    val waypointType: WaypointType = WaypointType.CURRENT_LOCATION,
    val placeType: List<String> = emptyList(),
    val isReceivedFromAI: Boolean = false
) {
    constructor() : this(
        id = Uuid.NIL,
        placeId = null,
        name = "",
        address = null,
        rating = null,
        userRatingsTotal = null,
        openNow = null,
        phoneNumber = null,
        websiteUri = null,
        latitude = 0.0,
        longitude = 0.0
    )

    fun toPersonalWaypoint(
        type: WaypointType,
        isUnlocked: Boolean,
        userId: Uuid,
        journeyId: Uuid,
        id: Uuid? = null
    )
    = PersonalWaypoint(
        id = id,
        placeId = placeId,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        isUnlocked = isUnlocked,
        type = type,
        userId = userId,
        journeyId = journeyId
    )
}