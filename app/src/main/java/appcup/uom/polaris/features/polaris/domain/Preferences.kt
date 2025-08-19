package appcup.uom.polaris.features.polaris.domain

import android.graphics.drawable.Icon
import androidx.compose.ui.res.painterResource
import appcup.uom.polaris.R
import kotlinx.serialization.Serializable

@Serializable
enum class Preferences(val label: String, val types: List<String>, var icon: Int) {
    FOOD(label = "\uD83C\uDF7D\uFE0F Food", types = listOf(
        "acai_shop", "afghani_restaurant", "african_restaurant", "american_restaurant",
        "asian_restaurant", "bagel_shop", "bakery", "bar", "bar_and_grill", "barbecue_restaurant",
        "brazilian_restaurant", "breakfast_restaurant", "brunch_restaurant", "buffet_restaurant",
        "cafe", "cafeteria", "candy_store", "cat_cafe", "chinese_restaurant", "chocolate_factory",
        "chocolate_shop", "coffee_shop", "confectionery", "deli", "dessert_restaurant", "dessert_shop",
        "diner", "dog_cafe", "donut_shop", "fast_food_restaurant", "fine_dining_restaurant",
        "food_court", "french_restaurant", "greek_restaurant", "hamburger_restaurant", "ice_cream_shop",
        "indian_restaurant", "indonesian_restaurant", "italian_restaurant", "japanese_restaurant",
        "juice_shop", "korean_restaurant", "lebanese_restaurant", "meal_delivery", "meal_takeaway",
        "mediterranean_restaurant", "mexican_restaurant", "middle_eastern_restaurant", "pizza_restaurant",
        "pub", "ramen_restaurant", "restaurant", "sandwich_shop", "seafood_restaurant",
        "spanish_restaurant", "steak_house", "sushi_restaurant", "tea_house", "thai_restaurant",
        "turkish_restaurant", "vegan_restaurant", "vegetarian_restaurant", "vietnamese_restaurant",
        "wine_bar"
    ), icon = R.drawable.icon_start
    ),
    ATTRACTIONS(label = "\uD83C\uDFDB\uFE0F Attractions", types = listOf(
        "art_gallery", "art_studio", "auditorium", "cultural_landmark", "historical_place", "monument",
        "museum", "performing_arts_theater", "sculpture", "adventure_sports_center", "amphitheatre",
        "amusement_center", "amusement_park", "aquarium", "banquet_hall", "barbecue_area",
        "botanical_garden", "bowling_alley", "casino", "comedy_club", "community_center",
        "concert_hall", "convention_center", "cultural_center", "cycling_park", "dance_hall", "dog_park",
        "event_venue", "ferris_wheel", "garden", "hiking_area", "historical_landmark", "karaoke",
        "marina", "movie_theater", "national_park", "night_club", "observation_deck", "off_roading_area",
        "opera_house", "park", "philharmonic_hall", "picnic_ground", "planetarium", "plaza",
        "roller_coaster", "skateboard_park", "state_park", "tourist_attraction", "video_arcade",
        "visitor_center", "water_park", "wedding_venue", "wildlife_park", "wildlife_refuge", "zoo",
        "tour_agency", "tourist_information_center", "travel_agency"
    ), icon = R.drawable.icon_start
    ),
    NATURE(label = "\uD83C\uDF3F Nature", types = listOf(
        "beach", "botanical_garden", "garden", "hiking_area", "national_park", "park",
        "picnic_ground", "state_park", "wildlife_park", "wildlife_refuge", "natural_feature"
    ), icon = R.drawable.icon_start
    ),
    HOTELS(label = "\uD83C\uDFE8 Hotels", types = listOf(
        "bed_and_breakfast", "campground", "camping_cabin", "cottage", "extended_stay_hotel",
        "farmstay", "guest_house", "hostel", "hotel", "inn", "japanese_inn", "lodging",
        "motel", "private_guest_room", "resort_hotel", "rv_park"
    ), icon = R.drawable.icon_start
    ),
    SHOPPING(label = "\uD83D\uDECD\uFE0F Shopping", types = listOf(
        "asian_grocery_store", "auto_parts_store", "bicycle_store", "book_store", "butcher_shop",
        "cell_phone_store", "clothing_store", "convenience_store", "department_store",
        "discount_store", "electronics_store", "food_store", "furniture_store", "gift_shop",
        "grocery_store", "hardware_store", "home_goods_store", "home_improvement_store",
        "jewelry_store", "liquor_store", "market", "pet_store", "shoe_store", "shopping_mall",
        "sporting_goods_store", "store", "supermarket", "warehouse_store", "wholesaler"
    ), icon = R.drawable.icon_start
    ),
    ESSENTIALS(label = "\uD83D\uDEBB Essentials", types = listOf(
        "atm", "bank", "pharmacy", "hospital", "police", "post_office", "gas_station",
        "parking", "bus_station", "train_station", "airport", "fire_station",
        "drugstore", "doctor", "laundry", "electric_vehicle_charging_station"
    ), icon = R.drawable.icon_start
    )}

