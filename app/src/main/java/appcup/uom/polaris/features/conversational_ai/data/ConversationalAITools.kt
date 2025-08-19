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

    val searchPlacesTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.SEARCH_PLACES.name),
            "description" to Value.Str("Searches for places based on a search query. Use this when the user asks for specific types of places like restaurants, hotels, attractions, etc."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "search_query" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The search query for places (e.g., 'restaurants', 'gas stations', 'tourist attractions', 'coffee shops').")
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("search_query")
                        )
                    )
                )
            )
        )
    )

    val searchNearbyPlacesTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.SEARCH_NEARBY_PLACES.name),
            "description" to Value.Str("Retrieves all nearby places within a specified radius from the user's current location. Useful for discovering what's around the user."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "radius" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("NUMBER"),
                                    "description" to Value.Str("The search radius in meters from the user's current location (e.g., 1000 for 1km, 5000 for 5km).")
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("radius")
                        )
                    )
                )
            )
        )
    )

    val getUserLocationTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.GET_USER_LOCATION.name),
            "description" to Value.Str("Gets the user's current location including address and coordinates. Use this to understand where the user is currently located."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object()
                )
            )
        )
    )

    val sendWaypointTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.SEND_WAYPOINT.name),
            "description" to Value.Str("Sends a waypoint to the user based on their request. Use this after searching for places and getting user confirmation. The waypoint will be added to their journey or navigation or provided as a recommended place."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "place_id" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The unique place ID from the search results.")
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("place_id")
                        )
                    )
                )
            )
        )
    )

    val getAvailableJourneysTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.GET_AVAILABLE_JOURNEYS.name),
            "description" to Value.Str("Retrieves all available journeys that the user can start. Use this to show the user their journey options."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object()
                )
            )
        )
    )

    val startJourneyTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.START_JOURNEY.name),
            "description" to Value.Str("Starts a specific journey based on the journey ID. Use this after getting available journeys and user confirmation."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object(
                        values = arrayOf(
                            "journey_id" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The unique ID of the journey to start, obtained from the available journeys list.")
                                )
                            )
                        )
                    ),
                    "required" to Value.Array(
                        values = arrayOf(
                            Value.Str("journey_id")
                        )
                    )
                )
            )
        )
    )

    val stopJourneyTool = Value.Object(
        values = arrayOf(
            "name" to Value.Str(FunctionCallAction.STOP_JOURNEY.name),
            "description" to Value.Str("Stops the currently active journey. Use this when the user wants to end their current journey."),
            "parameters" to Value.Object(
                values = arrayOf(
                    "type" to Value.Str("OBJECT"),
                    "properties" to Value.Object()
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
        searchPlacesTool,
        searchNearbyPlacesTool,
        getUserLocationTool,
        sendWaypointTool,
        getAvailableJourneysTool,
        startJourneyTool,
        stopJourneyTool
    )

}