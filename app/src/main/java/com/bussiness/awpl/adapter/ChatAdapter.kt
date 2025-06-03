package com.bussiness.awpl.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.ChatItem
import com.bussiness.awpl.ChatMessage
import com.bussiness.awpl.R

class ChatAdapter(private var messages: List<ChatItem>,
                  private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var senderImageUrl =""
    var receiverImageUrl =""

    companion object {
        private const val TEXT_RECEIVED = 0
        private const val TEXT_SENT = 1
        private const val IMAGE_RECEIVED = 2
        private const val IMAGE_SENT = 3
        private const val DATE_HEADER = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = messages[position]) {
            is ChatItem.DateHeader -> DATE_HEADER
            is ChatItem.MessageItem -> {
                val message = item.message
                if (URLUtil.isValidUrl(message.message) && message.senderId == currentUserId) IMAGE_SENT
                else if (URLUtil.isValidUrl(message.message)) IMAGE_RECEIVED
                else if (message.senderId == currentUserId) TEXT_SENT
                else TEXT_RECEIVED
            }

            else -> {
                0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            DATE_HEADER -> DateHeaderViewHolder(inflater.inflate(R.layout.item_date_header_chat, parent, false))
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
//        val message = messages[position]
//        if (holder is TextMessageViewHolder) {
//            holder.bind(message)
//        } else if (holder is ImageMessageViewHolder) {
//            holder.bind(message)
//        }
        when (val item = messages[position]) {
            is ChatItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item.date)
            is ChatItem.MessageItem -> {
                val message = item.message
                when (holder) {
                    is TextMessageViewHolder -> holder.bind(message)
                    is ImageMessageViewHolder -> holder.bind(message)
                }
            }
        }
    }

    inner class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateHeaderText: TextView = itemView.findViewById(R.id.dateHeaderText)
        fun bind(date: String) {
            dateHeaderText.text = date
        }
    }

    inner class TextMessageViewHolder(itemView: View,var type:Int) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        private val timeTv :TextView = itemView.findViewById(R.id.textTime)

        fun bind(message: ChatMessage) {
            message.date?.let {
                Log.d("TESTINGtIME",it)
            }
            message.time?.let {
                Log.d("TESTINGtIME",it+"time")
                timeTv.text = it
            }
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
        private val timeTv :TextView = itemView.findViewById(R.id.imageTime1)

        fun bind(message: ChatMessage) {

            Glide.with(itemView.context)
                .load(message.imageUrl)
                .placeholder(R.drawable.ic_not_found_img)
                .into(imageMessage)

            message.time?.let {
                Log.d("TESTINGtIME",it+"time")
                timeTv.text = it
            }

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
                .circleCrop().into(profileImage)
        }

    }


     fun submitList( messages: List<ChatItem>){
         this.messages = messages
         notifyDataSetChanged()
     }
}