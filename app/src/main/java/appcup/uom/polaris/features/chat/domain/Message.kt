package appcup.uom.polaris.features.chat.domain

import appcup.uom.polaris.core.presentation.app.utils.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Message @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class) constructor(
    val role: Role,
    val content: String,
    @SerialName("user_id")
    val userId: Uuid,

    @EncodeDefault(NEVER)
    val id: Uuid? = null,

    @Serializable(with = InstantSerializer::class)
    @EncodeDefault(NEVER)
    val timestamp: Instant? = null
)
