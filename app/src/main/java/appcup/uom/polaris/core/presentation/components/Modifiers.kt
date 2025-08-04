package appcup.uom.polaris.core.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

fun Modifier.polarisDropShadow(): Modifier {
    return dropShadow(
        shape = RoundedCornerShape(16.dp),
        shadow = Shadow(
            radius = 15.dp,
            alpha = 0.25f,
            offset = DpOffset(0.dp, 4.dp)
        )
    )
}

fun Modifier.polarisShadow(): Modifier {
    return dropShadow(
        shape = RoundedCornerShape(16.dp),
        shadow = Shadow(
            radius = 8.dp,
            alpha = 0.25f,
            offset = DpOffset(0.dp, 0.dp)
        )
    )
}
