package appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Live Agent", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().defaultMinSize(
                    minHeight = 100.dp
                ),
                value = state.message,
                onValueChange = {
                    onAction(ConversationalAIMessageAction.OnMessageChanged(it))
                },
                label = {
                    Text(text = "Message")
                },
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
                )
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(ConversationalAIMessageAction.SendMessage)
                }) {
                if (state.isLoading) {
                    Text(text = "Sending...")
                    Spacer(modifier = Modifier.width(16.dp))
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = "Send")
                }
            }
        }

    }
}