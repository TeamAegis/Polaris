package appcup.uom.polaris.features.conversational_ai.data

import ai.pipecat.client.types.Value
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.extras.theme.SeedColor
import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallAction

object ConversationalAITools {
    val screens = Screen::class.nestedClasses.map { Value.Str(it.simpleName!!) }.toTypedArray()

    val navigateToScreenTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.NAVIGATE_TO_SCREEN.name),
            "description" to Value.Str("Navigates the user to a specific screen within the application. Before navigating, check the current screen you are in and also the details of each screen being navigated to. This is useful for guiding the user through the app or directly accessing features based on their request."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "screen" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The name of the screen to navigate to. This must be one of the available screens in the application."),
                                    "enum" to Value.Array(
                                        values = screens
                                    )
                                )
                            ),
                            "navigation_arguments" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("OBJECT"),
                                    "description" to Value.Str("Optional arguments to pass to the screen during navigation. This can be used to provide context or data to the destination screen."),
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
            "description" to Value.Str("Retrieves details about a specific screen in the application. This is useful for understanding the content and functionality of a particular screen without navigating to it."),
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
            "name" to Value.Str(FunctionCallAction.GET_CURRENT_LOCATION.name),
            "description" to Value.Str("Gets the current location of the user within the application, specifically the current screen they are viewing. This helps in providing context-aware assistance or information."),
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
            "description" to Value.Str("Navigates the user to the previous screen in the application's navigation stack. Always check the current screen it is in currently. This is useful for allowing the user to go back to a prior view or step."),
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

    val changeSeedColorTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.CHANGE_SEED_COLOR.name),
            "description" to Value.Str("Must navigate to Settings Screen first. Changes the application's seed color/theme color. This allows the user to switch between different color palettes for the app's UI."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "seed_color" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The seed color to apply. This must be one of the available seed colors."),
                                    "enum" to Value.Array(
                                        values = SeedColor.entries.map { Value.Str(it.name) }
                                            .toTypedArray()
                                    )
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("seed_color")
                        )
                    )
                )
            )
        )
    )

    val enableAmoledModeTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.ENABLE_AMOLED_MODE.name),
            "description" to Value.Str("Must navigate to Settings Screen first. Enables or disables AMOLED mode, which uses pure black backgrounds to save battery on AMOLED screens."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "enable" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("BOOLEAN"),
                                    "description" to Value.Str("Whether to enable or disable AMOLED mode.")
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(Value.Str("enable"))
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
        changeSeedColorTool,
        enableAmoledModeTool
    )

}