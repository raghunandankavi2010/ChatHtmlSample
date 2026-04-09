package com.example.htmlsample.domain.model

data class ChatMessage(
    val id: String,
    val content: MessageContent,
    val senderName: String,
    val isMine: Boolean,
    val timestamp: String
)