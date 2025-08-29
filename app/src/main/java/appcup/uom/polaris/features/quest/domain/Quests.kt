package appcup.uom.polaris.features.quest.domain

import kotlinx.serialization.Serializable

@Serializable
data class Quests(
    val daily: List<Quest>,
    val weekly: List<Quest>
)
