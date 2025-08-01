package appcup.uom.polaris.features.conversational_ai.domain

enum class Role() {
    USER,
    MODEL;

    fun capitalizeFirstLetter(): String {
        return this.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}