package appcup.uom.polaris.features.conversational_ai.data

import ai.pipecat.client.types.Value
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallAction

object ConversationalAITools {
    val screens = Screen::class.nestedClasses.map { Value.Str(it.simpleName!!) }.toTypedArray()

    val navigateToScreenTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.NAVIGATE_TO_SCREEN.name),
            "description" to Value.Str("A navigation tool that navigate to the desired screen within the application. Ensures that the you take into context the current screen that you in, so as to give the user a visual guide of where the screen is."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "screen" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The name of the destination screen to navigate to. This must be one of the available screens in the application based on the enum."),
                                    "enum" to Value.Array(
                                        values = screens
                                    )
                                )
                            ),
                            "navigation_arguments" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("OBJECT"),
                                    "description" to Value.Str("Optional argument(s) provided to pass to the destination screen during navigation. This will be used as context and data to to auto-fill for the destination screen."),
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("screen")
                        )
                    )
                )
            )
        )
    )
    val getScreenDetailsTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.GET_SCREEN_DETAILS.name),
            "description" to Value.Str("Retrieves details available about a specific screen in the application. This is useful for understanding the content and functionality of a particular screen without navigating to it."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "screen" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The name of the screen to get details for. This must be one of the available screens in the application."),
                                    "enum" to Value.Array(
                                        values = screens
                                    )
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("screen")
                        )
                    )
                )
            )
        )
    )
    val getCurrentLocationTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.GET_CURRENT_LOCATION_IN_APP.name),
            "description" to Value.Str("Gets the current screen that the user is currently viewing in the application, This enable in providing context-aware assistance or information."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object()
                )
            )
        )
    )
    val navigateBackTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.NAVIGATE_BACK.name),
            "description" to Value.Str("Navigates the user to the previous screen in the application's navigation stack. Always check the current screen it is in currently. This is enables the user to go back to a prior view or step."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object()
                )
            )
        )
    )

    val changeThemeTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.CHANGE_THEME.name),
            "description" to Value.Str("Must navigate to Settings Screen first. Changes the application's theme. This allows the user to switch between system, light, or dark themes."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "theme" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The theme to apply. Must be one of the available themes: SYSTEM, LIGHT, or DARK."),
                                    "enum" to Value.Array(
                                        values = AppTheme.entries.map { Value.Str(it.name) }
                                            .toTypedArray()
                                    )
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("theme")
                        )
                    )
                )
            )
        )
    )


    val toolsArray = arrayOf(
        navigateToScreenTool,
        getCurrentLocationTool,
        changeThemeTool,
        navigateBackTool,
        getScreenDetailsTool,
    )

}