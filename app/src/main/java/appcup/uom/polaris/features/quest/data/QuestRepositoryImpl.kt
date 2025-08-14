package appcup.uom.polaris.features.quest.data

import android.util.Log
import appcup.uom.polaris.QuestQueries
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.createQuest
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.domain.Preferences
import appcup.uom.polaris.features.quest.domain.Quest
import appcup.uom.polaris.features.quest.domain.QuestRepository
import appcup.uom.polaris.features.quest.domain.QuestStatus
import appcup.uom.polaris.features.quest.domain.QuestType
import appcup.uom.polaris.features.quest.domain.Quests
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

class QuestRepositoryImpl(
    private val questDataSource: QuestQueries,
    private val locationManager: LocationManager,
) : QuestRepository {

    override suspend fun createQuests() {
        try {
            val waypoints = locationManager.nearbySearchPlaces()
            val questSchema = Schema.obj(
                mapOf(
                    "id" to Schema.integer(),
                    "placeId" to Schema.string(),
                    "title" to Schema.string(),
                    "description" to Schema.string(),
                    "placeName" to Schema.string(),
                    "address" to Schema.string(),
                    "longitude" to Schema.double(),
                    "latitude" to Schema.double(),
                    "placeType" to Schema.array(Schema.string()),
                    "type" to Schema.enumeration(listOf("DAILY", "WEEKLY")),
                    "status" to Schema.enumeration(listOf("PENDING", "COMPLETED")),
                    "createdDate" to Schema.string()
                )
            )

            val questsSchema = Schema.obj(
                mapOf(
                    "daily" to Schema.array(
                        questSchema,
                        minItems = 5,
                        maxItems = 5
                    ),
                    "weekly" to Schema.array(
                        questSchema,
                        minItems = 5,
                        maxItems = 5
                    )
                )
            )
            val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                modelName = Constants.GEMINI_API_MODEL,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = questsSchema
                })

            val prompt = createQuest(
                waypoints.toString(),
                Preferences.NATURE.types.random() + Preferences.FOOD.types.random() + Preferences.ATTRACTIONS.types.random()
            )
            questDataSource.deleteAll()
            val response = model.generateContent(prompt)
            val quests = Json.decodeFromString(Quests.serializer(), response.text ?: "{}")

            quests.daily.forEach { quest ->
                questDataSource.insert(
                    place_id = quest.placeId,
                    title = quest.title,
                    description = quest.description,
                    place_name = quest.placeName,
                    address = quest.address,
                    latitude = quest.latitude,
                    longitude = quest.longitude,
                    place_type = Json.encodeToString(
                        ListSerializer(String.serializer()),
                        quest.placeType
                    ),
                    type = quest.type.name,
                    status = quest.status.name,
                    created_date = quest.createdDate
                )
            }

            quests.weekly.forEach { quest ->
                questDataSource.insert(
                    place_id = quest.placeId,
                    title = quest.title,
                    description = quest.description,
                    place_name = quest.placeName,
                    address = quest.address,
                    latitude = quest.latitude,
                    longitude = quest.longitude,
                    place_type = Json.encodeToString(
                        ListSerializer(String.serializer()),
                        quest.placeType
                    ),
                    type = quest.type.name,
                    status = quest.status.name,
                    created_date = quest.createdDate
                )
            }

        } catch (e: Exception) {
        }
    }


    @OptIn(ExperimentalTime::class)
    override suspend fun fetchDailyQuest(): List<Quest> {
        val quests = questDataSource.selectQuests(QuestType.DAILY.name).executeAsList()
        if (quests.isNotEmpty()) {
            return quests.map { quest ->
                Quest(
                    id = quest.id,
                    placeId = quest.place_id,
                    title = quest.title,
                    description = quest.description,
                    placeName = quest.place_name ?: "",
                    address = quest.address ?: "",
                    longitude = quest.longitude,
                    latitude = quest.latitude,
                    placeType = Json.decodeFromString(
                        ListSerializer(String.serializer()),
                        quest.place_type ?: "[]"
                    ),
                    type = QuestType.valueOf(quest.type ?: QuestType.DAILY.name),
                    status = QuestStatus.valueOf(quest.status ?: QuestStatus.PENDING.name),
                    createdDate = quest.created_date!!
                )
            }
        } else {
            createQuests()
            return emptyList()
        }
    }

    override suspend fun fetchWeeklyQuest(): List<Quest> {
        val quests = questDataSource.selectQuests(QuestType.WEEKLY.name).executeAsList()
        if (quests.isNotEmpty()) {
            return quests.map { quest ->
                Quest(
                    id = quest.id,
                    placeId = quest.place_id,
                    title = quest.title,
                    description = quest.description,
                    placeName = quest.place_name ?: "",
                    address = quest.address ?: "",
                    longitude = quest.longitude,
                    latitude = quest.latitude,
                    placeType = Json.decodeFromString(
                        ListSerializer(String.serializer()),
                        quest.place_type ?: "[]"
                    ),
                    type = QuestType.valueOf(quest.type ?: QuestType.WEEKLY.name),
                    status = QuestStatus.valueOf(quest.status ?: QuestStatus.PENDING.name),
                    createdDate = quest.created_date!!
                )
            }
        } else {
            createQuests()
            return emptyList()
        }
    }

    override suspend fun setQuestCompleted(questId: Long) {
        questDataSource.update(QuestStatus.COMPLETED.name, questId)
    }
}