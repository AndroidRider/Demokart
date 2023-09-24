package com.androidrider.demokart.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Model.StatusModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.OrdersStatusLayoutBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StatusAdapter(val context: Context, val list: ArrayList<StatusModel>) :
    RecyclerView.Adapter<StatusAdapter.OrderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val binding =
            OrdersStatusLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        holder.binding.productTitle.text = list[position].name

        val price = list[position].price
        holder.binding.productPrice.text = "₹$price"


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

    class OrderViewHolder(val binding: OrdersStatusLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}