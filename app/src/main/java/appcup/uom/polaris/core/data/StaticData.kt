package appcup.uom.polaris.core.data

import appcup.uom.polaris.core.extras.theme.SeedColor
import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.auth.domain.User

object StaticData {
    var appTheme: AppTheme = AppTheme.System
    var seedColor: SeedColor = SeedColor.CrimsonForge
    var isAmoled: Boolean = false
    lateinit var user: User
}