package appcup.uom.polaris.core.data

import android.content.Context
import android.net.Uri
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import appcup.uom.polaris.Memory
import appcup.uom.polaris.MemoryQueries
import appcup.uom.polaris.core.domain.MemoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class MemoryRepositoryImpl(
    private val memoryDataSource: MemoryQueries,
    private val context: Context
) : MemoryRepository {
    override fun getAllMemory(): Flow<List<Memory>> =
        memoryDataSource.selectAll().asFlow().mapToList(Dispatchers.IO)

    override suspend fun createMemory(memory: Memory) {
        memoryDataSource.insert(
            latitude = memory.latitude,
            longitude = memory.longitude,
            path = memory.path,
            journey_id = memory.journey_id
        )

    }

    override suspend fun saveImage(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val memoriesDir = File(context.filesDir, "memories")
            if (!memoriesDir.exists()) {
                memoriesDir.mkdirs()
            }

            val fileName = "memory_${System.currentTimeMillis()}.jpg"
            val destFile = File(memoriesDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            destFile.absolutePath
        }
    }

    override suspend fun deleteMemoryIfImageMissing(memory: Memory) {
        withContext(Dispatchers.IO) {
            val file = File(memory.path)
            if (!file.exists()) {
                memoryDataSource.delete(memory.id)
            }
        }
    }
}