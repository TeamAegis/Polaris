package appcup.uom.polaris.core.presentation.more

import appcup.uom.polaris.features.auth.domain.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class MoreState @OptIn(ExperimentalUuidApi::class) constructor(
    val user: User = User(Uuid.NIL, "", "", 0, 0),
    val isLoading: Boolean = false
)