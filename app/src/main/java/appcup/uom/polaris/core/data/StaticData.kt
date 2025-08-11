package appcup.uom.polaris.core.data

import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.auth.domain.User

object StaticData {
    var appTheme: AppTheme = AppTheme.System
    lateinit var user: User
}