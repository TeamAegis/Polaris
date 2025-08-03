package appcup.uom.polaris.features.chat.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import appcup.uom.polaris.core.presentation.components.polarisDropShadow

@Composable
fun TypingIndicator() {
    val dotSize = 6.dp
    val dotColor = MaterialTheme.colorScheme.onSurface
    val space = 4.dp
    val animDelay = 300

    val infiniteTransition = rememberInfiniteTransition(label = "typing-dots")
    val delays = listOf(0, animDelay, animDelay * 2)

    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = "Model",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier
                .size(20.dp)
                .padding(end = 4.dp)
                .align(Alignment.Bottom)
        )

        Box(
            modifier = Modifier
                .polarisDropShadow()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(space)) {
                repeat(3) { i ->
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, delayMillis = delays[i], easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot-$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .background(dotColor, shape = CircleShape)
                    )
                }
            }
        }
    }
}