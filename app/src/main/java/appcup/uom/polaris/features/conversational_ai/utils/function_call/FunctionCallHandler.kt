package appcup.uom.polaris.features.conversational_ai.utils.function_call


import androidx.navigation3.runtime.NavBackStack
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.domain.Event.OnAmoledModeChange
import appcup.uom.polaris.core.domain.Event.OnSeedColorChange
import appcup.uom.polaris.core.domain.Event.OnThemeChange
import appcup.uom.polaris.core.extras.theme.SeedColor
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

            FunctionCallAction.CHANGE_SEED_COLOR -> {
                changeSeedColor(args, onResult)
            }

            FunctionCallAction.ENABLE_AMOLED_MODE -> {
                enableAmoledMode(args, onResult)
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

    private suspend fun changeSeedColor(args: Value.Object, onResult: (Map<String, Value>) -> Unit) {
        try {
            if (args.value.containsKey("seed_color") && args.value["seed_color"] is Value.Str) {
                EventBus.emit(
                    OnSeedColorChange(
                        SeedColor.valueOf((args.value["seed_color"] as Value.Str).value),
                        onResult
                    )
                )
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }
    }

    private suspend fun enableAmoledMode(args: Value.Object, onResult: (Map<String, Value>) -> Unit) {
        try {
            if (args.value.containsKey("enable") && args.value["enable"] is Value.Bool) {
                EventBus.emit(
                    OnAmoledModeChange(
                        (args.value["enable"] as Value.Bool).value,
                        onResult
                    )
                )
            }
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure")))
        }

    }


}

