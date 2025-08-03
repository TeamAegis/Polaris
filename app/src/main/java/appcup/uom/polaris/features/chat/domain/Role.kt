package appcup.uom.polaris.features.chat.domain

enum class Role() {
    USER,
    MODEL;

    fun capitalizeFirstLetter(): String {
        return this.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}