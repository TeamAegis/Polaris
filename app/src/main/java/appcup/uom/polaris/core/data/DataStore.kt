package appcup.uom.polaris.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "prefs.preferences_pb"

fun getPreferencesDataStore(path: String) = PreferenceDataStoreFactory.createWithPath {
    path.toPath()
}

private lateinit var applicationContext: Context

fun initPreferencesDataStore(appContext: Context) {
    applicationContext = appContext
}
fun getPreferencesDataStorePath(appContext: Context): String =
    appContext.filesDir.resolve(dataStoreFileName).absolutePath

fun createPreferencesDataStore(): DataStore<Preferences> {
    val path = getPreferencesDataStorePath(applicationContext)
    return getPreferencesDataStore(path)
}