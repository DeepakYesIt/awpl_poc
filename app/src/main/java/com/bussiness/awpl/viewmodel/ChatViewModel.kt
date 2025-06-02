package com.bussiness.awpl.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.awpl.ChatMessage
import com.bussiness.awpl.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {


    private var chatId :String=""
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages
    private lateinit var  repo: ChatRepository

    var currentUserId :String= ""

    private lateinit var receiverId: String

    fun initChat(receiverId: String,currentUserId :String,chatId :String,repository: ChatRepository) {
        this.receiverId = receiverId
        this.chatId = chatId
        this.currentUserId = currentUserId
        this.repo=repository

        viewModelScope.launch {
            repo.observeMessages(chatId).collect {
                _messages.value = it
            }
        }
    }

    private fun fetchChatHistory() {
        viewModelScope.launch {
            repo.observeMessages(chatId).collectLatest { messageList ->
                _messages.postValue(messageList)
            }
        }
    }

    fun sendTextMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = currentUserId,
            receiverId = receiverId,
            message = text
        )
        repo.sendMessage(chatId, message)
    }

    fun sendImage(uri: Uri) {
        viewModelScope.launch {
            val url = repo.uploadImage(uri)
            val message = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderId = currentUserId,
                receiverId = receiverId,
                imageUrl = url
            )
            repo.sendMessage(chatId, message)
        }
    }
}