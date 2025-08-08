package appcup.uom.polaris.features.polaris.di

import android.content.Context
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.presentation.create_journey.CreateJourneyViewModel
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.WaypointSelectorViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PolarisModule {
    @Single
    fun provideLocationManager(
        context: Context,
        placesClient: PlacesClient
    ): LocationManager = LocationManager(context = context, placesClient = placesClient)

    @Single
    fun providePlacesClient(context: Context): PlacesClient = Places.createClient(context)

    @KoinViewModel
    fun provideCreateJourneyViewModel(
        locationManager: LocationManager
    ): CreateJourneyViewModel = CreateJourneyViewModel(
        locationManager = locationManager
    )

    @KoinViewModel
    fun provideWaypointSelectorViewModel(
        locationManager: LocationManager,
        placesClient: PlacesClient
    ): WaypointSelectorViewModel = WaypointSelectorViewModel(locationManager = locationManager, placesClient = placesClient)


}