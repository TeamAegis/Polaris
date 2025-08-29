package appcup.uom.polaris.core.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun rememberDraggableFlingStateWithDynamicSize(
    padding: Dp = 16.dp,
    initialElementHeight: Dp = 56.dp
): MutableDraggableFlingState {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    return remember(configuration, padding) {
        with(density) {
            val paddingPx = padding.toPx()
            val screenWidthPx = configuration.screenWidthDp.dp.toPx()
            val screenHeightPx = configuration.screenHeightDp.dp.toPx()

            val initialX = paddingPx
            val initialY = (screenHeightPx - initialElementHeight.toPx()) / 4

            MutableDraggableFlingState(
                offsetX = Animatable(initialX),
                offsetY = Animatable(initialY),
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx,
                paddingPx = paddingPx,
                coroutineScope = scope
            )
        }
    }
}

class MutableDraggableFlingState(
    val offsetX: Animatable<Float, AnimationVector1D>,
    val offsetY: Animatable<Float, AnimationVector1D>,
    private val screenWidthPx: Float,
    private val screenHeightPx: Float,
    private val paddingPx: Float,
    private val coroutineScope: CoroutineScope
) {
    private var velocityTracker = VelocityTracker()

    private var _elementWidthPx = 56f * 3f
    private var _elementHeightPx = 56f * 3f

    val minX: Float get() = paddingPx
    val maxX: Float get() = (screenWidthPx - _elementWidthPx - paddingPx).coerceAtLeast(minX)
    val minY: Float get() = paddingPx
    val maxY: Float get() = (screenHeightPx - _elementHeightPx - paddingPx).coerceAtLeast(minY)
    val screenCenterX: Float get() = screenWidthPx / 2

    fun updateElementSize(widthPx: Float, heightPx: Float) {
        _elementWidthPx = widthPx
        _elementHeightPx = heightPx

        val currentX = offsetX.value
        val currentY = offsetY.value

        val newMaxX = maxX
        val newMaxY = maxY

        // Only adjust if the new bounds are smaller and current position is out of bounds
        coroutineScope.launch {
            if (currentX > newMaxX) {
                offsetX.snapTo(newMaxX)
            }
            if (currentY > newMaxY) {
                offsetY.snapTo(newMaxY)
            }
        }
    }

    fun resetVelocityTracker() {
        velocityTracker = VelocityTracker()
    }

    fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(timeMillis, position)
    }

    fun getVelocity(): Velocity = velocityTracker.calculateVelocity()
}

fun Modifier.draggableWithDynamicFling(
    state: MutableDraggableFlingState,
    scope: CoroutineScope
): Modifier = this.pointerInput(state) {
    val decay = splineBasedDecay<Float>(this)

    detectDragGestures(
        onDragStart = {
            scope.launch {
                state.offsetX.stop()
                state.offsetY.stop()
            }
            state.resetVelocityTracker()
        },
        onDragEnd = {
            val velocity = state.getVelocity()

            scope.launch {
                launch {
                    var targetX = state.offsetX.value

                    if (abs(velocity.x) > 100f) {
                        val naturalEnd = decay.calculateTargetValue(state.offsetX.value, velocity.x)
                        targetX = naturalEnd.coerceIn(state.minX, state.maxX)
                        if (naturalEnd in state.minX..state.maxX) {
                            state.offsetX.animateDecay(velocity.x, decay)
                        } else {
                            state.offsetX.animateTo(
                                targetValue = targetX,
                                initialVelocity = velocity.x
                            )
                        }
                    }

                    val snapTarget = if (targetX < state.screenCenterX) {
                        state.minX
                    } else {
                        state.maxX
                    }

                    if (abs(state.offsetX.value - snapTarget) > 1f) {
                        state.offsetX.animateTo(
                            targetValue = snapTarget,
                            animationSpec = tween(300, easing = EaseOutCubic)
                        )
                    }
                }

                launch {
                    if (abs(velocity.y) > 100f) {
                        val naturalEnd = decay.calculateTargetValue(state.offsetY.value, velocity.y)
                        val targetY = naturalEnd.coerceIn(state.minY, state.maxY)

                        if (naturalEnd in state.minY..state.maxY) {
                            state.offsetY.animateDecay(velocity.y, decay)
                        } else {
                            state.offsetY.animateTo(
                                targetValue = targetY,
                                initialVelocity = velocity.y
                            )
                        }
                    }
                }
            }
        },
        onDrag = { change, dragAmount ->
            change.consume()
            state.addPosition(change.uptimeMillis, change.position)

            scope.launch {
                val newX = (state.offsetX.value + dragAmount.x).coerceIn(state.minX, state.maxX)
                val newY = (state.offsetY.value + dragAmount.y).coerceIn(state.minY, state.maxY)

                state.offsetX.snapTo(newX)
                state.offsetY.snapTo(newY)
            }
        }
    )
}