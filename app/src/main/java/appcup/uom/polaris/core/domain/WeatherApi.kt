package appcup.uom.polaris.core.domain

interface WeatherApi {
    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherData, DataError.Remote>
}