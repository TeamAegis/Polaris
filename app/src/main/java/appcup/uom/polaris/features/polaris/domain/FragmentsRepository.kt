package appcup.uom.polaris.features.polaris.domain

import android.net.Uri
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface FragmentsRepository {

    suspend fun createPublicWaypoint(waypoint: PublicWaypoint): Result<PublicWaypoint, DataError.FragmentError>

    @OptIn(ExperimentalUuidApi::class)
    fun getFragments(waypointId: Uuid): Flow<List<Fragment>>

    suspend fun addFragment(fragment: Fragment): Result<Unit, DataError.FragmentError>

    suspend fun uploadImageFragment(imageUri: Uri): Result<String, DataError.FragmentError>
}