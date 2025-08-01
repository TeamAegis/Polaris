package appcup.uom.polaris.features.conversational_ai.domain

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ConversationalAIRepository {
    suspend fun initialize(): Result<Unit, DataError.Local>
    suspend fun getChatHistory(): Flow<List<Message>>
    suspend fun sendUserMessage(message: String): Result<Unit, DataError.Local>

}