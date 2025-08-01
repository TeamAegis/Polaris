package appcup.uom.polaris.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import appcup.uom.polaris.core.data.AppSecrets
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.createPreferencesDataStore
import appcup.uom.polaris.core.presentation.app.AppViewModel
import appcup.uom.polaris.core.presentation.home.HomeViewModel
import appcup.uom.polaris.core.presentation.map.MapViewModel
import appcup.uom.polaris.core.presentation.memories.MemoriesViewModel
import appcup.uom.polaris.core.presentation.more.MoreViewModel
import appcup.uom.polaris.core.presentation.settings.SettingsViewModel
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import io.github.jan.supabase.SupabaseClient
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

    @KoinViewModel
    fun appViewModel(supabaseClient: SupabaseClient, permissionBridge: PermissionBridge) =
        AppViewModel(supabaseClient = supabaseClient, permissionBridge = permissionBridge)

    @KoinViewModel
    fun settingsViewModel(userRepository: UserRepository, prefs: DataStore<Preferences>) =
        SettingsViewModel(userRepository = userRepository, prefs = prefs)

    @KoinViewModel
    fun homeViewModel(userRepository: UserRepository) =
        HomeViewModel(userRepository = userRepository)

    @KoinViewModel
    fun mapViewModel(userRepository: UserRepository) =
        MapViewModel(userRepository = userRepository)

    @KoinViewModel
    fun memoriesViewModel(userRepository: UserRepository) =
        MemoriesViewModel(userRepository = userRepository)

    @KoinViewModel
    fun moreViewModel(userRepository: UserRepository) =
        MoreViewModel(userRepository = userRepository)

}