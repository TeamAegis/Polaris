package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.Serializable

@Serializable
enum class JourneyStatus(val label: String) {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
}