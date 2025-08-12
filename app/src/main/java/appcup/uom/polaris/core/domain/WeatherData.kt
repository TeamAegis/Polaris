package appcup.uom.polaris.core.domain

data class WeatherData(
    val iconUrl: String,
    val condition: String,
    val temperature: Double,
    val feelsLike: Double,
    val windSpeed: Int,
    val windDir: String,
    val humidity: Int,
    val precipitation: Int,
    val isDaytime: Boolean
)