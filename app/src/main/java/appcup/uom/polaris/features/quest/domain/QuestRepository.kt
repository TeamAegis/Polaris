package appcup.uom.polaris.features.quest.domain

interface QuestRepository {
    suspend fun fetchDailyQuest(): List<Quest>
    suspend fun fetchWeeklyQuest(): List<Quest>
    suspend fun setQuestCompleted(questId: Long)
    suspend fun createQuests()

}