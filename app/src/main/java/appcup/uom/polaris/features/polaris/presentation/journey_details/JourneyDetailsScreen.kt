@file:OptIn(ExperimentalTime::class)

package appcup.uom.polaris.features.polaris.presentation.journey_details


import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisTopAppBar
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.polaris.presentation.journey_details.components.JourneyDetailsCard
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.time.ExperimentalTime
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import kotlin.text.get

@Composable
fun JourneyDetailsScreen(
    viewModel: JourneyDetailsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    JourneyDetailsScreenImpl(
        state = state.value,
        onAction = { action ->
            when (action) {
                JourneyDetailsAction.OnBackClicked -> {
                    onBack()
                }

                is JourneyDetailsAction.OnDeleteClicked -> {
                    viewModel.onAction(JourneyDetailsAction.OnDeleteClicked {
                        onBack()
                    })
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}
//
//}
//@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun JourneyDetailsScreenImpl(
//    state: JourneyDetailsState,
//    onAction: (JourneyDetailsAction) -> Unit
//) {
//    Scaffold(
//        modifier = Modifier
//            .imePadding()
//            .statusBarsPadding()
//            .fillMaxSize(),
//        topBar = {
//            Column {
//                PolarisTopAppBar(
//                    title = "Journey Details",
//                    navigationIcon = {
//                        PolarisIconButton(
//                            icon = {
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                    contentDescription = "Back",
//                                    tint = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        ) {
//                            onAction(JourneyDetailsAction.OnBackClicked)
//                        }
//                    }
//                ) { }
//                if (state.isLoading) {
//                    LinearWavyProgressIndicator(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    )
//                }
//            }
//        },
//    ) { contentPadding ->
//        if (!state.isLoading) LazyColumn(
//            contentPadding = contentPadding,
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp)
//        ) {
//            //1. Journey Details Card
//            item {
//                state.journey?.let { journey ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 12.dp)
//                            .polarisDropShadow(),
//                        shape = RoundedCornerShape(20.dp)
//                    ) {
//                        JourneyDetailsCard(journey = journey)
//                    }
//                }
//            }
//
//            //2. Journey Name and Description
//            item {
//                state.journey?.let { journey ->
//                    Text(
//                        text = journey.name,
//                        fontWeight = FontWeight.Bold,
//                        style = MaterialTheme.typography.titleLarge,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 12.dp)
//                            .polarisDropShadow(),
//                        shape = RoundedCornerShape(20.dp)
//                    ) {
//                        Column(modifier = Modifier.padding(20.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp) ) {
////                            Text(
////                                text = journey.name,
////                                style = MaterialTheme.typography.headlineSmall,
////                                color = MaterialTheme.colorScheme.primary
////                            )
////                            Spacer(modifier = Modifier.height(8.dp))
////                            Text(
////                                text = journey.description,
////                                style = MaterialTheme.typography.bodyMedium,
////                                color = MaterialTheme.colorScheme.onSurfaceVariant
////                            )
//
//                            ExpandableDescription(journey.description)
//                        }
//                    }
//                }
//            }
//
//            //3. Waypoints
////            if (state.waypoints.isNotEmpty()) {
////                item {
////                    Card(
////                        modifier = Modifier
////                            .fillMaxWidth()
////                            .padding(bottom = 12.dp)
////                            .polarisDropShadow(),
////                        shape = RoundedCornerShape(20.dp)
////                    ) {
////                        Column(modifier = Modifier.padding(20.dp)) {
////                            Text(
////                                text = "Waypoints",
////                                style = MaterialTheme.typography.headlineSmall,
////                                color = MaterialTheme.colorScheme.primary
////                            )
////
////                            state.waypoints.forEach { waypoint ->
////                                Row(
////                                    verticalAlignment = Alignment.CenterVertically,
////                                    modifier = Modifier.padding(vertical = 4.dp)
////                                ) {
////                                    Icon(
////                                        imageVector = Icons.Filled.Place,
////                                        contentDescription = null,
////                                        tint = MaterialTheme.colorScheme.primary,
////                                        modifier = Modifier.size(20.dp)
////                                    )
////                                    Spacer(modifier = Modifier.width(8.dp))
////                                    Text(
////                                        text = if (waypoint.name != null && waypoint.name.isNotBlank()) waypoint.name else if (waypoint.address != null && waypoint.address.isNotBlank()) waypoint.address else "Unknown",
////                                        style = MaterialTheme.typography.bodyMedium,
////                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
////                                        modifier = Modifier.padding(top = 4.dp)
////                                    )
////                                }
////                            }
////                        }
////                    }
////                }
////            }
//
//            //3. Waypoints
//            if (state.waypoints.isNotEmpty()) {
//                item {
//                    Text(
//                        text = "Waypoints",
//                        fontWeight = FontWeight.Bold,
//                        style = MaterialTheme.typography.titleLarge,
//                        color = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
//                    )
//                }
//                item {
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 12.dp)
//                            .polarisDropShadow(),
//                        shape = RoundedCornerShape(20.dp)
//                    ) {
//                        Column(modifier = Modifier.padding(20.dp)) {
//                            state.waypoints.forEach { waypoint ->
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.padding(vertical = 4.dp)
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Filled.Place,
//                                        contentDescription = null,
//                                        tint = MaterialTheme.colorScheme.primary,
//                                        modifier = Modifier.size(20.dp)
//                                    )
//                                    Spacer(modifier = Modifier.width(8.dp))
//                                    Column {
//                                        Text(
//                                            text = waypoint.name.takeIf { !it.isNullOrBlank() }
//                                                ?: waypoint.address.takeIf { !it.isNullOrBlank() }
//                                                ?: "Unknown",
//                                            style = MaterialTheme.typography.bodyMedium,
//                                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                                        )
//                                        waypoint.address?.let {
//                                            if (it.isNotBlank() && it != waypoint.name) {
//                                                Text(
//                                                    text = it,
//                                                    style = MaterialTheme.typography.bodySmall,
//                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//
//
////            //4. Memories
////            if (state.memories.isNotEmpty()) {
////                item {
////                    Text(
////                        text = "Memories",
////                        style = MaterialTheme.typography.headlineSmall,
////                        color = MaterialTheme.colorScheme.onSurface,
////                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
////                    )
////                }
////                item {
////                    val carouselState = rememberCarouselState { state.memories.size }
////                    HorizontalMultiBrowseCarousel(
////                        state = carouselState,
////                        modifier = Modifier
////                            .fillMaxWidth()
////                            .wrapContentHeight()
////                            .padding(vertical = 16.dp),
////                        preferredItemWidth = 240.dp,
////                        itemSpacing = 12.dp,
////                        contentPadding = PaddingValues(horizontal = 16.dp)
////                    ) { index ->
////                        val memory = state.memories[index]
////                        Card(
////                            modifier = Modifier
////                                .fillMaxWidth()
////                                .height(160.dp)
////                                .polarisDropShadow(),
////                            shape = RoundedCornerShape(16.dp)
////                        ) {
////                            AsyncImage(
////                                model = File(memory.path),
////                                contentDescription = "Memory photo",
////                                modifier = Modifier
////                                    .fillMaxWidth()
////                                    .height(280.dp)
////                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
////                                contentScale = ContentScale.Crop
////                            )
////                        }
////                    }
////                }
////            }
////        }
////    }
////}
//
//            // Memories Section
//            if (state.memories.isNotEmpty()) {
//                item {
//                    Text(
//                        text = "Memories",
//                        fontWeight = FontWeight.Bold,
//                        style = MaterialTheme.typography.titleLarge,
//                        color = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
//                    )
//                }
//                item {
//                    val carouselState = rememberCarouselState { state.memories.size }
//                    HorizontalMultiBrowseCarousel(
//                        state = carouselState,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .wrapContentHeight()
//                            .padding(vertical = 16.dp),
//                        preferredItemWidth = 240.dp,
//                        itemSpacing = 12.dp,
//                        contentPadding = PaddingValues(horizontal = 16.dp)
//                    ) { index ->
//                        val memory = state.memories[index]
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(160.dp)
//                                .polarisDropShadow(),
//                            shape = RoundedCornerShape(16.dp)
//                        ) {
//                            AsyncImage(
//                                model = File(memory.path),
//                                contentDescription = "Memory photo",
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(160.dp)
//                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
//                                contentScale = ContentScale.Crop
//                            )
//                        }
//                    }
//                }
//            }
//
//            item { Spacer(modifier = Modifier.height(32.dp)) }
//        }
//    }
//}
//
//



// ...

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JourneyDetailsScreenImpl(
    state: JourneyDetailsState,
    onAction: (JourneyDetailsAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .imePadding()
            .statusBarsPadding()
            .fillMaxSize(),
        topBar = {
            Column {
                PolarisTopAppBar(
                    title = "Journey Details",
                    navigationIcon = {
                        PolarisIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            onAction(JourneyDetailsAction.OnBackClicked)
                        }
                    }
                ) { }
                if (state.isLoading) {
                    LinearWavyProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { contentPadding ->
        if (!state.isLoading) {
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Journey Cover Card
                item {
                    state.journey?.let { journey ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            JourneyDetailsCard(journey = journey)
                        }
                    }
                }

                // Journey Name + Description
                item {
                    state.journey?.let { journey ->
                        Text(
                            text = journey.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExpandableDescription(journey.description)
                            }
                        }
                    }
                }

                // Waypoints
                if (state.waypoints.isNotEmpty()) {
                    item { SectionHeader(title = "Waypoints") }
                    items(state.waypoints.size) { index ->
                        val waypoint = state.waypoints[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .polarisDropShadow(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = waypoint.name.takeIf { !it.isNullOrBlank() }
                                            ?: waypoint.address.takeIf { !it.isNullOrBlank() }
                                            ?: "Unknown",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    waypoint.address?.let {
                                        if (it.isNotBlank() && it != waypoint.name) {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Memories
                if (state.memories.isNotEmpty()) {
                    item { SectionHeader(title = "Memories") }
                    item {
                        val carouselState = rememberCarouselState { state.memories.size }
                        HorizontalMultiBrowseCarousel(
                            state = carouselState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(vertical = 16.dp),
                            preferredItemWidth = 220.dp,
                            itemSpacing = 12.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) { index ->
                            val memory = state.memories[index]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .polarisDropShadow(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                AsyncImage(
                                    model = File(memory.path),
                                    contentDescription = "Memory photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // Delete Button
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            onAction(
                                JourneyDetailsAction.OnDeleteClicked {
                                    onAction(JourneyDetailsAction.OnBackClicked)
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Delete Journey")
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}


// Expandable description composable
    @Composable
    fun ExpandableDescription(description: String) {
        var expanded by remember { mutableStateOf(false) }
        val maxLines = if (expanded) Int.MAX_VALUE else 3

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .animateContentSize()
                .clickable { expanded = !expanded }
                .padding(top = 4.dp)
        )
    }

// Reusable section header
@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}
