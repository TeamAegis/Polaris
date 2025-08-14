package appcup.uom.polaris.core.data

fun createJourney(startingPoint: String, endPoint: String, nearbyPlaces: String, userPreference: String): String {
    /*
     * Formats the journey creation prompt based on input parameters:
     * - startingPoint: The journey's starting location.
     * - endPoint: The journey's ending location (optional).
     * - nearbyPlaces: A list of nearby places to include as waypoints.
     */
    return """
        Create a journey in Mauritius using the provided information:
        - User Preference: $userPreference
        - Starting Point: $startingPoint
        - End Point: $endPoint
        - Nearby Places: $nearbyPlaces

        Instructions:
        - If the end point is not specified, select one of the nearby places as the default end point.
        - Design a logical journey starting from `$startingPoint`, ending at `$endPoint`, and including nearby places as waypoints.
        - Use relevant nearby places to form interesting or efficient routes.
        - Make use of the user preference to decide how the journey should be structured.

        Return the output in the following JSON format:

        {
            "EndPoint": "String",
            "MiddleWayPoints": [
                {
                    "PlaceName": "String",
                    "PlaceLat": Double,
                    "PlaceLong": Double,
                    "PlaceType": ["String", ...],
                    "PlaceAddress": "String",
                    "PlaceDescription": "String"
                }
            ]
        }
    """.trimIndent()
}

fun createQuest(placesList: String, preference: String) : String {
    return """
        Generate a quest list for traveler/tourist/locals in Mauritius.

        Use the following list of places:
        $placesList
        
        Objective:
        - Create engaging and realistic quests that involve visiting or interacting with the places.
        - Include a variety of types: food, culture, shopping, nature, fitness, history, etc.
        - Make quests short and achievable for daily, and broader/more exploratory for weekly.
        - Takes into consideration the user's preferences, $preference .
        - If the list of places is empty, generate some generic quests that anyone can do. like the one in the example tag.
        
        Quest Format:
        - 5 Daily Quests (short tasks achievable in a day)
        - 5 Weekly Quests (longer, more exploratory missions)

        Make the quests fun and localized to Mauritius. Include local flavor, cultural landmarks, food spots, or nature trails when relevant.
    """.trimIndent()
}
