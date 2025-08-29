package appcup.uom.polaris.features.polaris.di

import android.content.Context
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.domain.MemoryRepository
import appcup.uom.polaris.core.domain.RoutesApi
import appcup.uom.polaris.core.domain.WeatherApi
import appcup.uom.polaris.features.polaris.data.FragmentsRepositoryImpl
import appcup.uom.polaris.features.polaris.data.LocationManager
import appcup.uom.polaris.features.polaris.data.PolarisRepositoryImpl
import appcup.uom.polaris.features.polaris.domain.FragmentsRepository
import appcup.uom.polaris.features.polaris.domain.PolarisRepository
import appcup.uom.polaris.features.polaris.presentation.create_journey.CreateJourneyViewModel
import appcup.uom.polaris.features.polaris.presentation.fragments.FragmentsViewModel
import appcup.uom.polaris.features.polaris.presentation.journey_details.JourneyDetailsViewModel
import appcup.uom.polaris.features.polaris.presentation.journeys.JourneysViewModel
import appcup.uom.polaris.features.polaris.presentation.waypoint_selector.WaypointSelectorViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.generationConfig
import io.github.jan.supabase.SupabaseClient
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
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
        supabaseClient: SupabaseClient,
        locationManager: LocationManager
    ): PolarisRepository = PolarisRepositoryImpl(
        routesApi = routesApi,
        weatherApi = weatherApi,
        supabaseClient = supabaseClient,
        locationManager = locationManager,
        firebaseWaypointAiFunctionCallChat = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = Constants.GEMINI_API_MODEL,
                tools = listOf(
                    Tool.functionDeclarations(
                        listOf(
                            FunctionDeclaration(
                                name = "getNearbyPlacesAlongRoute",
                                description = "Retrieve a list of nearby places located along a specified route based on a search query.",
                                parameters = mapOf(
                                    "searchQuery" to Schema.string(
                                        "The text-based query used to search for places along the route. Examples: 'restaurants', 'beaches', 'museums' and more."
                                    )
                                )
                            )
                        )
                    )
                )
            ).startChat(),
        firebaseWaypointGenerativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = Constants.GEMINI_API_MODEL,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = Schema.obj(
                        mapOf(
                            "title" to Schema.string(),
                            "description" to Schema.string(),
                            "waypoints" to Schema.array(
                                Schema.obj(
                                    mapOf(
                                        "id" to Schema.string(),
                                        "placeId" to Schema.string(),
                                        "name" to Schema.string(),
                                        "address" to Schema.string(),
                                        "rating" to Schema.double(),
                                        "userRatingsTotal" to Schema.double(),
                                        "openNow" to Schema.boolean(),
                                        "phoneNumber" to Schema.string(),
                                        "websiteUri" to Schema.string(),
                                        "latitude" to Schema.double(),
                                        "longitude" to Schema.double(),
                                        "waypointType" to Schema.enumeration(
                                            listOf(
                                                "START",
                                                "INTERMEDIATE",
                                                "END",
                                                "CURRENT_LOCATION",
                                                "FRAGMENT",
                                                "UNLOCKED_WAYPOINT",
                                                "QUEST_WAYPOINT"
                                            )
                                        ),
                                        "placeType" to Schema.array(Schema.string())
                                    )
                                )
                            )
                        )
                    )
                }
            )
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

    @KoinViewModel
    fun provideJourneyDetailViewModel(
        @InjectedParam journeyId: String,
        polarisRepository: PolarisRepository,
        memoryRepository: MemoryRepository
    ): JourneyDetailsViewModel = JourneyDetailsViewModel(
        journeyId = journeyId,
        polarisRepository = polarisRepository,
        memoryRepository = memoryRepository
    )

    @Single
    fun provideFragmentsRepository(
        supabaseClient: SupabaseClient
    ): FragmentsRepository = FragmentsRepositoryImpl(
        supabaseClient = supabaseClient
    )

    @KoinViewModel
    fun provideFragmentsViewModel(
        @InjectedParam publicWaypointId: String,
        fragmentsRepository: FragmentsRepository
    ): FragmentsViewModel = FragmentsViewModel(
        publicWaypointId = publicWaypointId,
        fragmentsRepository = fragmentsRepository
    )



}