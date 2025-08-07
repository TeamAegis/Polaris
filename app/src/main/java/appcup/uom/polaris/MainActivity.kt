package appcup.uom.polaris

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import appcup.uom.polaris.core.extras.theme.PolarisDarkColorScheme
import appcup.uom.polaris.core.extras.theme.PolarisLightColorScheme
import appcup.uom.polaris.core.extras.theme.PolarisTheme
import appcup.uom.polaris.core.extras.theme.SeedColor
import appcup.uom.polaris.core.extras.theme.Theme
import appcup.uom.polaris.core.presentation.app.App
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.conversational_ai.utils.PermissionResultCallback
import appcup.uom.polaris.features.conversational_ai.utils.PermissionsBridgeListener
import com.materialkolor.DynamicMaterialThemeState
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity(), PermissionsBridgeListener {

    val supabaseClient: SupabaseClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalContext.get().get<PermissionBridge>().setListener(this)

        supabaseClient.handleDeeplinks(intent)

//        if (intent.data != null)
//            ExternalUriHandler.onNewUri(intent.data.toString())

        var supabaseInitialized = false
        var appInitialized = false

        lifecycleScope.launch {
            supabaseClient.auth.sessionStatus.collect {
                when (it) {
                    is SessionStatus.Initializing -> {}
                    else -> {
                        delay(1000)
                        supabaseInitialized = true
                    }
                }
            }
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !supabaseInitialized || !appInitialized
            }
        }

        setContent {
            val darkColor = Color.Transparent
            val lightColor = Color.Transparent

            val isDarkTheme = rememberSaveable { mutableStateOf(false) }
            val isAmoled = rememberSaveable { mutableStateOf(false) }
            val color = remember { mutableStateOf<Color?>(SeedColor.CrimsonForge.color!!) }

            Theme(
                isDarkTheme = isDarkTheme,
                isAmoled = isAmoled,
                color = color
            ) {
                appInitialized = true
            }

            enableEdgeToEdge(
                statusBarStyle = if (!isDarkTheme.value) {
                    SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode())
                } else {
                    SystemBarStyle.dark(darkColor.hashCode())
                },
                navigationBarStyle = if (!isDarkTheme.value) {
                    SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode())
                } else {
                    SystemBarStyle.dark(darkColor.hashCode())
                }
            )

            val themeState: DynamicMaterialThemeState? = if (color.value != null) {
                rememberDynamicMaterialThemeState(
                    isAmoled = isAmoled.value,
                    isDark = isDarkTheme.value,
                    style = PaletteStyle.Vibrant,
                    specVersion = ColorSpec.SpecVersion.SPEC_2025,
                    primary = color.value!!,
                    secondary = color.value,
                    tertiary = color.value
                )
            } else {
                null
            }

            PolarisTheme(
                darkTheme = isDarkTheme.value,
                dynamicColor = color.value == null,
//                lightColorScheme = themeState?.colorScheme ?: LightColorScheme,
//                darkColorScheme = themeState?.colorScheme ?: DarkColorScheme,
                lightColorScheme = PolarisLightColorScheme,
                darkColorScheme = PolarisDarkColorScheme,
            ) {
                App()
            }
        }
    }

    private var locationPermissionResultCallback: PermissionResultCallback? = null

    private val requestLocationPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                locationPermissionResultCallback?.onPermissionGranted()
            } else {
                val finePermanentlyDenied =
                    !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                val coarsePermanentlyDenied =
                    !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                val permanentlyDenied = finePermanentlyDenied && coarsePermanentlyDenied

                locationPermissionResultCallback?.onPermissionDenied(permanentlyDenied)
            }
        }

    override fun requestLocationPermission(callback: PermissionResultCallback) {
        val fine = Manifest.permission.ACCESS_FINE_LOCATION
        val coarse = Manifest.permission.ACCESS_COARSE_LOCATION

        val fineGranted = ContextCompat.checkSelfPermission(this, fine) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(this, coarse) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            callback.onPermissionGranted()
            return
        }

        locationPermissionResultCallback = callback

        requestLocationPermissionsLauncher.launch(arrayOf(fine, coarse))
    }

    override fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }



    private var recordAudioPermissionResultCallback: PermissionResultCallback? = null
    private val requestRecordAudioPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                recordAudioPermissionResultCallback?.onPermissionGranted()
            } else {
                val permanentlyDenied =
                    !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                recordAudioPermissionResultCallback?.onPermissionDenied(permanentlyDenied)
            }

        }

    override fun requestRecordAudioPermission(callback: PermissionResultCallback) {
        val permission = Manifest.permission.RECORD_AUDIO

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            callback.onPermissionGranted()
            return
        } else if (!shouldShowRequestPermissionRationale(permission)) {
            callback.onPermissionDenied(false)
        } else {
            callback.onPermissionDenied(true)
            return
        }
        recordAudioPermissionResultCallback = callback
        requestRecordAudioPermissionLauncher.launch(permission)
    }

    override fun isRecordAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

    }

    private var cameraPermissionResultCallback: PermissionResultCallback? = null

    private val requestCameraPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraPermissionResultCallback?.onPermissionGranted()
            } else {
                val permanentlyDenied =
                    !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                cameraPermissionResultCallback?.onPermissionDenied(permanentlyDenied)
            }

        }

    override fun requestCameraPermission(callback: PermissionResultCallback) {
        val permission = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            callback.onPermissionGranted()
            return
        } else if (!shouldShowRequestPermissionRationale(permission)) {
            callback.onPermissionDenied(false)
        } else {
            callback.onPermissionDenied(true)
            return
        }

        cameraPermissionResultCallback = callback
        requestCameraPermissionLauncher.launch(permission)
    }

    override fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
