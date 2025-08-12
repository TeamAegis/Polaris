package appcup.uom.polaris.core.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class GoogleWeatherResponse(
    val currentTime: String,
    val isDaytime: Boolean,
    @SerialName("weatherCondition") val weatherCondition: WeatherCondition,
    val temperature: Temperature,
    val feelsLikeTemperature: Temperature,
    val relativeHumidity: Int,
    val uvIndex: Int,
    val precipitation: Precipitation,
    val wind: Wind
) {
    @Serializable
    data class WeatherCondition(
        val iconBaseUri: String,
        val description: Description
    ) {
        @Serializable
        data class Description(
            val text: String
        )
    }

    @Serializable
    data class Temperature(
        val degrees: Double
    )

    @Serializable
    data class Precipitation(
        val probability: Probability
    ) {
        @Serializable
        data class Probability(
            val percent: Int
        )
    }

    @Serializable
    data class Wind(
        val direction: Direction,
        val speed: Speed
    ) {
        @Serializable
        data class Direction(
            val cardinal: String
        )
        @Serializable
        data class Speed(
            val value: Int
        )
    }
}

fun GoogleWeatherResponse.toWeatherData(): WeatherData {
    return WeatherData(
        iconUrl = "${this.weatherCondition.iconBaseUri}.png",
        condition = this.weatherCondition.description.text,
        temperature = this.temperature.degrees,
        feelsLike = this.feelsLikeTemperature.degrees,
        windSpeed = this.wind.speed.value,
        windDir = this.wind.direction.cardinal,
        humidity = this.relativeHumidity,
        precipitation = this.precipitation.probability.percent,
        isDaytime = this.isDaytime
    )
}