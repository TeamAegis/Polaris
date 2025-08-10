package appcup.uom.polaris.features.polaris.domain

import appcup.uom.polaris.core.presentation.app.utils.InstantSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class, ExperimentalSerializationApi::class)
@Serializable
data class Journey(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val id: Uuid? = null,
    val name: String,
    val description: String,
    val preferences: List<Preferences>,
    @SerialName("encoded_polyline")
    val encodedPolyline: String,
    val status: JourneyStatus,
    @SerialName("user_id")
    val userId: Uuid,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @Serializable(with = InstantSerializer::class)
    @SerialName("created_at")
    val createdAt: Instant? = null
)