package appcup.uom.polaris.features.polaris.presentation.create_journey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel
import appcup.uom.polaris.features.conversational_ai.presentation.conversational_ai_message.ConversationalAIMessage
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateJourneyScreen(
    viewModel: CreateJourneyViewModel = koinViewModel(),
    conversationAIViewModel: ConversationalAIViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    CreateJourneyScreenImpl(
        state = state.value,
        conversationAIViewModel = conversationAIViewModel,
        snackbarHostState = snackbarHostState,
    ) { action ->
        when (action) {
            CreateJourneyAction.OnBackClicked -> {
                onBack()
            }

            else -> {
                viewModel.onAction(action)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateJourneyScreenImpl(
    state: CreateJourneyState,
    conversationAIViewModel: ConversationalAIViewModel,
    snackbarHostState: SnackbarHostState,
    onAction: (CreateJourneyAction) -> Unit
) {

    val chatWithAIBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sendMessageToLiveAgentBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    Scaffold(
        modifier = Modifier
            .imePadding()
            .statusBarsPadding()
            .fillMaxSize(),
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(16.dp),
                            shadow = Shadow(
                                radius = 15.dp,
                                alpha = 0.25f,
                                offset = DpOffset(0.dp, 4.dp)
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable {
                                onAction(CreateJourneyAction.OnBackClicked)
                            }
                            .padding(12.dp)

                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text("Create Journey", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            }

        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {


            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {

            }

            if (state.isSendMessageToAIBottomSheetOpen) {
//                scope
//                    .launch { bottomSheetState.hide() }
//                    .invokeOnCompletion {
//                        if (!bottomSheetState.isVisible) {
//                            openBottomSheet = false
//                        }
//                    }
                ModalBottomSheet(
                    onDismissRequest = {
                        onAction(CreateJourneyAction.OnSendMessageToAIBottomSheetClicked)
                    },
                    sheetState = chatWithAIBottomSheetState,
                ) {

                }
            }

            if (state.isSendMessageToLiveAgentBottomSheetOpen) {
                ConversationalAIMessage(conversationAIViewModel, snackbarHostState, sendMessageToLiveAgentBottomSheetState) {
                    onAction(CreateJourneyAction.OnSendMessageToLiveAgentBottomSheetClicked)
                }
            }

            HorizontalFloatingToolbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -ScreenOffset)
                    .dropShadow(
                        shape = RoundedCornerShape(16.dp),
                        shadow = Shadow(
                            radius = 15.dp,
                            alpha = 0.25f,
                            offset = DpOffset(0.dp, 4.dp)
                        )
                    ),
                expanded = state.isToolbarExpanded,
                trailingContent = {
                    Row {
                        IconButton(onClick = {
                            onAction(CreateJourneyAction.OnSendMessageToAIBottomSheetClicked)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Message,
                                contentDescription = "Localized description"
                            )
                        }

                        IconButton(
                            onClick = {
                                onAction(CreateJourneyAction.OnSendMessageToLiveAgentBottomSheetClicked)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CallMade,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
                content = {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {
                            onAction(CreateJourneyAction.OnToolbarExpandedChanged(!state.isToolbarExpanded))
                        },
                    ) {
                        Icon(
                            if (state.isToolbarExpanded)
                                Icons.Default.ArrowBackIosNew
                            else
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        }
    }
}