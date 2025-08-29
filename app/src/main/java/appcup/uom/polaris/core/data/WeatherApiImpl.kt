package appcup.uom.polaris.core.data

import android.util.Log
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.GoogleWeatherResponse
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.domain.WeatherApi
import appcup.uom.polaris.core.domain.WeatherData
import appcup.uom.polaris.core.domain.toWeatherData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

class WeatherApiImpl(
    private val httpClient: HttpClient
) : WeatherApi {
    val weatherUrl = "https://weather.googleapis.com/v1/currentConditions:lookup"

    override suspend fun getWeather(
        lat: Double,
        lon: Double
    ): Result<WeatherData, DataError.Remote> {
        return try {
            Result.Success(httpClient.get(weatherUrl) {
                parameter("key", AppSecrets.mapsApiKey)
                parameter("location.latitude", lat)
                parameter("location.longitude", lon)
            }.body<GoogleWeatherResponse>().toWeatherData())
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }

    }
}