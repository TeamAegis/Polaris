package appcup.uom.polaris.features.conversational_ai.data

import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.conversational_ai.domain.ConversationalAIRepository
import appcup.uom.polaris.features.conversational_ai.domain.Message
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallAction
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.uuid.ExperimentalUuidApi

class ConversationalAIRepositoryImpl(
    private val supabaseClient: SupabaseClient
): ConversationalAIRepository {
    lateinit var chat: Chat
    override suspend fun initialize(): Result<Unit, DataError.Local> {
        try {
            val messages = supabaseClient.from("messages").select().decodeList<Message>()

            chat = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                modelName = Constants.GEMINI_API_MODEL,
                generationConfig = generationConfig {
                    responseModalities = listOf(ResponseModality.TEXT)
                },
            ).startChat(
                messages.map {
                    content(role = it.role.capitalizeFirstLetter()) {
                        text(it.content)
                    }
                }
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    @OptIn(ExperimentalUuidApi::class, SupabaseExperimental::class)
    override suspend fun getChatHistory(): Flow<List<Message>> {
        return supabaseClient.from("messages").selectAsFlow(Message::id)
    }

    override suspend fun sendUserMessage(message: String): Result<Unit, DataError.Local> {
        try {
            val response = chat.sendMessage(message)
            response.functionCalls.forEach { functionCallPart ->
                val functionCallAction = try {
                    FunctionCallAction.valueOf(functionCallPart.name)
                } catch (_: IllegalArgumentException) {
                    null
                }
                if (functionCallAction == null) {
                    val errorResponse = Json.encodeToJsonElement(mapOf("error" to "Unknown function call: ${functionCallPart.name}")).jsonObject
                    chat.sendMessage(content("function") {
                            FunctionResponsePart(name = functionCallPart.name, response = errorResponse)
                        }
                    )
                } else {

                }
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }
}