package appcup.uom.polaris.features.polaris.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.domain.LatLong
import appcup.uom.polaris.core.domain.ResultState
import appcup.uom.polaris.features.polaris.domain.Waypoint
import com.google.android.gms.location.DeviceOrientation
import com.google.android.gms.location.DeviceOrientationListener
import com.google.android.gms.location.DeviceOrientationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.FusedOrientationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.EncodedPolyline
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.SearchAlongRouteParameters
import com.google.android.libraries.places.api.model.kotlin.circularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.kotlin.searchNearbyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.uuid.ExperimentalUuidApi

class LocationManager(
    private val context: Context,
    private val placesClient: PlacesClient,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        context
    ),
    private val fusedOrientationProviderClient: FusedOrientationProviderClient =
        LocationServices.getFusedOrientationProviderClient(context)
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getNearbyPlacesAlongRoute(
        searchQuery: String,
        polyline: String
    ): List<Waypoint> {
        return try {
            val result = placesClient.searchByText(
                SearchByTextRequest.builder(
                    searchQuery,
                    listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.RATING,
                        Place.Field.USER_RATINGS_TOTAL,
                        Place.Field.CURRENT_OPENING_HOURS,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.WEBSITE_URI,
                        Place.Field.LAT_LNG,
                        Place.Field.TYPES,
                    )
                ).setSearchAlongRouteParameters(
                    SearchAlongRouteParameters.newInstance(
                        EncodedPolyline.newInstance(polyline)
                    )
                )
                    .setMaxResultCount(3)
                    .setLocationRestriction(RectangularBounds.newInstance(Constants.MAP_LAT_LNG_BOUNDS))
                    .build()
            ).await()

            result.places.map { place ->
                Waypoint(
                    placeId = place.id,
                    name = place.displayName ?: "Unknown",
                    address = place.formattedAddress,
                    rating = place.rating,
                    userRatingsTotal = place.rating,
                    openNow = isPlaceOpenNow(place.currentOpeningHours),
                    phoneNumber = place.internationalPhoneNumber
                        ?: place.nationalPhoneNumber,
                    websiteUri = place.websiteUri,
                    latitude = place.location?.latitude ?: 0.0,
                    longitude = place.location?.longitude ?: 0.0,
                    placeType = place.placeTypes ?: emptyList()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @SuppressLint("NewApi")
    fun getWaypointByPlaceId(placeId: String, onResult: (waypoint: Waypoint?) -> Unit) {
        placesClient.fetchPlace(
            FetchPlaceRequest.builder(
                placeId, listOf(
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.RATING,
                    Place.Field.USER_RATINGS_TOTAL,
                    Place.Field.CURRENT_OPENING_HOURS,
                    Place.Field.PHONE_NUMBER,
                    Place.Field.WEBSITE_URI,
                    Place.Field.LAT_LNG
                )
            ).build()
        ).addOnSuccessListener { placeResponse ->
            val waypoint = Waypoint(
                placeId = placeId,
                name = placeResponse.place.displayName ?: "Unknown",
                address = placeResponse.place.formattedAddress,
                rating = placeResponse.place.rating,
                userRatingsTotal = placeResponse.place.rating,
                openNow = isPlaceOpenNow(placeResponse.place.currentOpeningHours),
                phoneNumber = placeResponse.place.internationalPhoneNumber
                    ?: placeResponse.place.nationalPhoneNumber,
                websiteUri = placeResponse.place.websiteUri,
                latitude = placeResponse.place.location?.latitude ?: 0.0,
                longitude = placeResponse.place.location?.longitude ?: 0.0
            )
            onResult(waypoint)
        }.addOnFailureListener {
            onResult(null)
        }
    }

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

    @SuppressLint("NewApi")
    fun getAddressFromLocation(
        latitude: Double,
        longitude: Double,
        onAddressFetched: (String?) -> Unit
    ) {

        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
            if (addresses.isNotEmpty()) {
                onAddressFetched(addresses[0].getAddressLine(0))
            } else {
                onAddressFetched("No address found")
            }
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
    fun getOrientationFlow(): Flow<DeviceOrientation> = callbackFlow {
        val request =
            DeviceOrientationRequest.Builder(DeviceOrientationRequest.OUTPUT_PERIOD_DEFAULT).build()
        val executor = Executors.newSingleThreadExecutor()

        val listener = DeviceOrientationListener { p0 -> trySend(p0) }

        fusedOrientationProviderClient.requestOrientationUpdates(
            request,
            executor,
            listener
        )

        awaitClose {
            fusedOrientationProviderClient.removeOrientationUpdates(listener)
        }
    }.flowOn(Dispatchers.IO)


    @OptIn(ExperimentalUuidApi::class)
    @SuppressLint("MissingPermission")
    suspend fun nearbySearchPlaces(): List<Waypoint> {
        val result = fusedLocationClient.lastLocation.await()

        val restriction = circularBounds(
            center = LatLng(result.latitude, result.longitude),
            radius = 5000.0
        )
        val request = searchNearbyRequest(
            locationRestriction = restriction,
            placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.CURRENT_OPENING_HOURS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.LAT_LNG
            ),
            actions = null
        )

        val places = placesClient.searchNearby(request).await()
        return places.places.map { place ->
            Waypoint(
                placeId = place.id,
                name = place.displayName ?: "Unknown",
                address = place.formattedAddress,
                rating = place.rating,
                userRatingsTotal = place.rating,
                openNow = isPlaceOpenNow(place.currentOpeningHours),
                phoneNumber = place.internationalPhoneNumber
                    ?: place.nationalPhoneNumber,
                websiteUri = place.websiteUri,
                latitude = place.location?.latitude ?: 0.0,
                longitude = place.location?.longitude ?: 0.0
            )
        }

    }

}