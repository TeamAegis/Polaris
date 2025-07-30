package appcup.uom.polaris.features.auth.presentation.start

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navigateToLogin: () -> Unit,
    navigateToRegister: () -> Unit,
) {
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-16).dp)
            ) {
                AetherLogoShape(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Column (
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy((-16).dp)
                ) {
                    Text(
                        text = "Polaris",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "By Aegis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1.4f))
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = navigateToLogin, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 16.dp)
            ) {
                Text(text = "Login")
            }
            Button(
                onClick = navigateToRegister, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 48.dp)
            ) {
                Text(text = "Register")
            }
        }
    }

}

@Composable
fun AetherLogoShape(modifier: Modifier = Modifier, color: Color = Color.Cyan) {
    val colorCenter = MaterialTheme.colorScheme.onPrimary
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Central point from which elements might emanate or converge
        val centerX = width / 2f
        val centerY = height / 2f

        // Define a path for a flowing, abstract shape
        val path = Path().apply {
            // Starting point - bottom left, slightly offset
            moveTo(width * 0.2f, height * 0.85f)

            // First curve - upward and towards center
            quadraticTo(
                x1 = width * 0.3f, y1 = height * 0.4f, // Control point
                x2 = centerX, y2 = height * 0.15f      // End point (top middle)
            )

            // Second curve - downward from top middle to top right region
            quadraticTo(
                x1 = width * 0.7f, y1 = height * 0.4f, // Control point
                x2 = width * 0.8f, y2 = height * 0.85f  // End point (bottom right)
            )

            // Third curve - creating a slight inward flow at the bottom
            // This connects back towards the start but with a curve
            quadraticTo(
                x1 = centerX, y1 = height * 0.75f,      // Control point (pulling inwards)
                x2 = width * 0.2f, y2 = height * 0.85f // Closing near the start
            )
            // For a filled shape that feels more "substantial"
            close() // Close the path to make it a fillable shape
        }

        // Draw the filled path
        drawPath(
            path = path,
            color = color
        )

        // Optional: Add some accent lines or an inner element for more detail
        val innerPath = Path().apply {
            moveTo(centerX, height * 0.3f) // Start near the top center
            quadraticTo(
                x1 = width * 0.4f, y1 = centerY,
                x2 = centerX, y2 = height * 0.7f
            )
            quadraticTo(
                x1 = width * 0.6f, y1 = centerY,
                x2 = centerX, y2 = height * 0.3f
            )
            close()
        }


        drawPath(
            path = innerPath,
            color = colorCenter.copy(alpha = 0.6f), // Semi-transparent white
        )

        // Optional: A small circle at the center
//        drawCircle(
//            color = onPrimary,
//            radius = width * 0.05f,
//            center = Offset(centerX, centerY)
//        )
    }
}

