package com.bussiness.awpl.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemDateHeaderBinding
import com.bussiness.awpl.databinding.ItemNotificationBinding
import com.bussiness.awpl.model.NotificationModel
import com.bussiness.awpl.model.PatinetNotification
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private var notifications: List<PatinetNotification>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private val groupedNotifications = mutableListOf<Any>()

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
        return if (notifications[position].id.isEmpty() || notifications[position].title.isEmpty()) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
              Log.d("TESTING_HEADERS","INSIDE THE HEADER MAIN")
            val binding = ItemDateHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DateHeaderViewHolder(binding)
        } else {
            Log.d("TESTING_HEADERS","INSIDE THE  MAIN VIEW")
            val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            NotificationViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DateHeaderViewHolder) {
            holder.bind(notifications[position].date) // Fixed `items` reference
        } else if (holder is NotificationViewHolder) {
            holder.bind(notifications[position]) // Fixed `items` reference
        }
    }

    override fun getItemCount(): Int = notifications.size

    // ViewHolder for Notification Item
    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: PatinetNotification) {
            binding.tvNotificationTitle.text = notification.title
            binding.tvNotificationDescription.text = notification.description
            binding.tvTime.text = notification.time

            when (notification.title) {
                "Appointment Success" -> {
                    binding.ivNotificationIcon.setImageResource(R.drawable.calendar_tick)
                }
                "Appointment Cancelled" -> {
                    binding.ivNotificationIcon.setImageResource(R.drawable.calendar_cancel_ic)
                }
                "Appointment Rescheduled" -> {
                    binding.ivNotificationIcon.setImageResource(R.drawable.calendar_edit)
                }
            }
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

    fun updateAdapter(notifications: List<PatinetNotification>){
        this.notifications = notifications
        notifyDataSetChanged()
    }


}
