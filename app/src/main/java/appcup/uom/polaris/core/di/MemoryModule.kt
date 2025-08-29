package appcup.uom.polaris.core.di

import android.content.Context
import appcup.uom.polaris.Database
import appcup.uom.polaris.MemoryQueries
import appcup.uom.polaris.core.data.MemoryRepositoryImpl
import appcup.uom.polaris.core.domain.MemoryRepository
import appcup.uom.polaris.core.presentation.memories.memory.MemoryViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class MemoryModule {
    @Single
    fun provideMemoryDataSource(database: Database): MemoryQueries = database.memoryQueries

    @Single
    fun provideMemoryRepository(
        memoryDataSource: MemoryQueries,
        context: Context
    ): MemoryRepository =
        MemoryRepositoryImpl(
            memoryDataSource,
            context
        )

    @KoinViewModel
    fun provideMemoryViewModel(
        memoryRepository: MemoryRepository
    ): MemoryViewModel = MemoryViewModel(
        memoryRepository
    )
}