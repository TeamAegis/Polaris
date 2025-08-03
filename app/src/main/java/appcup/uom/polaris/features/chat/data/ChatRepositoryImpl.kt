package appcup.uom.polaris.features.chat.data

import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.chat.domain.ChatRepository
import appcup.uom.polaris.features.chat.domain.Message
import appcup.uom.polaris.features.chat.domain.Role
import appcup.uom.polaris.features.chat.utils.function_call.ChatFunctionCallAction
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.content
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

class ChatRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val generativeModel: GenerativeModel
) : ChatRepository {
    lateinit var chat: Chat

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun initialize(): Result<Unit, DataError.Local> {
        try {
            val messages = supabaseClient.from("messages").select {
                filter {
                    Message::id eq StaticData.user.id
                }
            }.decodeList<Message>()

            chat = generativeModel.startChat(
                messages.map {
                    content(role = it.role.capitalizeFirstLetter()) {
                        text(it.content)
                    }
                }
            )

            return Result.Success(Unit)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    @OptIn(ExperimentalUuidApi::class, SupabaseExperimental::class)
    override suspend fun getChatHistory(): Flow<List<Message>> {
        return supabaseClient.from("messages").selectAsFlow(
            Message::id,
            filter = FilterOperation("user_id", FilterOperator.EQ, StaticData.user.id)
        )
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun sendMessage(message: String): Result<Unit, DataError.Local> {
        if (message.isBlank()) return Result.Error(DataError.Local.MESSAGE_EMPTY)

        return try {
            // Send the message to the chat model
            var currentResponse = chat.sendMessage(message)

            while (true) {

                val functionCalls = currentResponse.functionCalls
                if (functionCalls.isEmpty()) break

                // Handle multiple function calls
                val functionResponses = functionCalls.map { functionCallPart ->
                    val action = try {
                        ChatFunctionCallAction.valueOf(functionCallPart.name)
                    } catch (_: IllegalArgumentException) {
                        null
                    }

                    val responsePayload = if (action == null) {
                        Json.Default.encodeToJsonElement(
                            mapOf("error" to "Unknown function call: ${functionCallPart.name}")
                        ).jsonObject
                    } else {
                        try {
                            val result = performFunction(
                                action = action,
                                args = Json.Default.encodeToJsonElement(functionCallPart.args).jsonObject
                            )
                            Json.Default.encodeToJsonElement(result).jsonObject
                        } catch (e: Exception) {
                            Json.Default.encodeToJsonElement(
                                mapOf("error" to "Function ${functionCallPart.name} failed: ${e.message}")
                            ).jsonObject
                        }
                    }

                    FunctionResponsePart(name = functionCallPart.name, response = responsePayload)
                }

                // Send function response and get next model reply
                currentResponse = chat.sendMessage(
                    content("function") {
                        functionResponses.forEach {
                            part(it)
                        }
                    }
                )
            }

            if (currentResponse.text != null && currentResponse.text!!.isNotBlank()) {
                supabaseClient.from("messages").insert(
                    listOf(
                        Message(
                            role = Role.USER,
                            content = message,
                            userId = StaticData.user.id
                        ),
                        Message(
                            role = Role.MODEL,
                            content = currentResponse.text!!,
                            userId = StaticData.user.id
                        )
                    )
                )
            } else {
                Result.Error(DataError.Local.UNKNOWN)
            }

            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun clearChatHistory(): Result<Unit, DataError.Local> {
        return try {
            supabaseClient.from("messages").delete {
                filter {
                    Message::userId eq StaticData.user.id
                }
            }
            initialize()
            Result.Success(Unit)
        } catch (e: Exception) {
            println(Constants.DEBUG_VALUE + e.message)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }


    suspend fun performFunction(action: ChatFunctionCallAction, args: JsonObject): Any {
        return when (action) {
            else -> {}
        }
    }


}