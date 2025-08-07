package appcup.uom.polaris.core.domain

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Failure(val message: String) : ResultState<Nothing>()
}

data class LatLong(val latitude: Double, val longitude: Double)
