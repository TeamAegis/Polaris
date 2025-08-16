package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.Serializable

@Serializable
enum class WaypointType(val label: String) {
    START("Start"),
    INTERMEDIATE("Intermediate"),
    END("End"),
    CURRENT_LOCATION("Current Location"),
    FRAGMENT("Fragment"),
    QUEST_WAYPOINT("Quest Waypoint")
}
