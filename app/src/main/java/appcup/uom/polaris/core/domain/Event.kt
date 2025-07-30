package appcup.uom.polaris.core.domain

import appcup.uom.polaris.core.extras.theme.SeedColor
import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.conversational_ai.domain.Value
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallAction

sealed class Event {
    data class OnFunctionCall(val func: FunctionCallAction, val args: Value.Object, val onResult: (Map<String, Value>) -> Unit): Event()
    data class OnThemeChange(val appTheme: AppTheme, val onResult: (Map<String, Value>) -> Unit): Event()
    data class OnSeedColorChange(val seedColor: SeedColor, val onResult: (Map<String, Value>) -> Unit): Event()
    data class OnAmoledModeChange(val enable: Boolean, val onResult: (Map<String, Value>) -> Unit): Event()
}