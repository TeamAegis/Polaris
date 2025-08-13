package appcup.uom.polaris.features.conversational_ai.utils.function_call


import androidx.navigation3.runtime.NavBackStack
import appcup.uom.polaris.core.data.EventBus
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

            FunctionCallAction.GET_CURRENT_LOCATION -> {
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
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

}

