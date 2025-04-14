package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemDateHeaderBinding
import com.bussiness.awpl.databinding.ItemNotificationBinding
import com.bussiness.awpl.model.NotificationModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val notifications: List<NotificationModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private val groupedNotifications = mutableListOf<Any>()

    init {
        groupNotificationsByDate()
    }

    private fun groupNotificationsByDate() {
        var lastDate = ""
        for (notification in notifications) {
            val formattedDate = formatDate(notification.date)
            if (formattedDate != lastDate) {
                groupedNotifications.add(formattedDate) // Add header first
                lastDate = formattedDate
            }
            groupedNotifications.add(notification) // Add notification item
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (groupedNotifications[position] is String) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = ItemDateHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DateHeaderViewHolder(binding)
        } else {
            val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            NotificationViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DateHeaderViewHolder) {
            holder.bind(groupedNotifications[position] as String) // Fixed `items` reference
        } else if (holder is NotificationViewHolder) {
            holder.bind(groupedNotifications[position] as NotificationModel) // Fixed `items` reference
        }
    }

    override fun getItemCount(): Int = groupedNotifications.size

    // ViewHolder for Notification Item
    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationModel) {
            binding.tvNotificationTitle.text = notification.title
            binding.tvNotificationDescription.text = notification.description
            binding.tvTime.text = notification.time
            binding.ivNotificationIcon.setImageResource(notification.icon)
        }
    }

    // ViewHolder for Date Header
    inner class DateHeaderViewHolder(private val binding: ItemDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            binding.dayTxt.text = date
        }
    }

    // Convert "YYYY-MM-DD" to "Today", "Yesterday", or actual date
    private fun formatDate(date: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()

        val notificationDate = Calendar.getInstance().apply {
            time = sdf.parse(date)!!
        }

        return when {
            notificationDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    notificationDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "Today"

            notificationDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    notificationDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1 -> "Yesterday"

            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(notificationDate.time)
        }
    }

    private fun markNotificationsAsRead(date: String) {
        for (i in groupedNotifications.indices) {
            if (groupedNotifications[i] is NotificationModel) {
                val notification = groupedNotifications[i] as NotificationModel
                if (formatDate(notification.date) == date) {
                    // Example: Change text color to gray (or handle logic to mark as read)
                    notification.isRead = true
                }
            }
        }
        notifyDataSetChanged()
    }
}
