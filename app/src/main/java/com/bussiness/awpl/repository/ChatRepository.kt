package com.bussiness.awpl.repository

import android.net.Uri
import android.util.Log
import com.bussiness.awpl.ChatMessage
import com.bussiness.awpl.utils.AppConstant
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
){


    fun sendMessage(chatId: String, message: ChatMessage): Task<Void> {
        return firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()
            .set(message)
    }



    fun observeMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
       // .orderBy("timestamp")
//        val subscription = firestore.collection("chats")
//            .document(chatId)
//            .collection("messages")
//            .orderBy("timestamp")
//            .addSnapshotListener { snapshots, _ ->
//                val msgs = snapshots?.documents?.mapNotNull {
//                    it.toObject(ChatMessage::class.java)
//                } ?: emptyList()
//                trySend(msgs)
//            }
//
//        awaitClose { subscription.remove() }
        var cc = chatId
        var arr = cc?.split("_")
      var   currentUserId  = arr?.get(2) ?: "0"

        val subscription = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, _ ->
                val messages = mutableListOf<ChatMessage>()
                snapshots?.documents?.forEach { doc ->
                    val msg = doc.toObject(ChatMessage::class.java)
                    msg?.let {
                        // Update seen status if not seen and it's not the sender

                        if (!it.seen && it.receiverId == currentUserId) {
                            firestore.collection("chats")
                                .document(chatId)
                                .collection("messages")
                                .document(doc.id)
                                .update("seen", true)
                        }
                        messages.add(it)
                    }
                }
                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun uploadImage(uri: Uri): String {
        val ref = storage.reference.child("chat_images/${UUID.randomUUID()}.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }



}