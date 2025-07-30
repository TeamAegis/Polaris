package appcup.uom.polaris.features.conversational_ai.presentation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.domain.ValidationEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConversationalAI(
    viewModel: ConversationalAIViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.validationEvent.collect { event ->
            when (event) {
                ValidationEvent.Success -> {
                }

                is ValidationEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    ConversationalAIImpl(
        state = state.value,
        onAction = { action ->
            when (action) {
                ConversationalAIAction.StartRecording -> {
                    viewModel.onAction(action)
                }
                ConversationalAIAction.StopRecording -> {
                    viewModel.onAction(action)
                }
                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )

}

@Composable
fun ConversationalAIImpl(
    state: ConversationalAIState,
    onAction: (ConversationalAIAction) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite")

    val wobble by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Wobble"
    )

    val userScale by animateFloatAsState(
        targetValue = if (state.isUserSpeaking)
            1f + state.userAudioLevel.coerceIn(0f, 1f) * 1.2f
        else 1f,
        animationSpec = tween(200, easing = FastOutLinearInEasing),
        label = "UserScale"
    )

    val botScale by animateFloatAsState(
        targetValue = if (state.isBotSpeaking)
            1f + state.botAudioLevel.coerceIn(0f, 1f) * 1.1f
        else 1f,
        animationSpec = tween(200, easing = FastOutLinearInEasing),
        label = "BotScale"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    val scale = when (state.connectionState) {
        ConversationalAIConnectionState.Connecting -> pulse
        ConversationalAIConnectionState.Connected -> if (state.isUserSpeaking) userScale else botScale
        ConversationalAIConnectionState.Idle -> 1f
    }

    val mainColor = when (state.connectionState) {
        ConversationalAIConnectionState.Connecting -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ConversationalAIConnectionState.Connected -> when {
            state.isUserSpeaking -> MaterialTheme.colorScheme.primary
            state.isBotSpeaking -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.secondary
        }
        ConversationalAIConnectionState.Idle -> MaterialTheme.colorScheme.secondaryContainer
    }

    val showIdleIndicator = state.connectionState == ConversationalAIConnectionState.Idle && !state.isRecording
    val idleAlpha by animateFloatAsState(
        targetValue = if (showIdleIndicator) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "IdleAlpha"
    )


    Box(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (state.isRecording) {
                    onAction(ConversationalAIAction.StopRecording)
                } else {
                    onAction(ConversationalAIAction.StartRecording)
                }
            }
            .size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val baseRadius = size.minDimension / 4f

            val innerCircleScaledRadius = baseRadius * 0.65f * (1f + 0.5f * idleAlpha)


            val center = size.center
            val scaledRadius = baseRadius * scale
            val controlOffset = scaledRadius * 0.5f + wobble

            val path = Path().apply {
                moveTo(center.x, center.y - scaledRadius)
                cubicTo(
                    center.x + controlOffset, center.y - scaledRadius,
                    center.x + scaledRadius, center.y - controlOffset,
                    center.x + scaledRadius, center.y
                )
                cubicTo(
                    center.x + scaledRadius, center.y + controlOffset,
                    center.x + controlOffset, center.y + scaledRadius,
                    center.x, center.y + scaledRadius
                )
                cubicTo(
                    center.x - controlOffset, center.y + scaledRadius,
                    center.x - scaledRadius, center.y + controlOffset,
                    center.x - scaledRadius, center.y
                )
                cubicTo(
                    center.x - scaledRadius, center.y - controlOffset,
                    center.x - controlOffset, center.y - scaledRadius,
                    center.x, center.y - scaledRadius
                )
                close()
            }

            drawPath(path, color = mainColor.copy(alpha = 0.3f * (1f - idleAlpha)))

            drawCircle(
                color = mainColor,
                radius = innerCircleScaledRadius,
                center = center
            )
        }

        if (idleAlpha > 0f) {
            Icon(
                imageVector = Robot,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp).alpha(idleAlpha)
            )
        }
    }
}

val Robot: ImageVector
    get() {
        if (RobotImpl != null) return RobotImpl!!

        RobotImpl = ImageVector.Builder(
            name = "Robot",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-400f)
                quadToRelative(0f, -100f, 70f, -170f)
                reflectiveQuadToRelative(170f, -70f)
                horizontalLineToRelative(240f)
                quadToRelative(100f, 0f, 170f, 70f)
                reflectiveQuadToRelative(70f, 170f)
                verticalLineToRelative(400f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-400f)
                quadToRelative(0f, -66f, -47f, -113f)
                reflectiveQuadToRelative(-113f, -47f)
                horizontalLineTo(360f)
                quadToRelative(-66f, 0f, -113f, 47f)
                reflectiveQuadToRelative(-47f, 113f)
                close()
                moveToRelative(160f, -280f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(280f, 400f)
                reflectiveQuadToRelative(23.5f, -56.5f)
                reflectiveQuadTo(360f, 320f)
                reflectiveQuadToRelative(56.5f, 23.5f)
                reflectiveQuadTo(440f, 400f)
                reflectiveQuadToRelative(-23.5f, 56.5f)
                reflectiveQuadTo(360f, 480f)
                moveToRelative(240f, 0f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(520f, 400f)
                reflectiveQuadToRelative(23.5f, -56.5f)
                reflectiveQuadTo(600f, 320f)
                reflectiveQuadToRelative(56.5f, 23.5f)
                reflectiveQuadTo(680f, 400f)
                reflectiveQuadToRelative(-23.5f, 56.5f)
                reflectiveQuadTo(600f, 480f)
                moveTo(280f, 760f)
                verticalLineToRelative(-80f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(360f, 600f)
                horizontalLineToRelative(240f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(680f, 680f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(-80f, 0f)
                horizontalLineToRelative(560f)
                close()
            }
        }.build()

        return RobotImpl!!
    }

private var RobotImpl: ImageVector? = null