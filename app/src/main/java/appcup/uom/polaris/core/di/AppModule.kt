package appcup.uom.polaris.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import appcup.uom.polaris.core.data.AppSecrets
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.HttpClientEngineFactory
import appcup.uom.polaris.core.data.createPreferencesDataStore
import appcup.uom.polaris.core.presentation.app.AppViewModel
import appcup.uom.polaris.core.presentation.home.HomeViewModel
import appcup.uom.polaris.core.presentation.more.MoreViewModel
import appcup.uom.polaris.core.presentation.settings.SettingsViewModel
import appcup.uom.polaris.features.auth.domain.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AppModule {

    @Single
    fun httpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) {

    }

    @Factory
    fun httpClientEngine(): HttpClientEngine = HttpClientEngineFactory().getHttpEngine()

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
    }

    @KoinViewModel
    fun appViewModel(supabaseClient: SupabaseClient) = AppViewModel(supabaseClient = supabaseClient)

    @KoinViewModel
    fun settingsViewModel(userRepository: UserRepository, prefs: DataStore<Preferences>) =
        SettingsViewModel(userRepository = userRepository, prefs = prefs)

    @KoinViewModel
    fun homeViewModel(userRepository: UserRepository) =
        HomeViewModel(userRepository = userRepository)

    @KoinViewModel
    fun moreViewModel(userRepository: UserRepository) =
        MoreViewModel(userRepository = userRepository)

}