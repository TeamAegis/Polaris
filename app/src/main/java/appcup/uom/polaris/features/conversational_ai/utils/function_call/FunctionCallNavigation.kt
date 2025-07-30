package appcup.uom.polaris.features.conversational_ai.utils.function_call

import androidx.navigation3.runtime.NavBackStack
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.presentation.components.BottomBarItem
import appcup.uom.polaris.features.conversational_ai.domain.Value
import coil3.toUri
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

interface FunctionCallNavigation {
    fun getScreenDetails(): Value
}

@Suppress("UNCHECKED_CAST")
class FunctionCallNavigationHandler(
    private val navBackStack: NavBackStack
) {

    fun getCurrentScreenDetails(args: Value.Object, onResult: (Map<String, Value>) -> Unit) {
        try {
            val screen = getScreen(args)
            onResult(mapOf("details" to screen.getScreenDetails()))
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure, screen not found")))
        }
    }

    fun getCurrentLocation(onResult: (Map<String, Value>) -> Unit) {
        onResult(mapOf("location" to Value.Str(navBackStack.last().toString())))
    }

    fun handleBackNavigation(
        onResult: (Map<String, Value>) -> Unit
    ) {
        val result = navBackStack.removeLastOrNull()
        onResult(mapOf("result" to Value.Str(if (result != null) "success" else "failure")))
    }


    @OptIn(InternalSerializationApi::class)
    fun handleNavigation(
        args: Value.Object,
        onResult: (Map<String, Value>) -> Unit
    ) {

        val screenName = try {
            getScreenName(args)
        } catch (_: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure, screen not found")))
            return
        }
        val navigationArguments = getNavigationArguments(args)
        val screens: Collection<KClass<Screen>> =
            Screen::class.nestedClasses as Collection<KClass<Screen>>

        try {
            screens.forEach { kClass ->
                val screenInstance = try {
                    if (navigationArguments == null) {
                        throw IllegalArgumentException("Navigation arguments not found")
                    } else {
                        val jsonString =
                            Json.encodeToString(Value.serializer(), navigationArguments)
                        Json.decodeFromString(kClass.serializer(), jsonString.toUri().toString())
                    }
                } catch (_: IllegalArgumentException) {
                    try {
                        kClass.objectInstance
                    } catch (_: Exception) {
                        null
                    }
                }
                if (screenInstance != null && screenInstance::class.simpleName == screenName) {
                    val isCurrentDestOnBackStack = navBackStack.last() == screenInstance::class
                    val isBottomBarItem = BottomBarItem.entries.any { it.screen == screenInstance }
                    if (isBottomBarItem) {
                        if (isCurrentDestOnBackStack) {
                            navBackStack.removeLastOrNull()
                            navBackStack.add(screenInstance)
                            onResult(mapOf("result" to Value.Str("success")))
                            return@forEach
                        }
                        navBackStack.clear()
                        navBackStack.add(Screen.Home)
                        navBackStack.add(screenInstance)
                        onResult(mapOf("result" to Value.Str("success")))
                    } else if (navBackStack.last() == screenInstance::class) {
                        navBackStack.removeLastOrNull()
                        navBackStack.add(screenInstance)
                        onResult(mapOf("result" to Value.Str("success")))
                    } else {
                        navBackStack.add(screenInstance)
                        onResult(mapOf("result" to Value.Str("success")))
                    }
                    return@forEach
                }
            }
            onResult(mapOf("result" to Value.Str("failure, screen not found")))
        } catch (e: IllegalArgumentException) {
            onResult(mapOf("result" to Value.Str("failure, ${e.message}")))
        }

    }

    private fun getScreen(args: Value.Object): Screen {
        val screens: Collection<KClass<Screen>> =
            Screen::class.nestedClasses as Collection<KClass<Screen>>

        val screenName =
            getScreenName(args) ?: throw IllegalArgumentException("Screen name not found")

        screens.forEach { kClass ->
            val screenInstance = try {
                kClass.createInstance() as? Screen
            } catch (_: IllegalArgumentException) {
                try {
                    kClass.objectInstance
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
            if (screenInstance != null && screenInstance::class.simpleName == screenName) {
                return screenInstance
            }
        }
        throw IllegalArgumentException("Screen not found")
    }

    private fun getScreenName(args: Value.Object): String? {
        val screen = args.value.getOrElse("screen") { null }
        return if (screen is Value.Str) screen.value else null
    }

    private fun getNavigationArguments(args: Value.Object): Value.Object? {
        val navigationArguments = args.value.getOrElse("navigation_arguments") { null }
        return navigationArguments as? Value.Object
    }
}


