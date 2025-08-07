package appcup.uom.polaris.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

val roundness = 10.dp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeHolder: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .dropShadow(
                shape = RoundedCornerShape(roundness),
                shadow = Shadow(
                    10.dp,
                    alpha = 0.25f,
                    offset = DpOffset(0.dp, 0.dp)
                )
            )
    ) {
        CustomChatBar(
            query = query,
            onQueryChange = onQueryChange,
            placeHolder = placeHolder,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomChatBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeHolder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeHolder, color = Color.Gray) },

        trailingIcon = {
            Row(
                modifier = Modifier
                    .padding(end = 5.dp)
            ) {
                // Send to chat Button
                IconButton(
                    onClick = {
                        // code here

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "send chat icon",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                // Audio Button
                IconButton(
                    onClick = {
                        // Code here
                    }
                ) {
                    Icon(
                        imageVector = AudioWaves,
                        modifier = Modifier
                            .size(24.dp),
                        contentDescription = "Audio Chat",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        shape = RoundedCornerShape(roundness),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent, // Hides the focused outline
            unfocusedIndicatorColor = Color.Transparent, // Hides the unfocused outline
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        modifier = modifier
    )
}