package appcup.uom.polaris.core.data

data class place_c(
    val name : String,
    val category : String,
    val lat : Double,
    val lng : Double,
    val desc : String
)

val beaches = listOf(
    place_c(
        "Flic en Flac",
        "Beaches & Islands",
        -20.2742,
        57.3631,
        "Long, scenic beach with great sunset views and calm waters."
    ),
    place_c(
        "Trou aux Biches",
        "Beaches & Islands",
        -20.0363,
        57.5447,
        "Shallow lagoon, perfect for swimming and snorkeling."
    ),
    place_c(
        "Belle Mare",
        "Beaches & Islands",
        -20.195,
        57.76833,
        "Great sunrise spot with clear turquoise waters."
    ),
    place_c(
        "Le Morne Beach",
        "Beaches & Islands",
        -20.4563,
        57.3082,
        "Iconic beach at the foot of Le Morne mountain."
    ),
    place_c(
        "Blue Bay",
        "Beaches & Islands",
        -20.44485,
        57.710756,
        "Crystal‑clear waters and a marine park ideal for snorkeling."
    ),
    place_c(
        "Île aux Cerfs",
        "Beaches & Islands",
        -20.27235,
        57.80411,
        "A beautiful island with water sports, bars, and beach clubs."
    ),
    place_c(
        "Pereybere",
        "Beaches & Islands",
        -19.999075,
        57.588669,
        "Small but lively beach with restaurants nearby."
    ),
    place_c(
        "Île aux Aigrettes",
        "Beaches & Islands",
        -20.42,
        57.732778,
        "Eco‑island and nature reserve near Mahebourg."
    )
)

val natureSpots = listOf(
    place_c(
        "Black River Gorges National Park",
        "Nature & Parks",
        -20.4167,
        57.4167,
        "Hike through lush tropical forest, spot rare endemic birds like the pink pigeon and Mauritius fody—nature at its wildest."
    ),
    place_c(
        "Chamarel Seven Coloured Earth",
        "Nature & Parks",
        -20.3500,
        57.4670,
        "Surreal sand dunes in rainbow hues—nature’s art, formed by volcanic minerals that never mix."
    ),
    place_c(
        "Chamarel Waterfall",
        "Nature & Parks",
        -20.3500,
        57.4670,
        "A dramatic ~95 m waterfall cascading over a cliff—nature's power just a short walk from the Coloured Earths."
    ),
    place_c(
        "La Vallée des Couleurs",
        "Nature & Parks",
        -20.3210,
        57.4100,
        "Thrilling zip lines, ATV rides, and vibrant colored earth formations—nature turned into adventure."
    ),
    place_c(
        "Ebony Forest Reserve",
        "Nature & Parks",
        -20.3500,
        57.4670,
        "Conservation treasure with skywalks and endemic trees—great combo with Chamarel's views."
    ),
    place_c(
        "SSR Botanical Garden (Pamplemousses)",
        "Nature & Parks",
        -20.1069,
        57.5794,
        "Historic tropical garden with giant water lilies, spices, palms—and royal trees planted by global leaders."
    ),
    place_c(
        "Rochester Falls",
        "Nature & Parks",
        -20.4000,
        57.4500,
        "Cascading waterfall framed by quirky rectangular rock formations—picture‑perfect and a bit unusual."
    ),
    place_c(
        "Casela Nature Parks",
        "Nature & Parks",
        -20.2833,
        57.3833,
        "Wildlife meets adrenaline—safari rides, bird encounters, and zip‑lines for the thrill‑seeker."
    )
)

val culturalSpots = listOf(
    place_c(
        "Aapravasi Ghat (UNESCO Site)",
        "Cultural & Historical",
        -20.15857,
        57.50298,
        "Historic immigration depot in Port Louis—where half a million indentured labourers passed through, now a poignant UNESCO heritage site."
    ),
    place_c(
        "Le Morne Brabant (UNESCO Site)",
        "Cultural & Historical",
        -20.4563,
        57.3082,
        "Sacred basaltic monolith and peninsula—freedom symbol for escaped slaves, dramatic views, and UNESCO-listed cultural landscape."
    ),
    place_c(
        "Grand Bassin (Ganga Talao)",
        "Cultural & Historical",
        -20.4460,
        57.5430,
        "Crater lake nestled in the mountains—Mauritius’s holiest Hindu pilgrimage site, especially vibrant during Maha Shivaratri."
    ),
    place_c(
        "L’Aventure du Sucre",
        "Cultural & Historical",
        -20.1240,
        57.5620,
        "Sweet history! A sugar museum in an old factory—tour through the island’s sugar past, with tasty samples."
    ),
    place_c(
        "Eureka House",
        "Cultural & Historical",
        -20.3050,
        57.5370,
        "Charming colonial-era mansion tucked in the hills—history, lush gardens, and old-world vibe."
    ),
    place_c(
        "Mahebourg Museum",
        "Cultural & Historical",
        -20.41583,
        57.70322,
        "Naval and cultural museum in Mahébourg—history of colonization, naval battles, and local heritage."
    ),
    place_c(
        "St. Louis Cathedral & Jummah Mosque",
        "Cultural & Historical",
        -20.16454,
        57.50646,
        "Side-by-side historic cathedrals and mosque in Port Louis—testaments to religious harmony and colonial architecture."
    )
)