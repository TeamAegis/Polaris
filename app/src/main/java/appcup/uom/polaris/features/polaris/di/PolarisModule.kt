package appcup.uom.polaris.features.polaris.di

import appcup.uom.polaris.features.polaris.presentation.create_journey.CreateJourneyViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class PolarisModule {
    @KoinViewModel
    fun provideCreateJourneyViewModel(): CreateJourneyViewModel = CreateJourneyViewModel()
}