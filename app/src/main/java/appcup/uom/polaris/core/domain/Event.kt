package appcup.uom.polaris.core.domain

import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.conversational_ai.domain.Value
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallAction

sealed class Event {
    data class OnFunctionCall(
        val func: FunctionCallAction,
        val args: Value.Object,
        val onResult: (Map<String, Value>) -> Unit
    ) : Event()
    data class OnThemeChange(val appTheme: AppTheme, val onResult: (Map<String, Value>) -> Unit) :
        Event()
    data class OnCreateJourneyBottomSheetVisibilityChanged(val visible: Boolean) : Event()
    data class OnWaypointUnlocked(val message: String): Event()
    data class OnSearchPlaces(val searchQuery: String, val onResult: (Map<String, Value>) -> Unit) : Event()
    data class OnSearchNearbyPlaces(val radius: Double, val onResult: (Map<String, Value>) -> Unit) : Event()
    data class OnGetUserLocation(val onResult: (Map<String, Value>) -> Unit) : Event()
    data class OnSendWaypoint(val placeId: String, val onResult: (Map<String, Value>) -> Unit) : Event()
    data class OnGetAvailableJourneys(val onResult: (Map<String, Value>) -> Unit) : Event()
    data class OnStartJourney(val journeyId: String, val onResult: (Map<String, Value>) -> Unit) : Event()
    data class OnStopJourney(val onResult: (Map<String, Value>) -> Unit) : Event()
}