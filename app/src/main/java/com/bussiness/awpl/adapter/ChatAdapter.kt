package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.ChatMessage
import com.bussiness.awpl.R

class ChatAdapter(private val messages: List<ChatMessage>,
                    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var senderImageUrl =""
    var receiverImageUrl =""

    companion object {
        private const val TEXT_RECEIVED = 0
        private const val TEXT_SENT = 1
        private const val IMAGE_RECEIVED = 2
        private const val IMAGE_SENT = 3
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            URLUtil.isValidUrl(message.message) && message.senderId == currentUserId -> IMAGE_SENT
            URLUtil.isValidUrl(message.message) -> IMAGE_RECEIVED
            message.senderId == currentUserId -> TEXT_SENT
            else -> TEXT_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TEXT_SENT -> TextMessageViewHolder(inflater.inflate(R.layout.item_text_sent, parent, false),
                TEXT_SENT)
            TEXT_RECEIVED -> TextMessageViewHolder(inflater.inflate(R.layout.item_text_received, parent, false),
                TEXT_RECEIVED)
            IMAGE_SENT -> ImageMessageViewHolder(inflater.inflate(R.layout.item_image_sent, parent, false),
                TEXT_SENT)
            IMAGE_RECEIVED -> ImageMessageViewHolder(inflater.inflate(R.layout.item_image_received, parent, false),
                TEXT_SENT)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is TextMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ImageMessageViewHolder) {
            holder.bind(message)
        }
    }

    inner class TextMessageViewHolder(itemView: View,var type:Int) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)

        fun bind(message: ChatMessage) {
            textMessage.text = message.message

            var url = ""
            if(type == TEXT_SENT){
                url = senderImageUrl
            }
            else {
                url = receiverImageUrl
            }

            Glide.with(itemView.context)
                .load(url)
                .placeholder(R.drawable.ic_not_found_img)
                .circleCrop()
                .into(profileImage)
        }
    }

    inner class ImageMessageViewHolder(itemView: View,var type :Int) : RecyclerView.ViewHolder(itemView) {
        private val imageMessage: ImageView = itemView.findViewById(R.id.imageMessage)
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)

        fun bind(message: ChatMessage) {
            Glide.with(itemView.context)
                .load(message.imageUrl)
                .placeholder(R.drawable.ic_not_found_img)
                .into(imageMessage)

            var url = ""
            if(type == TEXT_SENT){
                url = senderImageUrl
            }
            else {
                url = receiverImageUrl
            }
            Glide.with(itemView.context)
                .load(url)
                .placeholder(R.drawable.ic_not_found_img)
                .circleCrop()
                .into(profileImage)
        }
    }
}