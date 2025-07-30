package appcup.uom.polaris.features.auth.presentation.display_name

import appcup.uom.polaris.core.data.StaticData

data class DisplayNameState(
    val name: String = StaticData.user.name,
    val currentName: String = StaticData.user.name,
    val isLoading: Boolean = false
)