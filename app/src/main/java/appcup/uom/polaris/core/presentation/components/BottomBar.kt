package appcup.uom.polaris.core.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavBackStack
import appcup.uom.polaris.core.extras.navigation.Screen

@Composable
fun BottomBar(
    navBackStack: NavBackStack
) {
    val currentDestination = navBackStack.last()

    NavigationBar {
        BottomBarItem.entries.forEach { barItem ->
            val selected = currentDestination == barItem.screen
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) {
                        navBackStack.removeLastOrNull()
                        navBackStack.add(barItem.screen)
                        return@NavigationBarItem
                    }
                    navBackStack.clear()
                    navBackStack.add(Screen.Home)
                    navBackStack.add(barItem.screen)
                },
                icon = {
                    Icon(
                        barItem.icon,
                        contentDescription = barItem.label
                    )
                },
                label = {
                    Text(
                        barItem.label, overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
            )
        }
    }
}

enum class BottomBarItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
) {
    Home(Screen.Home, Icons.Default.Home, "Home"),
    Search(Screen.More, Icons.Default.MoreHoriz, "More"),
}

@Composable
fun NavBackStack.BottomBarVisibility(visible: MutableState<Boolean>) {
    val currentDestination = this.last()

    when {
        currentDestination == Screen.Home -> visible.value = true
        currentDestination == Screen.More -> visible.value = true
        else -> visible.value = false
    }
}