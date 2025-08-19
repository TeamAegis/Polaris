package appcup.uom.polaris.core.data

fun createJourneyFunctionCallingPrompt(
    journeyName: String,
    journeyDescription: String,
    userPreference: String,
    encodedPolyline: String
): String {
    return """
        You are tasked with creating a complete and engaging journey in Mauritius.

        Journey Details:
        - Name: $journeyName
        - Description: $journeyDescription
        - User Preferences: $userPreference
        - Encoded Polyline: $encodedPolyline

        Instructions:
        1. Based on the user preferences, perform multiple **parallel calls** to the tool `getNearbyPlacesAlongRoute`:
            - Calls should pass both a search text (derived from user preferences, title and description) **and** the `encodedPolyline`.
        2. Collect all returned waypoints from these parallel calls.
        3. Evaluate these waypoints considering:
            - Relevance to user preferences
            - Logical geographic progression
            - Variety and uniqueness of experiences
            - Balance between scenic, cultural, and activity-based stops
            - Optimal travel efficiency while maintaining engagement
        4. Select the best waypoints to form a journey that feels **complete, worthwhile, and memorable**.
        5. Ensure the route has a natural flow and progression, ending in a **satisfying final destination**.
        6. Include enough waypoints to make the experience rich, but avoid overloading it.
        7. Whenever possible, ensure there is **at least one scenic stop, one cultural stop, and one activity-based stop**.
    """.trimIndent()
}

fun createJourneyFromExistingWaypointsPrompt(
    journeyName: String,
    journeyDescription: String,
    userPreference: String,
    encodedPolyline: String,
    intermediateWaypoints: String // Can be JSON or formatted list
): String {
    return """
        You are tasked with creating a meaningful and engaging journey in Mauritius.

        Journey Details:
        - Name: $journeyName
        - Description: $journeyDescription
        - User Preferences: $userPreference
        - Encoded Polyline: $encodedPolyline
        - Provided Intermediate Waypoints: $intermediateWaypoints

        Instructions:
        1. From the provided intermediate waypoints, select the best ones based on:
            - Relevance to user preferences
            - Logical geographic progression
            - Variety and uniqueness of experiences
            - Balance between scenic, cultural, and activity-based stops
            - Optimal travel efficiency while maintaining engagement
        2. Select the best waypoints to form a journey that feels **complete, worthwhile, and memorable**.
        3. Ensure the route has a natural flow and progression, ending in a **satisfying final destination**.
        4. Include enough waypoints to make the experience rich, but avoid overloading it.
        5. Whenever possible, ensure there is **at least one scenic stop, one cultural stop, and one activity-based stop**.
        6. Create a **better journey title** that captures the theme and appeal.
        7. Write an **improved journey description** that conveys excitement and purpose.
        8. Ensure the route has a satisfying final destination and is well-balanced.
    """.trimIndent()
}



fun createQuestPrompt(placesList: String, preference: String) : String {
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


fun waypointReachTriggerPrompt(
    currentWaypoints:String,
    nearbySearchItems: String
): String {
    return """
        I have just received new data. The user has successfully unlocked a waypoint.

        Current waypoint: $currentWaypoints
        Nearby places: $nearbySearchItems

        My task is to format a response to the user. I need to:
            Congratulate them warmly on reaching the waypoint.
            Say something positive and encouraging.
            Suggest a few places from the nearbySearchItems list, using their names, as potential next stops for the user's adventure.

        The final output should be a friendly, engaging message that feels like a natural part of a conversation, as if I'm the one sending it to the user.
    """.trimIndent()
}