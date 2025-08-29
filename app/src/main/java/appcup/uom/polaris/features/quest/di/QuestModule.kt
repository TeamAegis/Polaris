package appcup.uom.polaris.features.quest.di

import appcup.uom.polaris.Database
import appcup.uom.polaris.QuestQueries
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.quest.data.QuestRepositoryImpl
import appcup.uom.polaris.features.quest.domain.QuestRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class QuestModule {
    @Single
    fun provideQuestDataSource(database: Database): QuestQueries = database.questQueries

    @Single
    fun provideQuestRepository(
        questDataSource: QuestQueries,
        locationManager: LocationManager,
        userRepository: UserRepository
        ): QuestRepository =
        QuestRepositoryImpl(questDataSource, locationManager, userRepository)



}