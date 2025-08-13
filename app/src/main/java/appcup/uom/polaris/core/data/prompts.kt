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
        
        <example>
        {
            "dailyQuests": [
                {
                    "title": "Sunrise Swim",
                    "description": "Start your day with a refreshing dip in the ocean as the sun rises. Find a quiet beach and enjoy the peaceful morning.",
                    "relatedPlace": {
                        "placeName": "Public beaches on the east coast",
                        "placeType": [
                            "Beach",
                            "Ocean"
                        ]
                    }
                },
                {
                    "title": "Tropical Fruit Tasting",
                    "description": "Visit the local market and try at least three different fruits you've never had before, such as lychees, longans, or a tropical variety of mango.",
                    "relatedPlace": {
                        "placeName": "Goodlands Market",
                        "placeType": [
                            "Market"
                        ]
                    }
                },
                {
                    "title": "Beachcombing Adventure",
                    "description": "Walk along a new stretch of beach and look for unique seashells, pieces of coral, or interesting sea glass. The less-frequented beaches often have the best treasures.",
                    "relatedPlace": {
                        "placeName": "Pointe D'Esny Beach",
                        "placeType": [
                            "Beach"
                        ]
                    }
                },
                {
                    "title": "Local Cuisine Lunch",
                    "description": "Find a small, non-touristy restaurant or street food stall and order a dish that is a local specialty, like a seafood curry or a bowl of spicy noodles.",
                    "relatedPlace": {
                        "placeName": "La Demeure Saint Antoine",
                        "placeType": [
                            "Restaurant"
                        ]
                    }
                },
                {
                    "title": "Sunset Cocktail",
                    "description": "Find a spot on the west coast to watch the sun set over the water. Enjoy a local rum cocktail or fresh juice as the sky turns to fiery colors.",
                    "relatedPlace": {
                        "placeName": "Flic en Flac Beach",
                        "placeType": [
                            "Beach",
                            "Bar"
                        ]
                    }
                }
            ],
            "weeklyQuests": [
                {
                    "title": "Waterfall Hike",
                    "description": "Embark on a hike to one of the island's many stunning waterfalls. Swim in the natural pools and take in the lush, tropical scenery.",
                    "relatedPlace": {
                        "placeName": "Tamarind Falls (7 Cascades)",
                        "placeType": [
                            "Waterfall",
                            "Hiking Trail"
                        ]
                    }
                },
                {
                    "title": "Snorkeling/Diving Expedition",
                    "description": "Book a boat tour to a nearby marine park or coral reef to explore the vibrant underwater world. Look for colorful fish, sea turtles, and other marine life.",
                    "relatedPlace": {
                        "placeName": "Blue Bay Marine Park",
                        "placeType": [
                            "Marine Park",
                            "Snorkeling Spot"
                        ]
                    }
                },
                {
                    "title": "Cooking Class",
                    "description": "Sign up for a cooking class to learn how to prepare a traditional Mauritian dish. This is a great way to take a taste of the island home with you.",
                    "relatedPlace": {
                        "placeName": "Anantara Iko Mauritius Resort & Villas",
                        "placeType": [
                            "Hotel",
                            "Cooking School"
                        ]
                    }
                },
                {
                    "title": "Explore the Other Side",
                    "description": "Rent a car or scooter and spend the day exploring a part of the island you haven't seen yet. Discover a different landscape and pace of life.",
                    "relatedPlace": {
                        "placeName": "Le Morne Brabant",
                        "placeType": [
                            "Mountain",
                            "Hiking Trail"
                        ]
                    }
                },
                {
                    "title": "Island Hopping",
                    "description": "Take a boat trip to a smaller, uninhabited island nearby. Spend the day relaxing on pristine beaches and enjoying the secluded beauty.",
                    "relatedPlace": {
                        "placeName": "Île aux Cerfs",
                        "placeType": [
                            "Island",
                            "Beach"
                        ]
                    }
                }
            ]
        }
        </example>

        Quest Format:
        - 5 Daily Quests (short tasks achievable in a day)
        - 5 Weekly Quests (longer, more exploratory missions)

        Output Format:
        - Return the quests as JSON in the following format:
        
        {
            "dailyQuests": [
                {
                    "title": "String",
                    "description": "String",
                    "relatedPlace": {
                        "placeName": "String",
                        "placeType": ["String"],
                    }
                }
            ],
            "weeklyQuests": [
                {
                    "title": "String",
                    "description": "String",
                    "relatedPlace": {
                        "placeName": "String",
                        "placeType": ["String"],
                    }
                }
            ]
        }

        Make the quests fun and localized to Mauritius. Include local flavor, cultural landmarks, food spots, or nature trails when relevant.
    """.trimIndent()
}
