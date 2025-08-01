package appcup.uom.polaris.core.extras.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.presentation.settings.AppTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val PolarisColorScheme = ColorScheme(
    primary = primaryColor,
    onPrimary = surfaceColor, // Typically white or black depending on primary
    primaryContainer = primaryColor.copy(alpha = 0.8f), // Lighter or darker shade of primary
    onPrimaryContainer = surfaceColor, // Contrast for primaryContainer
    inversePrimary = surfaceColor, // Used for elements that need to contrast with primary
    secondary = primaryColor.copy(alpha = 0.7f), // Similar to primary or a complementary color
    onSecondary = surfaceColor, // Contrast for secondary
    secondaryContainer = primaryColor.copy(alpha = 0.5f), // Lighter or darker shade of secondary
    onSecondaryContainer = onSurfaceColor, // Contrast for secondaryContainer
    tertiary = primaryColor.copy(alpha = 0.6f), // Similar to primary or a complementary color
    onTertiary = surfaceColor, // Contrast for tertiary
    tertiaryContainer = primaryColor.copy(alpha = 0.4f), // Lighter or darker shade of tertiary
    onTertiaryContainer = onSurfaceColor, // Contrast for tertiaryContainer
    background = surfaceColor,
    onBackground = onSurfaceColor,
    surface = surfaceColor,
    onSurface = onSurfaceColor,
    surfaceVariant = surfaceColor.copy(alpha = 0.9f), // Slightly different shade of surface
    onSurfaceVariant = onSurfaceColor, // Contrast for surfaceVariant
    surfaceTint = primaryColor, // Often same as primary
    inverseSurface = onSurfaceColor, // Inverse of surface
    inverseOnSurface = surfaceColor, // Inverse of onSurface
    error = Color(0xFFB00020), // Standard error color
    onError = Color.White, // Contrast for error
    errorContainer = Color(0xFFFDECEA), // Lighter shade for error background,
    onErrorContainer = Color(red = 0x5C, green = 0x28, blue = 0x26), // Contrast for errorContainer
    outline = onSurfaceColor.copy(alpha = 0.5f), // For outlines
    outlineVariant = onSurfaceColor.copy(alpha = 0.3f), // Slightly different outline
    scrim = Color.Black.copy(alpha = 0.32f), // For scrims
    surfaceBright = surfaceColor.copy(alpha = 0.95f),
    surfaceDim = surfaceColor.copy(alpha = 0.85f),
    surfaceContainer = surfaceColor.copy(alpha = 0.9f),
    surfaceContainerHigh = surfaceColor.copy(alpha = 0.92f),
    surfaceContainerHighest = surfaceColor.copy(alpha = 0.94f),
    surfaceContainerLow = surfaceColor.copy(alpha = 0.88f),
    surfaceContainerLowest = surfaceColor.copy(alpha = 0.86f),
    primaryFixed = primaryColor,
    primaryFixedDim = primaryColor.copy(alpha = 0.9f),
    onPrimaryFixed = surfaceColor,
    onPrimaryFixedVariant = onSurfaceColor,
    secondaryFixed = primaryColor.copy(alpha = 0.7f),
    secondaryFixedDim = primaryColor.copy(alpha = 0.6f),
    onSecondaryFixed = surfaceColor,
    onSecondaryFixedVariant = onSurfaceColor,
    tertiaryFixed = primaryColor.copy(alpha = 0.6f),
    tertiaryFixedDim = primaryColor.copy(alpha = 0.5f),
    onTertiaryFixed = surfaceColor,
    onTertiaryFixedVariant = onSurfaceColor
)

val PolarisLightColorScheme = ColorScheme(
    primary = primaryColor,
    onPrimary = surfaceColor,
    primaryContainer = primaryColor.copy(alpha = 0.6f),
    onPrimaryContainer = surfaceColor,

    inversePrimary = surfaceColor,

    secondary = primaryColor.copy(alpha = 0.5f),
    onSecondary = onSurfaceColor,
    secondaryContainer = primaryColor.copy(alpha = 0.3f),
    onSecondaryContainer = onSurfaceColor,

    tertiary = primaryColor.copy(alpha = 0.4f),
    onTertiary = onSurfaceColor,
    tertiaryContainer = primaryColor.copy(alpha = 0.2f),
    onTertiaryContainer = onSurfaceColor,

    background = surfaceColor,
    onBackground = onSurfaceColor,

    surface = surfaceColor,
    onSurface = onSurfaceColor,
    surfaceVariant = surfaceColor.copy(alpha = 0.95f),
    onSurfaceVariant = onSurfaceColor,

    surfaceTint = primaryColor,

    inverseSurface = onSurfaceColor,
    inverseOnSurface = surfaceColor,

    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Color(0xFF5C2826),

    outline = onSurfaceColor.copy(alpha = 0.4f),
    outlineVariant = onSurfaceColor.copy(alpha = 0.2f),

    scrim = Color.Black.copy(alpha = 0.32f),

    surfaceBright = surfaceColor.copy(alpha = 0.97f),
    surfaceDim = surfaceColor.copy(alpha = 0.85f),
    surfaceContainer = surfaceColor.copy(alpha = 0.9f),
    surfaceContainerHigh = surfaceColor.copy(alpha = 0.93f),
    surfaceContainerHighest = surfaceColor.copy(alpha = 0.96f),
    surfaceContainerLow = surfaceColor.copy(alpha = 0.88f),
    surfaceContainerLowest = surfaceColor.copy(alpha = 0.86f),

    primaryFixed = primaryColor,
    primaryFixedDim = primaryColor.copy(alpha = 0.8f),
    onPrimaryFixed = onSurfaceColor,
    onPrimaryFixedVariant = onSurfaceColor,

    secondaryFixed = primaryColor.copy(alpha = 0.5f),
    secondaryFixedDim = primaryColor.copy(alpha = 0.4f),
    onSecondaryFixed = onSurfaceColor,
    onSecondaryFixedVariant = onSurfaceColor,

    tertiaryFixed = primaryColor.copy(alpha = 0.4f),
    tertiaryFixedDim = primaryColor.copy(alpha = 0.3f),
    onTertiaryFixed = onSurfaceColor,
    onTertiaryFixedVariant = onSurfaceColor
)

val PolarisDarkColorScheme = ColorScheme(
    primary = primaryColor.copy(alpha = 0.8f),
    onPrimary = surfaceColor,
    primaryContainer = primaryColor.copy(alpha = 0.6f),
    onPrimaryContainer = surfaceColor,

    inversePrimary = onSurfaceColor,

    secondary = primaryColor.copy(alpha = 0.5f),
    onSecondary = surfaceColor,
    secondaryContainer = primaryColor.copy(alpha = 0.3f),
    onSecondaryContainer = surfaceColor,

    tertiary = primaryColor.copy(alpha = 0.4f),
    onTertiary = surfaceColor,
    tertiaryContainer = primaryColor.copy(alpha = 0.2f),
    onTertiaryContainer = surfaceColor,

    background = onSurfaceColor,
    onBackground = surfaceColor,

    surface = onSurfaceColor,
    onSurface = surfaceColor,
    surfaceVariant = onSurfaceColor.copy(alpha = 0.95f),
    onSurfaceVariant = surfaceColor,

    surfaceTint = primaryColor,

    inverseSurface = surfaceColor,
    inverseOnSurface = onSurfaceColor,

    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF5C2826),
    onErrorContainer = Color(0xFFFDECEA),

    outline = surfaceColor.copy(alpha = 0.4f),
    outlineVariant = surfaceColor.copy(alpha = 0.2f),

    scrim = Color.Black.copy(alpha = 0.6f),

    surfaceBright = onSurfaceColor.copy(alpha = 0.95f),
    surfaceDim = onSurfaceColor.copy(alpha = 0.85f),
    surfaceContainer = onSurfaceColor.copy(alpha = 0.9f),
    surfaceContainerHigh = onSurfaceColor.copy(alpha = 0.93f),
    surfaceContainerHighest = onSurfaceColor.copy(alpha = 0.96f),
    surfaceContainerLow = onSurfaceColor.copy(alpha = 0.88f),
    surfaceContainerLowest = onSurfaceColor.copy(alpha = 0.86f),

    primaryFixed = primaryColor,
    primaryFixedDim = primaryColor.copy(alpha = 0.7f),
    onPrimaryFixed = surfaceColor,
    onPrimaryFixedVariant = surfaceColor,

    secondaryFixed = primaryColor.copy(alpha = 0.4f),
    secondaryFixedDim = primaryColor.copy(alpha = 0.3f),
    onSecondaryFixed = surfaceColor,
    onSecondaryFixedVariant = surfaceColor,

    tertiaryFixed = primaryColor.copy(alpha = 0.3f),
    tertiaryFixedDim = primaryColor.copy(alpha = 0.2f),
    onTertiaryFixed = surfaceColor,
    onTertiaryFixedVariant = surfaceColor
)



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PolarisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    lightColorScheme: ColorScheme = LightColorScheme,
    darkColorScheme: ColorScheme = DarkColorScheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = Typography,
        content = content
    )
}

@Composable
fun Theme(isDarkTheme: MutableState<Boolean>, isAmoled: MutableState<Boolean>, color: MutableState<Color?>, onAppInitialized: () -> Unit) {
    val prefs : DataStore<Preferences> = koinInject<DataStore<Preferences>>()
    val isSystemInDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        prefs.data.collectLatest { data ->
            val themeKey = stringPreferencesKey(Constants.PREFERENCES_THEME)
            val amoledKey = booleanPreferencesKey(Constants.PREFERENCES_AMOLED)
            val themeColorKey = stringPreferencesKey(Constants.PREFERENCES_THEME_COLOR)

            val theme = data[themeKey] ?: AppTheme.System.name
            if (theme == AppTheme.System.name) {
                isDarkTheme.value = isSystemInDarkTheme
            } else {
                isDarkTheme.value = theme == AppTheme.Dark.name
            }

            isAmoled.value = data[amoledKey] ?: false

            val selectedColor = data[themeColorKey] ?: SeedColor.CrimsonForge.name
            if (selectedColor == SeedColor.Dynamic.name) {
                color.value = null
            } else {
                color.value = SeedColor.valueOf(selectedColor).color!!
            }
            StaticData.appTheme = AppTheme.valueOf(theme)
            StaticData.isAmoled = isAmoled.value
            StaticData.seedColor = SeedColor.valueOf(selectedColor)
            onAppInitialized.invoke()
        }
    }
}