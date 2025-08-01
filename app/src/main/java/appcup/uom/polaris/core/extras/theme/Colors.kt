package appcup.uom.polaris.core.extras.theme

import androidx.compose.ui.graphics.Color

enum class SeedColor(val color: Color?) {
    Dynamic(null),
    LimeOlive(Color(0xFF68A500)),
    FrostySky(Color(0xFFCBDDEE)),
    RosyCoral(Color(0xFFEC6094)),
    PastelRed(Color(0xFFFF8787)),
    CrimsonForge(Color(0xFF8B0300)),
    RoyalIndigo(Color(0xFF4C5CDC));

    fun formattedName(): String =
        name.replace(Regex("(?<=[a-z])(?=[A-Z])")) {
            " ${it.value}"
        }
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}