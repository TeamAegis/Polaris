package appcup.uom.polaris.features.chat.domain

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun initialize(): Result<Unit, DataError.ChatError>
    fun getChatHistory(): Flow<List<Message>>
    suspend fun sendMessage(message: String): Result<Unit, DataError.ChatError>
    suspend fun clearChatHistory(): Result<Unit, DataError.ChatError>


}