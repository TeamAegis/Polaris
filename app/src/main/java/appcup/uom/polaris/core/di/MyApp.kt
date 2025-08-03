package appcup.uom.polaris.core.di

import android.app.Application
import appcup.uom.polaris.core.data.initPreferencesDataStore
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initPreferencesDataStore(this)
        initKoin {
            androidContext(this@MyApp)
        }
    }
}