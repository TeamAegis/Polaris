package appcup.uom.polaris.features.polaris.domain

import appcup.uom.polaris.core.presentation.app.utils.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Serializable
data class Fragment(
    val id: String,
    @SerialName("public_waypoint_id")
    val publicWaypointId: Uuid,
    @SerialName("user_id")
    val userId: Uuid,
    @SerialName("fragment_url")
    val fragmentUrl: String,
    @Serializable(with = InstantSerializer::class)
    @SerialName("created_at")
    val createdAt: Instant
)