package appcup.uom.polaris.core.presentation.quest.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.LocationRestriction
import com.google.android.libraries.places.api.net.kotlin.searchNearbyRequest
import com.google.android.libraries.places.api.net.kotlin.awaitSearchNearby
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.kotlin.circularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.libraries.places.api.net.SearchNearbyResponse
import kotlinx.coroutines.*
import kotlin.coroutines.resumeWithException

private const val TAG = "NearbySearch"
var placesClient: PlacesClient? = null


val cat = listOf(
    "restaurant",
    "shopping_mall",
    "tourist_attraction",
    "hiking",
    "museum",
    "park"
)

@SuppressLint("MissingPermission")
fun getCurrentLocation(context : Context, callback: (LatLng?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        callback(null)
        return
    }
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
            if (location != null) {
                callback(LatLng(location.latitude, location.longitude))
            } else {
                callback(null)
            }
        }
}

@SuppressLint("MissingPermission")
fun nearbySearchPlaces(context: Context, onResult: (List<Place>) -> Unit) {
    getCurrentLocation(context) { latLng ->
        if (latLng == null) {
            Log.w(TAG, "Location unavailable")
            return@getCurrentLocation
        }

        val restriction = circularBounds(
            center = LatLng(latLng.latitude, latLng.longitude),
            radius = 5000.0 // adjust radius as needed
        )
        val request = searchNearbyRequest(
            locationRestriction = restriction,
            placeFields = listOf(
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.TYPES,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI
            ),
            actions = null
        )


        placesClient?.searchNearby(request)
            ?.addOnSuccessListener { response: SearchNearbyResponse ->
                onResult(response.places)
            }
            ?.addOnFailureListener { exception: Exception ->
                Log.e(TAG, "Error searching nearby places", exception)
            }
    }
}

