package appcup.uom.polaris.core.presentation.quest

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import appcup.uom.polaris.core.presentation.map.MapActions
import appcup.uom.polaris.core.presentation.map.MapState
import appcup.uom.polaris.core.presentation.map.MapViewModel
import appcup.uom.polaris.core.presentation.quest.data.getQuest
import appcup.uom.polaris.core.presentation.settings.AppTheme
import org.json.JSONObject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun QuestScreen(
    viewModel: MapViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
){

    var quests by remember { mutableStateOf<JSONObject?>(null) }
    val context = LocalContext.current

    LaunchedEffect(true) {
        getQuest(context){ response ->
            quests = response
        }
    }

    val daily = quests?.get("dailyQuests")

    Scaffold(
        Modifier
            .fillMaxSize()

    ) { contentPadding ->
        Box{
            Row(
                Modifier
                    .background(Color.Gray)
            ) {
                for (item in daily){

                }
                Text()
            }
        }
    }

}

@Composable
fun QuestImp(
    context: Context,
    state: MapState,
    onAction: (MapActions) -> Unit
) {
    getQuest(context) { response ->
        val daily = response.getJSONArray("dailyQuests")
        val weekly = response.getJSONArray("weeklyQuests")

        Scaffold(
            Modifier
        ) {

        }
    }

}