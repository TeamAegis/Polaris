package appcup.uom.polaris.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import appcup.uom.polaris.Database
import appcup.uom.polaris.core.data.AppSecrets
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.createPreferencesDataStore
import appcup.uom.polaris.core.domain.MemoryRepository
import appcup.uom.polaris.core.presentation.app.AppViewModel
import appcup.uom.polaris.core.presentation.map.MapViewModel
import appcup.uom.polaris.core.presentation.memories.MemoriesViewModel
import appcup.uom.polaris.core.presentation.more.MoreViewModel
import appcup.uom.polaris.core.presentation.settings.SettingsViewModel
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.domain.FragmentsRepository
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import appcup.uom.polaris.features.quest.domain.QuestRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AppModule {
    @Single
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }


    @Single
    fun provideDataStore(): DataStore<Preferences> = createPreferencesDataStore()

    @Single
    fun supabase() = createSupabaseClient(
        supabaseUrl = AppSecrets.supabaseUrl,
        supabaseKey = AppSecrets.supabaseKey
    ) {
        install(Auth) {
            host = Constants.DEEPLINK_HOST_AUTH
            scheme = Constants.DEEPLINK_SCHEMA
        }
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }

    @Single
    fun provideDatabase(
        context: Context
    ) = Database(AndroidSqliteDriver(Database.Schema, context, "polaris.db"))

    @KoinViewModel
    fun appViewModel(
        permissionBridge: PermissionBridge,
        fragmentsRepository: FragmentsRepository,
        locationManager: LocationManager
    ) =
        AppViewModel(
            permissionBridge = permissionBridge,
            fragmentsRepository = fragmentsRepository,
            locationManager = locationManager
        )

    @KoinViewModel
    fun settingsViewModel(userRepository: UserRepository, prefs: DataStore<Preferences>) =
        SettingsViewModel(userRepository = userRepository, prefs = prefs)

    @KoinViewModel
    fun mapViewModel(
        locationManager: LocationManager,
        polarisRepository: PolarisRepository,
        questRepository: QuestRepository
    ) =
        MapViewModel(
            locationManager = locationManager,
            polarisRepository = polarisRepository,
            questRepository = questRepository
        )

    @KoinViewModel
    fun memoriesViewModel(memoryRepository: MemoryRepository) =
        MemoriesViewModel(memoryRepository = memoryRepository)

    @KoinViewModel
    fun moreViewModel(userRepository: UserRepository, questRepository: QuestRepository) =
        MoreViewModel(userRepository = userRepository, questRepository = questRepository)

}