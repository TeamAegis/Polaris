package appcup.uom.polaris.core.presentation.more

import appcup.uom.polaris.features.auth.domain.User

data class MoreState(
    val user: User = User("", "", ""),
    val isLoading: Boolean = false
)