package appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.polarisDropShadow
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIEvent
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationalAIMessage(
    viewModel: ConversationalAIViewModel,
    snackbarHostState: SnackbarHostState,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val state = viewModel.messageState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ConversationalAIEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                ConversationalAIEvent.RecordAudioPermissionDenied -> {
                    snackbarHostState.showSnackbar("Record audio permission denied")
                }

                ConversationalAIEvent.RecordAudioPermissionDeniedPermanently -> {
                    snackbarHostState.showSnackbar("Record audio permission denied. Please enable it from settings.")
                }
                else -> {}
            }
        }
    }

    ConversationalAIMessageImpl(
        state = state.value,
        onAction = viewModel::onMessageAction,
        sheetState = sheetState,
        onDismiss = {
            viewModel.onMessageAction(ConversationalAIMessageAction.OnReset)
            onDismiss()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationalAIMessageImpl(
    state: ConversationalAIMessageState,
    onAction: (ConversationalAIMessageAction) -> Unit,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Live Agent",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = state.message,
                onValueChange = {
                    onAction(ConversationalAIMessageAction.OnMessageChanged(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                label = {
                    Text("Message")
                },
                shape = RoundedCornerShape(16.dp),
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onAction(ConversationalAIMessageAction.SendMessage)
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )


            Button(
                onClick = { onAction(ConversationalAIMessageAction.SendMessage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .polarisDropShadow()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Sending...")
                        Spacer(modifier = Modifier.width(12.dp))
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    Text("Send")
                }
            }
        }
    }
}
