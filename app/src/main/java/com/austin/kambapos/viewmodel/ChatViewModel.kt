package com.austin.kambapos.viewmodel

import androidx.lifecycle.ViewModel
import com.austin.kambapos.data.model.ChatMessage
import com.google.gson.Gson
import com.pusher.client.Pusher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val pusher: Pusher
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        pusher.connect(object : com.pusher.client.connection.ConnectionEventListener {
            override fun onConnectionStateChange(change: com.pusher.client.connection.ConnectionStateChange) {
                android.util.Log.d("PUSHER", "Estado: ${change.currentState}")
            }
            override fun onError(message: String, code: String?, e: Exception?) {
                android.util.Log.e("PUSHER", "Erro: $message | code: $code | ex: $e")
            }
        }, com.pusher.client.connection.ConnectionState.ALL)

        // Evita erro de "Already subscribed" ao recriar o ViewModel
        if (pusher.getChannel("public-chat") != null) {
            pusher.unsubscribe("public-chat")
        }

        pusher
            .subscribe("public-chat")
            .bind("new-message") { event ->
                android.util.Log.d("PUSHER", "Mensagem recebida: ${event.data}")
                val msg = Gson().fromJson(event.data, ChatMessage::class.java)
                _messages.update { current -> current + msg }
            }
    }

    override fun onCleared() {
        super.onCleared()
        pusher.unsubscribe("public-chat")
        pusher.disconnect()
    }
}