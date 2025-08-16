package appcup.uom.polaris.features.polaris.data

import android.net.Uri
import android.util.Log
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.polaris.domain.Fragment
import appcup.uom.polaris.features.polaris.domain.FragmentsRepository
import appcup.uom.polaris.features.polaris.domain.PublicWaypoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FragmentsRepositoryImpl(
//    private val context: Context,
    private val supabaseClient: SupabaseClient
) : FragmentsRepository {
    override suspend fun createPublicWaypoint(waypoint: PublicWaypoint): Result<PublicWaypoint, DataError.FragmentError> {
        return try {
            val result = supabaseClient.from("public_waypoints").insert(
                waypoint
            ) {
                select()
            }.decodeSingle<PublicWaypoint>()
            Result.Success(result)
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_VALUE, "createPublicWaypoint: " + e.message)

            Result.Error(DataError.FragmentError.UNKNOWN)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getFragments(waypointId: Uuid): Flow<List<Fragment>> = callbackFlow {
        val channel =
            supabaseClient.channel("fragments_channel_${StaticData.user.id}_${Uuid.random()}")
        val fragmentsFlow = channel.postgresListDataFlow(
            schema = "public",
            table = "fragments",
            primaryKey = Fragment::id,
            filter = FilterOperation("public_waypoint_id", FilterOperator.EQ, waypointId)
        )
        channel.subscribe()

        val job = launch {
            fragmentsFlow.collect { fragments ->
                trySend(fragments)
            }
        }

        awaitClose {
            job.cancel()
            runBlocking {
                supabaseClient.realtime.removeChannel(channel)
            }
        }
    }

    override suspend fun addFragment(fragment: Fragment): Result<Unit, DataError.FragmentError> {
        return try {
            supabaseClient.from("fragments").insert(fragment)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_VALUE, "addFragment: " + e.message)
            Result.Error(DataError.FragmentError.UNKNOWN)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadImageFragment(imageUri: Uri): Result<String, DataError.FragmentError> {
        return try {
            val path = "${StaticData.user.id}/${Uuid.random()}_${System.currentTimeMillis()}.jpg"
            supabaseClient.storage.from("fragment").upload(
                path = path,
                uri = imageUri,
            ) {
                upsert = false
            }
            Result.Success(supabaseClient.storage.from("fragment").publicUrl(path))
        } catch (e: Exception) {
            Result.Error(DataError.FragmentError.UNKNOWN)
        }
    }
}