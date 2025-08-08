package appcup.uom.polaris.features.polaris.domain

import android.net.Uri
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Waypoint(
    val waypointId: Uuid = Uuid.random(),
    val placeName: String,
    val formattedAddress: String?,
    val rating: Double?,
    val userRatingsTotal: Double?,
    val openNow: Boolean?,
    val phoneNumber: String?,
    val websiteUri: Uri?,
    val latitude: Double,
    val longitude: Double,
) {
    constructor() : this(
        waypointId = Uuid.random(),
        placeName = "",
        formattedAddress = null,
        rating = null,
        userRatingsTotal = null,
        openNow = null,
        phoneNumber = null,
        websiteUri = null,
        latitude = 0.0,
        longitude = 0.0
    )
}