package appcup.uom.polaris.core.extras.theme

import android.os.Build
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
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
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