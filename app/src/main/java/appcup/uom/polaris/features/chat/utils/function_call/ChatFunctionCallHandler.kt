package appcup.uom.polaris.features.chat.utils.function_call


import appcup.uom.polaris.features.conversational_ai.domain.Value
import kotlinx.serialization.json.JsonObject

class ChatFunctionCallHandler() {

    suspend fun handleFunctionCall(
        func: ChatFunctionCallAction,
        args: JsonObject,
        onResult: (Map<String, Value>) -> Unit
    ) {
        when (func) {
            else -> {}
        }
    }
}

