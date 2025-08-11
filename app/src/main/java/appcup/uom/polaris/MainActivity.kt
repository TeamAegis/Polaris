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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.extras.theme.PolarisTheme
import appcup.uom.polaris.core.presentation.app.AuthenticatedApp
import appcup.uom.polaris.core.presentation.app.UnauthenticatedApp
import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.features.auth.domain.User
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import appcup.uom.polaris.features.conversational_ai.utils.PermissionResultCallback
import appcup.uom.polaris.features.conversational_ai.utils.PermissionsBridgeListener
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.core.context.GlobalContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MainActivity : ComponentActivity(), PermissionsBridgeListener {

    val supabaseClient: SupabaseClient by inject()
    val prefs: DataStore<Preferences> by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalContext.get().get<PermissionBridge>().setListener(this)

        supabaseClient.handleDeeplinks(intent)

//        if (intent.data != null)
//            ExternalUriHandler.onNewUri(intent.data.toString())


        setContent {
            val darkColor = Color.Transparent
            val lightColor = Color.Transparent

            var userInitialized by rememberSaveable { mutableStateOf(false) }
            var themeInitialized by rememberSaveable { mutableStateOf(false) }

            installSplashScreen().apply {
                setKeepOnScreenCondition {
                    !userInitialized || !themeInitialized
                }
            }


            val isSystemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable { mutableStateOf(isSystemInDarkTheme) }
            LaunchedEffect(Unit) {
                prefs.data.collectLatest { data ->
                    val themeKey = stringPreferencesKey(Constants.PREFERENCES_THEME)

                    val theme = data[themeKey] ?: AppTheme.System.name
                    isDarkTheme = if (theme == AppTheme.System.name) {
                        isSystemInDarkTheme
                    } else {
                        theme == AppTheme.Dark.name
                    }

                    StaticData.appTheme = AppTheme.valueOf(theme)
                    themeInitialized = true
                }
            }

            val currentSession = supabaseClient.auth.currentSessionOrNull()
            var isAuthenticated by rememberSaveable { mutableStateOf(currentSession != null) }
            if (isAuthenticated) {
                setUser(currentSession!!.user!!)
            }

            LaunchedEffect(Unit) {
                supabaseClient.auth.sessionStatus.collect { status ->
                    when (status) {
                        is SessionStatus.Authenticated -> {
                            when (status.source) {
                                SessionSource.External -> {}
                                else -> {
                                    setUser(status.session.user!!)
                                    isAuthenticated = true
                                }
                            }
                            userInitialized = true
                        }

                        is SessionStatus.NotAuthenticated -> {
                            isAuthenticated = false
                            userInitialized = true
                        }

                        is SessionStatus.Initializing -> {}
                        is SessionStatus.RefreshFailure -> {
                            isAuthenticated = false
                            userInitialized = true
                        }
                    }
                }
            }



            enableEdgeToEdge(
                statusBarStyle = if (!isDarkTheme) {
                    SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode())
                } else {
                    SystemBarStyle.dark(darkColor.hashCode())
                },
                navigationBarStyle = if (!isDarkTheme) {
                    SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode())
                } else {
                    SystemBarStyle.dark(darkColor.hashCode())
                }
            )



            PolarisTheme(darkTheme = isDarkTheme) {
                if (isAuthenticated) {
                    AuthenticatedApp()
                } else {
                    UnauthenticatedApp()
                }
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

        val fineGranted =
            ContextCompat.checkSelfPermission(this, fine) == PackageManager.PERMISSION_GRANTED
        val coarseGranted =
            ContextCompat.checkSelfPermission(this, coarse) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            callback.onPermissionGranted()
            return
        }

        locationPermissionResultCallback = callback

        requestLocationPermissionsLauncher.launch(arrayOf(fine, coarse))
    }

    override fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
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

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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

    @OptIn(ExperimentalUuidApi::class)
    private fun setUser(user: UserInfo) {
        StaticData.user = User(
            id = Uuid.parse(user.id),
            name = user.userMetadata!!.getOrElse("name") { "" }.toString()
                .removeSurrounding("\""),
            email = user.email!!
        )
    }
}
