package appcup.uom.polaris.features.polaris.domain

import kotlinx.serialization.Serializable

@Serializable
enum class Preferences(val label: String, val types: List<String>) {
    FOOD(label = "\uD83C\uDF7D\uFE0F Food", types = listOf()),
    ATTRACTIONS(label = "\uD83C\uDFDB\uFE0F Attractions", types = listOf()),
    NATURE(label = "\uD83C\uDF3F Nature", types = listOf()),
    HOTELS(label = "\uD83C\uDFE8 Hotels", types = listOf()),
    SHOPPING(label = "\uD83D\uDECD\uFE0F Shopping", types = listOf()),
    ESSENTIALS(label = "\uD83D\uDEBB Essentials", types = listOf())
}