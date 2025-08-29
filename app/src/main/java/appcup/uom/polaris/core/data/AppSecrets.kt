package appcup.uom.polaris.core.data

import appcup.uom.polaris.BuildConfig

object AppSecrets {
    val supabaseUrl: String
        get() = BuildConfig.SUPABASE_URL
    val supabaseKey: String
        get() = BuildConfig.SUPABASE_API_KEY
    val geminiLiveApiKey: String
        get() = BuildConfig.GEMINI_LIVE_API_KEY
    val mapsApiKey: String
        get() = BuildConfig.MAPS_APP_API_KEY
}