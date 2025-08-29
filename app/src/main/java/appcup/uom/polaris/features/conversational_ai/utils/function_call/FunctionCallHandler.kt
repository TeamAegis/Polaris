package appcup.uom.polaris.features.conversational_ai.utils.function_call


import androidx.navigation3.runtime.NavBackStack
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.core.domain.Event.OnThemeChange
import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.conversational_ai.domain.Value

class FunctionCallHandler(
    navBackStack: NavBackStack
) {
    private val functionCallNavigationHandler = FunctionCallNavigationHandler(navBackStack)
    suspend fun handleFunctionCall(
        func: FunctionCallAction,
        args: Value.Object,
        onResult: (Map<String, Value>) -> Unit
    ) {
        when (func) {
            FunctionCallAction.GET_SCREEN_DETAILS -> {
                functionCallNavigationHandler.getCurrentScreenDetails(args, onResult)
            }

            FunctionCallAction.GET_CURRENT_LOCATION_IN_APP -> {
                functionCallNavigationHandler.getCurrentLocation(onResult)
            }

            FunctionCallAction.NAVIGATE_TO_SCREEN -> {
                functionCallNavigationHandler.handleNavigation(args, onResult)
            }

            FunctionCallAction.NAVIGATE_BACK -> {
                functionCallNavigationHandler.handleBackNavigation(onResult)
            }

            FunctionCallAction.CHANGE_THEME -> {
                changeTheme(args, onResult)
            }

            FunctionCallAction.SEARCH_PLACES -> {
                onSearchPlaces(args, onResult)
            }

            FunctionCallAction.SEARCH_NEARBY_PLACES -> {
                onSearchNearbyPlaces(args, onResult)
            }

            FunctionCallAction.GET_USER_LOCATION -> {
                onGetUserLocation(onResult)
            }

            FunctionCallAction.SEND_WAYPOINT -> {
                onSendWaypoint(args, onResult)
            }
            FunctionCallAction.GET_AVAILABLE_JOURNEYS -> {
                onGetAvailableJourneys(onResult)
            }
            FunctionCallAction.START_JOURNEY -> {
                onStartJourney(args, onResult)
            }
            FunctionCallAction.STOP_JOURNEY -> {
                onStopJourney(onResult)
            }
        }
    }

    private suspend fun changeTheme(args: Value.Object, onResult: (Map<String, Value>) -> Unit) {
        try {
            if (args.value.containsKey("theme") && args.value["theme"] is Value.Str) {
                EventBus.emit(
                    OnThemeChange(
                        AppTheme.valueOf((args.value["theme"] as Value.Str).value),
                        onResult
                    )
                )
            } else {
                onResult(mapOf("result" to Value.Str("failure")))
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun onSearchPlaces(args: Value.Object, onResult: (Map<String, Value>) -> Unit) {
        try {
            if (args.value.containsKey("search_query") && args.value["search_query"] is Value.Str) {
                EventBus.emit(
                    Event.OnSearchPlaces((args.value["search_query"] as Value.Str).value, onResult)
                )
            } else {
                onResult(mapOf("result" to Value.Str("failure")))
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun onSearchNearbyPlaces(
        args: Value.Object,
        onResult: (Map<String, Value>) -> Unit
    ) {
        try {
            if (args.value.containsKey("radius") && args.value["radius"] is Value.Number) {
                EventBus.emit(
                    Event.OnSearchNearbyPlaces(
                        (args.value["radius"] as Value.Number).value,
                        onResult
                    )
                )
            } else {
                onResult(mapOf("result" to Value.Str("failure")))
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun onGetUserLocation(
        onResult: (Map<String, Value>) -> Unit
    ) {
        try {
            EventBus.emit(
                Event.OnGetUserLocation(onResult)
            )
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun onSendWaypoint(args: Value.Object, onResult: (Map<String, Value>) -> Unit) {
        try {
            if (args.value.containsKey("place_id") && args.value["place_id"] is Value.Str) {
                EventBus.emit(
                    Event.OnSendWaypoint((args.value["place_id"] as Value.Str).value, onResult)
                )
            } else {
                onResult(mapOf("result" to Value.Str("failure")))
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun onGetAvailableJourneys(onResult: (Map<String, Value>) -> Unit) {
        try {
            EventBus.emit(
                Event.OnGetAvailableJourneys(onResult)
            )
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun onStartJourney(args: Value.Object, onResult: (Map<String, Value>) -> Unit) {
        try {
            if (args.value.containsKey("journey_id") && args.value["journey_id"] is Value.Str) {
                EventBus.emit(
                    Event.OnStartJourney((args.value["journey_id"] as Value.Str).value, onResult)
                    )
            } else {
                onResult(mapOf("result" to Value.Str("failure")))
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun onStopJourney(onResult: (Map<String, Value>) -> Unit) {
        try {
            EventBus.emit(
                Event.OnStopJourney(onResult)
            )
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }
}

