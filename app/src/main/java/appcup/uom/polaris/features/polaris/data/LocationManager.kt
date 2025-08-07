package appcup.uom.polaris.features.polaris.data

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import appcup.uom.polaris.core.domain.LatLong
import appcup.uom.polaris.core.domain.Orientation
import appcup.uom.polaris.core.domain.ResultState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import java.util.Locale

class LocationManager(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        context
    )
) {

    @SuppressLint("MissingPermission")
    fun getCoordinates(onResult: (latitude: Double?, longitude: Double?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onResult(location.latitude, location.longitude)
                } else {
                    onResult(null, null)
                }
            }
            .addOnFailureListener {
                onResult(null, null)
            }
    }

    @SuppressLint("MissingPermission")
    fun getAddress(onResult: (String?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    getAddressFromLocation(location, onResult)
                } else {
                    onResult("Location not found")
                }
            }
            .addOnFailureListener {
                onResult("Error fetching location: ${it.message}")
            }
    }

    @SuppressLint("MissingPermission")
    fun getAddressAndCoordinates(onResult: (address: String?, latitude: Double?, longitude: Double?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    getAddressFromLocation(location) { address ->
                        onResult(address, location.latitude, location.longitude)
                    }
                } else {
                    onResult("Location not found", null, null)
                }
            }
            .addOnFailureListener {
                onResult("Error fetching location: ${it.message}", null, null)
            }
    }

    private fun getAddressFromLocation(
        location: Location,
        onAddressFetched: (String?) -> Unit
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val geocoder = Geocoder(context, Locale.getDefault())
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        onAddressFetched(addresses[0].getAddressLine(0))
                    } else {
                        onAddressFetched("No address found")
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val geocoder = Geocoder(context, Locale.getDefault())

                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    onAddressFetched(addresses[0].getAddressLine(0))
                } else {
                    onAddressFetched("No address found")
                }
            }
        } catch (e: Exception) {
            onAddressFetched("Error fetching address: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdatesFlow(intervalMillis: Long = 5000L): Flow<ResultState<LatLong>> =
        callbackFlow {
            trySend(ResultState.Loading)

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalMillis
            ).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        trySend(
                            ResultState.Success(
                                LatLong(location.latitude, location.longitude)
                            )
                        )
                    } ?: trySend(ResultState.Failure("Location is null"))
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable) {
                        trySend(ResultState.Failure("Location unavailable"))
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }.flowOn(Dispatchers.IO)

    @SuppressLint("MissingPermission")
    fun getAddressUpdatesFlow(intervalMillis: Long = 5000L): Flow<ResultState<Pair<LatLong, String>>> =
        callbackFlow {
            trySend(ResultState.Loading)

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalMillis
            ).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        getAddressFromLocation(location) { address ->
                            trySend(
                                ResultState.Success(
                                    LatLong(location.latitude, location.longitude) to (address
                                        ?: "Unknown address")
                                )
                            )
                        }
                    } ?: trySend(ResultState.Failure("Location is null"))
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable) {
                        trySend(ResultState.Failure("Location unavailable"))
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }.flowOn(Dispatchers.IO)

    @OptIn(FlowPreview::class)
    fun getOrientationFlow(): Flow<Orientation> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationSensor == null) {
            close() // No sensor available
            return@callbackFlow
        }

        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                val azimuth =
                    (Math.toDegrees(orientationAngles[0].toDouble()).toFloat() + 360f) % 360f
                val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                trySend(Orientation(azimuth, pitch, roll))
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        sensorManager.registerListener(
            listener,
            rotationSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.flowOn(Dispatchers.IO)


}