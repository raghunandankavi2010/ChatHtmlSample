package com.example.htmlsample.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.htmlsample.data.repository.ChatRepository
import com.example.htmlsample.domain.model.ChatMessage

/**
 * Cursor-based [PagingSource] for chat history.
 *
 * Key  = id of the oldest message in the last loaded page (used as "before" cursor).
 * null = load the latest messages (initial / refresh).
 *
 * Items are returned **newest-first** so that [reverseLayout = true] in LazyColumn
 * puts the newest message at the bottom.  Paging's APPEND direction then naturally
 * loads *older* messages, which appear at the *top* — exactly the chat scroll-up pattern.
 *
 * PREPEND is never requested ([prevKey] is always null) because we don't load newer
 * messages on scroll-down; the latest messages are always the initial page.
 */
class ChatPagingSource(
    private val repository: ChatRepository
) : PagingSource<String, ChatMessage>() {

    override fun getRefreshKey(state: PagingState<String, ChatMessage>): String? {
        // On invalidation / refresh always restart from the latest messages.
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ChatMessage> {
        return try {
            val beforeId = params.key          // null on first load
            val messages = if (beforeId == null) {
                repository.getLatestMessages(limit = params.loadSize)
            } else {
                repository.getOlderMessages(beforeId = beforeId, limit = params.loadSize)
            }

            LoadResult.Page(
                data = messages,
                prevKey = null,                // never load newer messages via prepend
                nextKey = if (messages.size < params.loadSize) {
                    null                       // fewer results than requested → end of history
                } else {
                    messages.last().id         // cursor: oldest id in this page
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
