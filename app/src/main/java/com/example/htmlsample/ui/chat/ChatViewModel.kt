package com.example.htmlsample.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.htmlsample.data.paging.ChatPagingSource
import com.example.htmlsample.data.repository.ChatRepository
import com.example.htmlsample.data.repository.FakeChatRepository
import com.example.htmlsample.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    companion object {
        /** Number of messages per page. */
        const val PAGE_SIZE = 10

        /** Messages older than this are deleted on startup. */
        private const val DELETE_AFTER_HOURS = 6L

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ChatViewModel(FakeChatRepository()) as T
        }
    }

    /**
     * Paged stream of chat messages, newest-first.
     *
     * On first collection:
     *  1. Deletes messages older than [DELETE_AFTER_HOURS].
     *  2. Creates a [Pager] backed by [ChatPagingSource].
     *
     * [cachedIn] keeps the loaded pages alive across recompositions and config changes.
     */
    val messages: Flow<PagingData<ChatMessage>> = flow {
        // Auto-delete stale history before the first page loads
        val cutoffMs = System.currentTimeMillis() - DELETE_AFTER_HOURS * 3_600_000L
        repository.deleteMessagesOlderThan(cutoffMs)

        val pager = Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,  // exactly one page on first open
                prefetchDistance = 1,         // trigger next page 1 item before the edge
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ChatPagingSource(repository) }
        )
        emitAll(pager.flow)
    }.cachedIn(viewModelScope)
}