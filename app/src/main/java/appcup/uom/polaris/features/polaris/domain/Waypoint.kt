package appcup.uom.polaris.features.polaris.domain

import android.net.Uri
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalSerializationApi::class,ExperimentalUuidApi::class)
data class Waypoint(
    val id: Uuid = Uuid.NIL,
    @SerialName("place_id")
    val placeId: String?,
    val name: String,
    val address: String?,
    @EncodeDefault(NEVER)
    val rating: Double?,
    @EncodeDefault(NEVER)
    val userRatingsTotal: Double?,
    @EncodeDefault(NEVER)
    val openNow: Boolean?,
    @EncodeDefault(NEVER)
    val phoneNumber: String?,
    @EncodeDefault(NEVER)
    val websiteUri: Uri?,
    val latitude: Double,
    val longitude: Double,
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
}