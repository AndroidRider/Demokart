package com.androidrider.demokartadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.Model.NotificationModel
import com.androidrider.demokartadmin.databinding.NotificationRecyclerLayoutBinding
import com.bumptech.glide.Glide


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

    }

    inner class ViewHolder(val binding: NotificationRecyclerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}