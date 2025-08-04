package appcup.uom.polaris.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PolarisTopAppBar(
    title: String,
    navigationIcon: @Composable () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(end = 12.dp),
                fontWeight = FontWeight.Bold
            )
        },
        subtitle = { },
        titleHorizontalAlignment = Alignment.End,
        navigationIcon = {
            Box(Modifier.padding(horizontal = 16.dp)) {
                navigationIcon()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PolarisLargeTopAppBar(
    title: String,
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable (() -> Unit)? = null
) {
    LargeFlexibleTopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(end = 12.dp),
                fontWeight = FontWeight.Bold
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            Box(Modifier.padding(horizontal = 16.dp)) {
                navigationIcon()
            }
        },
        actions = {
            if (actions != null) {
                Box(Modifier.padding(horizontal = 16.dp)) {
                    actions()
                }
            }
        },
        titleHorizontalAlignment = Alignment.End,
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
    )
}