package com.example.htmlsample.data.repository

import com.example.htmlsample.domain.model.ChatMessage

/**
 * Contract for chat history access.
 *
 * All functions are suspend so implementations can run on a background dispatcher
 * (Room, Retrofit, etc.) without the caller needing to know the source.
 *
 * Pagination is cursor-based:
 *  - First call: [getLatestMessages]
 *  - Subsequent calls: [getOlderMessages] with the oldest currently loaded message id
 *
 * Messages are always returned **newest-first**.
 */
interface ChatRepository {

    /** Returns the [limit] most-recent messages, newest-first. */
    suspend fun getLatestMessages(limit: Int): List<ChatMessage>

    /**
     * Returns up to [limit] messages that are older than the message identified by [beforeId],
     * newest-first.  Returns an empty list when there is no more history.
     */
    suspend fun getOlderMessages(beforeId: String, limit: Int): List<ChatMessage>

    /** Removes all messages whose [ChatMessage.createdAt] is before [cutoffMs]. */
    suspend fun deleteMessagesOlderThan(cutoffMs: Long)
}