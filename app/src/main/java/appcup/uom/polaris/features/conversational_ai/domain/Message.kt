package appcup.uom.polaris.features.conversational_ai.domain

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Message @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val role: Role,
    val content: String,
    val timestamp: Long,
    val userId: Uuid
)
