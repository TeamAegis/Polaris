package appcup.uom.polaris.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.extras.navigation.rebaseTo

@Composable
fun BottomBar(
    navBackStack: NavBackStack
) {


    Box(
        modifier = Modifier
            .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
            .polarisDropShadow()
    ) {
        CustomBottomNavigation(
            navBackStack = navBackStack
        )
    }
}

enum class BottomBarItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
) {
    Map(Screen.Map, Icons.Default.Map, "Map"),
    Journeys(Screen.Journeys, Journey, "Journeys"),
    Memories(Screen.Memories, PhotoPrints, "Memories"),
    Search(Screen.More, Icons.Default.MoreHoriz, "More"),
}

@Composable
fun CustomBottomNavigation(
    navBackStack: NavBackStack
) {
    val currentDestination = navBackStack.last()

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem.entries.forEach { barItem ->
            val selected = currentDestination == barItem.screen
            CustomBottomNavigationItem(
                item = barItem,
                isSelected = selected,
                onClick = {
                    if (selected) {
                        navBackStack.removeLastOrNull()
                        navBackStack.add(barItem.screen)
                        return@CustomBottomNavigationItem
                    }

                    navBackStack.rebaseTo(listOf(Screen.Map, barItem.screen))
                }
            )
        }
    }
}

@Composable
fun CustomBottomNavigationItem(
    item: BottomBarItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick)

    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = contentColor
            )
            AnimatedVisibility(
                visible = isSelected
            ) {
                Text(
                    text = item.label,
                    color = contentColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}