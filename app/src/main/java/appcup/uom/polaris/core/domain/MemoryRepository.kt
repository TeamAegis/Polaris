package appcup.uom.polaris.core.domain

import android.net.Uri
import appcup.uom.polaris.Memory
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {
    fun getAllMemory(): Flow<List<Memory>>
    suspend fun createMemory(memory: Memory)
    suspend fun saveImage(uri: Uri): String
    suspend fun deleteMemoryIfImageMissing(memory: Memory)
    suspend fun getMemories(journeyId: String): List<Memory>
}