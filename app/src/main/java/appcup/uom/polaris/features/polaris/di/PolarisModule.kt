package appcup.uom.polaris.features.polaris.di

import android.content.Context
import appcup.uom.polaris.core.domain.RoutesApi
import appcup.uom.polaris.core.domain.WeatherApi
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.data.PolarisRepositoryImpl
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import appcup.uom.polaris.features.polaris.presentation.create_journey.CreateJourneyViewModel
import appcup.uom.polaris.features.polaris.presentation.journeys.JourneysViewModel
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.WaypointSelectorViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import io.github.jan.supabase.SupabaseClient
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

    @Single
    fun providePolarisRepository(
        routesApi: RoutesApi,
        weatherApi: WeatherApi,
        supabaseClient: SupabaseClient
    ): PolarisRepository = PolarisRepositoryImpl(
        routesApi = routesApi,
        weatherApi = weatherApi,
        supabaseClient = supabaseClient
    )

    @KoinViewModel
    fun provideCreateJourneyViewModel(
        locationManager: LocationManager,
        polarisRepository: PolarisRepository
    ): CreateJourneyViewModel = CreateJourneyViewModel(
        locationManager = locationManager,
        polarisRepository = polarisRepository
    )

    @KoinViewModel
    fun provideWaypointSelectorViewModel(
        locationManager: LocationManager,
        placesClient: PlacesClient
    ): WaypointSelectorViewModel =
        WaypointSelectorViewModel(locationManager = locationManager, placesClient = placesClient)


    @KoinViewModel
    fun provideJourneysViewModel(
        polarisRepository: PolarisRepository
    ): JourneysViewModel = JourneysViewModel(
        polarisRepository = polarisRepository
    )


}