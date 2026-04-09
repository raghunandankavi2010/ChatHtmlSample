package com.example.htmlsample.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.htmlsample.data.repository.ChatRepository
import com.example.htmlsample.data.repository.FakeChatRepository
import com.example.htmlsample.domain.model.ChatMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ChatViewModel(
    repository: ChatRepository
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> = repository.getMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    companion object {
        /**
         * Factory wired to FakeChatRepository.
         * Swap FakeChatRepository for a real implementation (e.g. RetrofitChatRepository)
         * here — or inject via Hilt/Koin — when the API is available.
         */
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ChatViewModel(FakeChatRepository()) as T
        }
    }
}