package appcup.uom.polaris.features.chat.presentation.chat

import EmptyChatMessage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.features.chat.presentation.components.ChatInputBar
import appcup.uom.polaris.features.chat.presentation.components.ChatMessageItem
import appcup.uom.polaris.features.chat.presentation.components.ShimmerMessageBubble
import appcup.uom.polaris.features.chat.presentation.components.TypingIndicator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    viewModel: ChatViewModel = koinViewModel(),
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState,
    sheetState: SheetState,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ChatEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    ChatBottomSheetImpl(
        state = state.value,
        sheetState = sheetState,
        onDismiss = onDismiss
    ) { action ->
        when (action) {
            else -> {
                viewModel.onAction(action)
            }
        }
    }
}


@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheetImpl(
    state: ChatState,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAction: (ChatAction) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state.messages.size) {
        coroutineScope.launch {
            listState.scrollToItem(
                index = state.messages.lastIndex.coerceAtLeast(0),
                scrollOffset = Int.MAX_VALUE
            )
        }
    }

    val showScrollToBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = layoutInfo.totalItemsCount
            val lastIndex = totalItems - 1

            if (lastVisible == null) return@derivedStateOf false

            val isLastItemVisible = lastVisible.index == lastIndex
            val isFullyVisible =
                isLastItemVisible && lastVisible.offset + lastVisible.size <= layoutInfo.viewportEndOffset

            !isFullyVisible
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Scaffold(
            modifier = Modifier
                .imePadding()
                .fillMaxSize(),
            bottomBar = {
                ChatInputBar(state, onAction)
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showScrollToBottom,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    PolarisIconButton(
                        icon = {
                            Icon(
                                tint = MaterialTheme.colorScheme.primary,
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(
                                    index = state.messages.lastIndex.coerceAtLeast(0),
                                    scrollOffset = Int.MAX_VALUE
                                )
                            }
                        })
                }
            }
        ) { contentPadding ->
            if (state.isLoading) {
                LazyColumn(
                    contentPadding = contentPadding,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(3) { index ->
                        ShimmerMessageBubble(isUser = index % 2 == 0)
                    }
                }
            } else if (state.messages.isEmpty()) {
                EmptyChatMessage()
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        state.messages.size,
                        key = { index -> state.messages[index].id!! }) { index ->
                        ChatMessageItem(state.messages[index])
                    }

                    if (state.isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }
        }
    }
}