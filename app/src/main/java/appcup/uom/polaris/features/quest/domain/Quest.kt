package appcup.uom.polaris.features.quest.domain

import kotlinx.serialization.Serializable

@Serializable
data class Quest(
    val id: Long? = null,
    val placeId: String?,
    val title: String,
    val description: String,
    val placeName: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val placeType: List<String> = emptyList(),
    val type: QuestType,
    val status: QuestStatus,
    val createdDate: String,
)
