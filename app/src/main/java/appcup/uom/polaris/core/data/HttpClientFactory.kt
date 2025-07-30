package appcup.uom.polaris.core.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

class HttpClientEngineFactory() {
    fun getHttpEngine(): HttpClientEngine {
        return OkHttp.create()
    }
}

