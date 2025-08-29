package appcup.uom.polaris.features.quest.domain

interface QuestRepository {
    suspend fun fetchDailyQuest(): List<Quest>
    suspend fun fetchWeeklyQuest(): List<Quest>
    suspend fun fetchAllPendingQuests(): List<Quest>
    suspend fun fetchAllCompletedQuests(): List<Quest>
    suspend fun fetchAllQuests(): List<Quest>
    suspend fun setQuestCompleted(questId: Long, type: QuestType)
    suspend fun createQuests()

}