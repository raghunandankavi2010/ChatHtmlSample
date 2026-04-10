package com.example.htmlsample.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.htmlsample.domain.model.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
) {
    val pagingItems = viewModel.messages.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // ── Initial load spinner ──────────────────────────────────────
                pagingItems.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // ── Initial load error ────────────────────────────────────────
                pagingItems.loadState.refresh is LoadState.Error -> {
                    val e = (pagingItems.loadState.refresh as LoadState.Error).error
                    Text(
                        text = "Failed to load messages: ${e.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                // ── Empty state ───────────────────────────────────────────────
                pagingItems.itemCount == 0 -> {
                    Text(
                        text = "No messages yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // ── Chat list ─────────────────────────────────────────────────
                else -> ChatList(pagingItems = pagingItems, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun ChatList(
    pagingItems: LazyPagingItems<ChatMessage>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        // reverseLayout = true renders item[0] (newest) at the bottom.
        // Paging's APPEND direction adds older pages to the END of the list,
        // which with reverseLayout appears at the TOP — scroll-up loads more history.
        reverseLayout = true,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // ── Messages ──────────────────────────────────────────────────────────
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.id }
        ) { index ->
            pagingItems[index]?.let { message ->
                ChatMessageItem(message = message)
            }
        }

        // ── History status bar (last DSL item → topmost with reverseLayout) ──
        item(key = "history_status") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val appendState = pagingItems.loadState.append) {
                    is LoadState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    is LoadState.NotLoading -> {
                        if (appendState.endOfPaginationReached) {
                            Text(
                                text = "— Beginning of conversation —",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is LoadState.Error -> Text(
                        text = "Couldn't load older messages. Scroll up to retry.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}