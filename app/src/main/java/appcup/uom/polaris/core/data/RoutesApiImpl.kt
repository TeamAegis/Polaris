package appcup.uom.polaris.core.data

import appcup.uom.polaris.core.domain.RoutesApi
import io.ktor.client.HttpClient

class RoutesApiImpl(
    private val httpClient: HttpClient
): RoutesApi {
}