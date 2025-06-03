package com.bussiness.awpl
import com.google.firebase.firestore.FieldValue // Import FieldValue
data class ChatMessage(

    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    var date:String? = null,
    var time:String? = null,
    var seen :Boolean= false

)
