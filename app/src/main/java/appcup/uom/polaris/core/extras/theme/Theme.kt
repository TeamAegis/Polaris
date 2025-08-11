package appcup.uom.polaris.core.extras.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
    dynamicColor: Boolean = false,
    lightColorScheme: ColorScheme = PolarisLightColorScheme,
    darkColorScheme: ColorScheme = PolarisDarkColorScheme,
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
        typography = Typography(),
        content = content
    )
}