package com.androidrider.demokart.Adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Model.NotificationModel
import com.androidrider.demokart.databinding.NotificationRecyclerLayoutBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class NotificationAdapter(val context: Context, val list: ArrayList<NotificationModel>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = NotificationRecyclerLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val listData = list[position]

        holder.binding.tvNotificationTitle.text = listData.title
        holder.binding.tvNotificationContent.text = listData.content

        Glide.with(context).load(listData.image).into(holder.binding.imgNotification)

        val timestamp = listData.timestamp?.toDate()?.time ?: 0L
        val formattedTimestamp = formatTimestampToRelative(timestamp) // Format the timestamp as needed
        holder.binding.tvTimeStamp.text = formattedTimestamp

        holder.itemView.setOnClickListener {

            Toast.makeText(context, "Clicked$listData", Toast.LENGTH_SHORT).show()
        }
    }

    fun formatTimestampToRelative(timestampMillis: Long): String {

        // Define the time zone for India Standard Time (IST)
        val istTimeZone = TimeZone.getTimeZone("Asia/Kolkata")

        // Create a Calendar instance using the IST time zone
        val istCalendar = Calendar.getInstance(istTimeZone)

        val currentTimeMillis = System.currentTimeMillis()
//        val calendar = Calendar.getInstance()
        istCalendar.timeInMillis = timestampMillis

        // Calculate the time difference in IST
        val diffMillis = currentTimeMillis - timestampMillis

        return when {
            diffMillis < DateUtils.MINUTE_IN_MILLIS -> "Now"

            diffMillis < DateUtils.DAY_IN_MILLIS ->{
                // Display time if within the current day
                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                "Today " + sdf.format(istCalendar.time)
            }
            diffMillis < (DateUtils.DAY_IN_MILLIS * 2) ->  {
                // Display time if within the previous day
                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                "Yesterday " + sdf.format(istCalendar.time)
            }
            else -> {
                // If the timestamp is older, format it as a date
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                sdf.format(istCalendar.time)
            }
        }
    }



    inner class ViewHolder(val binding: NotificationRecyclerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}