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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {


    private var chatId :String="105_11_12"
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
                it.forEach {
                    it.date = formatTimestamp(it.timestamp).first
                    it.time = formatTimestamp(it.timestamp).second
                }
                _messages.postValue(it)
            }
        }
    }

    fun formatTimestamp(timestamp: Long): Pair<String, String> {
        val date = Date(timestamp)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val formattedDate = dateFormat.format(date) // "14 May 2025"
        val formattedTime = timeFormat.format(date) // "21:15"

        return Pair(formattedDate, formattedTime)
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

                senderId = currentUserId,
                receiverId = receiverId,
                imageUrl = url
            )
            repo.sendMessage(chatId, message)
        }
    }
}