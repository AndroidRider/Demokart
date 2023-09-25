package com.androidrider.demokart.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Model.StatusModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.OrderStatusLayoutBinding
import com.bumptech.glide.Glide


class StatusAdapter(val context: Context, val list: ArrayList<StatusModel>) :
    RecyclerView.Adapter<StatusAdapter.OrderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val binding =
            OrderStatusLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        val listData = list[position]

        holder.binding.productTitle.text = listData.name
        holder.binding.productPrice.text = "₹${listData.price}"

        val productImage = listData.coverImage
        Glide.with(context).load(productImage).into(holder.binding.imageView)


        when (list[position].status) {
            "Ordered" -> {
               holder.binding.productStatus.text = "Ordered"
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.orderedTextColor))
            }

            "Dispatched" -> {
                holder.binding.productStatus.text = "Dispatched"
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.dispatchedTextColor))
            }

            "Delivered" -> {
                holder.binding.productStatus.text = "Delivered"
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.deliveredTextColor))
            }

            "Canceled" -> {
                holder.binding.productStatus.text = "Canceled"
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.canceledTextColor))
            }
        }
    }

    class OrderViewHolder(val binding: OrderStatusLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}