package appcup.uom.polaris.core.di

import appcup.uom.polaris.core.data.RoutesApiImpl
import appcup.uom.polaris.core.domain.RoutesApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class NetworkModule {
    @Single
    fun provideHttpClient(
        json: Json
    ): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    @Single
    fun provideRoutesApi(
        httpClient: HttpClient
    ): RoutesApi = RoutesApiImpl(httpClient)
}