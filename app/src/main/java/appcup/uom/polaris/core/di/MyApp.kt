package appcup.uom.polaris.core.di

import android.app.Application
import appcup.uom.polaris.core.data.initPreferencesDataStore
import org.koin.android.ext.koin.androidContext

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initPreferencesDataStore(this)
        initKoin {
            androidContext(this@MyApp)
        }
    }
}