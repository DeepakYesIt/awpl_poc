package com.bussiness.awpl

sealed  class ChatItem {

    data class DateHeader(val date: String) : ChatItem()
    data class MessageItem(val message: ChatMessage) : ChatItem()
}