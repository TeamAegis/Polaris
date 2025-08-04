package appcup.uom.polaris.features.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import appcup.uom.polaris.core.presentation.components.Robot
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.chat.domain.Message
import appcup.uom.polaris.features.chat.domain.Role
import appcup.uom.polaris.features.chat.utils.markdown.MarkdownPreview

@Composable
fun ChatMessageItem(message: Message) {
    val isUser = message.role == Role.USER
    val backgroundColor =
        if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Icon(
                imageVector = Robot,
                contentDescription = "Model",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
                    .align(Alignment.Bottom)
            )
        }

        Box(
            modifier = Modifier
                .polarisDropShadow()
                .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 280.dp)
        ) {
            if (isUser) {
                Text(
                    text = message.content,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                MarkdownPreview(text = message.content.trimIndent())
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Bottom)
            )
        }
    }
}