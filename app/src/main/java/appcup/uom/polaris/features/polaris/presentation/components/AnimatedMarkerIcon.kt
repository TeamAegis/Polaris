package appcup.uom.polaris.features.polaris.presentation.components

import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import appcup.uom.polaris.features.polaris.domain.WaypointType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedMarkerIcon(
    type: WaypointType,
    label: String? = null,
    isNewlyUnlocked: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "marker_animations")

    // Common animations
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "time"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "shimmer"
    )

    // Type-specific animations
    val fragmentSpring by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutElastic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fragment_spring"
    )

    val intermediateFloat by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "intermediate_float"
    )

    val startEndPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "start_end_pulse"
    )

    val currentLocationRipple by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseOut)
        ),
        label = "current_ripple"
    )

    val unlockedGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "unlocked_glow"
    )

    val popScale by animateFloatAsState(
        targetValue = if (isNewlyUnlocked) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pop_scale"
    )

    val colors = when (type) {
        WaypointType.START -> listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
        WaypointType.INTERMEDIATE -> listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
        WaypointType.END -> listOf(Color(0xFFF44336), Color(0xFFE57373))
        WaypointType.CURRENT_LOCATION -> listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
        WaypointType.FRAGMENT -> listOf(Color(0xFF00E5FF), Color(0xFF40C4FF))
        WaypointType.UNLOCKED_WAYPOINT -> listOf(Color(0xFF9C27B0), Color(0xFFBA68C8))
        WaypointType.QUEST_WAYPOINT -> listOf(Color(0xFFFFD700), Color(0xFFFFE082))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        // Background effects
        when (type) {
            WaypointType.CURRENT_LOCATION -> {
                // Ripple effects for robot
                repeat(3) { index ->
                    val delay = index * 0.3f
                    val rippleScale = (currentLocationRipple - 1f + delay).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .graphicsLayer {
                                scaleX = 1f + rippleScale
                                scaleY = 1f + rippleScale
                                alpha = 1f - rippleScale
                            }
                            .background(
                                colors.first().copy(alpha = 0.2f),
                                CircleShape
                            )
                    )
                }
            }

            WaypointType.UNLOCKED_WAYPOINT -> {
                // Subtle glow for unlocked
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .graphicsLayer {
                            scaleX = unlockedGlow
                            scaleY = unlockedGlow
                            alpha = 0.3f
                        }
                        .background(
                            colors.first().copy(alpha = 0.4f),
                            CircleShape
                        )
                )
            }

            else -> {}
        }

        // Main marker shape and content
        when (type) {
            WaypointType.FRAGMENT -> {
                // Ethereal crystal shape
                Canvas(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            rotationZ = fragmentSpring + sin(time * PI.toFloat() * 4) * 10f
                            scaleX = popScale * (1f + sin(time * PI.toFloat() * 6) * 0.1f)
                            scaleY = popScale * (1f + cos(time * PI.toFloat() * 6) * 0.1f)
                        }
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.minDimension / 3

                    // Draw crystal facets
                    val path = Path()
                    for (i in 0 until 6) {
                        val angle = i * PI.toFloat() / 3 + time * PI.toFloat()
                        val x = centerX + cos(angle) * radius
                        val y = centerY + sin(angle) * radius
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()

                    // Gradient fill
                    drawPath(
                        path = path,
                        brush = Brush.linearGradient(
                            colors = colors + Color.White.copy(alpha = 0.8f),
                            start = Offset(shimmerOffset, 0f),
                            end = Offset(shimmerOffset + 100f, 100f)
                        )
                    )

                    // Glowing outline
                    drawPath(
                        path = path,
                        brush = SolidColor(Color.White),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Inner sparkle
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )
                }
            }

            WaypointType.START -> {
                // Pulsing circle with play icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = popScale * startEndPulse
                            scaleY = popScale * startEndPulse
                        }
                        .background(
                            Brush.linearGradient(
                                colors = colors,
                                start = Offset(shimmerOffset, 0f),
                                end = Offset(shimmerOffset + 100f, 100f)
                            ),
                            CircleShape
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val path = Path()
                        path.moveTo(size.width * 0.3f, size.height * 0.2f)
                        path.lineTo(size.width * 0.8f, size.height * 0.5f)
                        path.lineTo(size.width * 0.3f, size.height * 0.8f)
                        path.close()

                        drawPath(path, SolidColor(Color.White))
                    }
                }
            }

            WaypointType.END -> {
                // Square with finish flag pattern
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = popScale * startEndPulse
                            scaleY = popScale * startEndPulse
                        }
                        .background(
                            Brush.linearGradient(
                                colors = colors,
                                start = Offset(shimmerOffset, 0f),
                                end = Offset(shimmerOffset + 100f, 100f)
                            ),
                            RoundedCornerShape(8.dp)
                        )
                        .border(3.dp, Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val squareSize = 4.dp.toPx()
                        for (row in 0 until 4) {
                            for (col in 0 until 4) {
                                val isBlack = (row + col) % 2 == 0
                                drawRect(
                                    color = if (isBlack) Color.White else Color.Black.copy(alpha = 0.3f),
                                    topLeft = Offset(col * squareSize, row * squareSize),
                                    size = Size(squareSize, squareSize)
                                )
                            }
                        }
                    }
                }
            }

            WaypointType.INTERMEDIATE -> {
                // Diamond shape that floats up and down
                Canvas(
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = intermediateFloat.dp)
                        .graphicsLayer {
                            scaleX = popScale
                            scaleY = popScale
                            rotationZ = 45f
                        }
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.minDimension / 3

                    drawRect(
                        brush = Brush.linearGradient(
                            colors = colors,
                            start = Offset(shimmerOffset, 0f),
                            end = Offset(shimmerOffset + 100f, 100f)
                        ),
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    drawRect(
                        color = Color.White,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // Label
                label?.let {
                    Text(
                        text = it,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.offset(y = intermediateFloat.dp)
                    )
                }
            }

            WaypointType.CURRENT_LOCATION -> {
                // Cute robot
                Canvas(
                    modifier = Modifier
                        .size(56.dp)
                        .graphicsLayer {
                            scaleX = popScale
                            scaleY = popScale
                        }
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    // Robot body
                    drawRoundRect(
                        brush = Brush.linearGradient(colors),
                        topLeft = Offset(centerX - 20.dp.toPx(), centerY - 10.dp.toPx()),
                        size = Size(40.dp.toPx(), 25.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )

                    // Robot head
                    drawRoundRect(
                        brush = Brush.linearGradient(colors),
                        topLeft = Offset(centerX - 15.dp.toPx(), centerY - 25.dp.toPx()),
                        size = Size(30.dp.toPx(), 20.dp.toPx()),
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )

                    // Eyes
                    val eyeOffset = sin(time * PI.toFloat() * 2) * 2f
                    drawCircle(
                        Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(centerX - 6.dp.toPx() + eyeOffset, centerY - 18.dp.toPx())
                    )
                    drawCircle(
                        Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(centerX + 6.dp.toPx() + eyeOffset, centerY - 18.dp.toPx())
                    )

                    // Pupils
                    drawCircle(
                        Color.Black,
                        radius = 1.5.dp.toPx(),
                        center = Offset(centerX - 6.dp.toPx() + eyeOffset, centerY - 18.dp.toPx())
                    )
                    drawCircle(
                        Color.Black,
                        radius = 1.5.dp.toPx(),
                        center = Offset(centerX + 6.dp.toPx() + eyeOffset, centerY - 18.dp.toPx())
                    )

                    // Antenna
                    drawLine(
                        Color.White,
                        start = Offset(centerX, centerY - 25.dp.toPx()),
                        end = Offset(centerX, centerY - 32.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawCircle(
                        Color.Yellow,
                        radius = 2.dp.toPx(),
                        center = Offset(centerX, centerY - 32.dp.toPx())
                    )

                    // Arms (animated)
                    val armRotation = sin(time * PI.toFloat() * 3) * 0.3f
                    drawLine(
                        Color.White,
                        start = Offset(centerX - 20.dp.toPx(), centerY - 5.dp.toPx()),
                        end = Offset(
                            centerX - 20.dp.toPx() - cos(armRotation) * 8.dp.toPx(),
                            centerY - 5.dp.toPx() + sin(armRotation) * 8.dp.toPx()
                        ),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        Color.White,
                        start = Offset(centerX + 20.dp.toPx(), centerY - 5.dp.toPx()),
                        end = Offset(
                            centerX + 20.dp.toPx() + cos(armRotation) * 8.dp.toPx(),
                            centerY - 5.dp.toPx() + sin(armRotation) * 8.dp.toPx()
                        ),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            WaypointType.UNLOCKED_WAYPOINT -> {
                // Static checkmark with subtle glow
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = popScale
                            scaleY = popScale
                        }
                        .background(
                            Brush.linearGradient(
                                colors = colors,
                                start = Offset(0f, 0f),
                                end = Offset(100f, 100f)
                            ),
                            CircleShape
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Unlocked",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            WaypointType.QUEST_WAYPOINT -> {
                // Glowing star
                Canvas(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = popScale * (1f + sin(time * PI.toFloat() * 2) * 0.1f)
                            scaleY = popScale * (1f + sin(time * PI.toFloat() * 2) * 0.1f)
                            rotationZ = time * 360f * 0.1f
                        }
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val outerRadius = size.minDimension / 3
                    val innerRadius = outerRadius * 0.5f

                    val path = Path()
                    for (i in 0 until 10) {
                        val angle = i * PI.toFloat() / 5
                        val radius = if (i % 2 == 0) outerRadius else innerRadius
                        val x = centerX + cos(angle) * radius
                        val y = centerY + sin(angle) * radius
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()

                    drawPath(
                        path = path,
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, colors.first())
                        )
                    )

                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        // Sparkle effect for newly unlocked
        if (isNewlyUnlocked) {
            repeat(8) { index ->
                val angle = index * 45f + time * 360f
                val distance = 35f
                val sparkleAlpha = (sin(time * PI.toFloat() * 4 + index) + 1f) * 0.5f

                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .offset(
                            x = (cos(Math.toRadians(angle.toDouble())) * distance).dp,
                            y = (sin(Math.toRadians(angle.toDouble())) * distance).dp
                        )
                        .graphicsLayer {
                            alpha = sparkleAlpha
                            scaleX = sparkleAlpha
                            scaleY = sparkleAlpha
                        }
                        .background(Color.Yellow, CircleShape)
                )
            }
        }
    }
}

