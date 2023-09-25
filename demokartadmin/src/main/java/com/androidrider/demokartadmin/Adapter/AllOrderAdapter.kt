package com.androidrider.demokartadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.Model.AllOrderModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.AllOrdersItemLayoutBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllOrderAdapter(val context: Context, val list: ArrayList<AllOrderModel>) :
    RecyclerView.Adapter<AllOrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val binding = AllOrdersItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
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

        holder.binding.cancelButton.setOnClickListener {
            updateStatus("Canceled", list[position].orderId!!)
            holder.binding.proceedButton.visibility = GONE
        }

        when (list[position].status) {
            "Ordered" -> {
                holder.binding.proceedButton.text = "Dispatched"

                holder.binding.proceedButton.setOnClickListener {
                    updateStatus("Dispatched", list[position].orderId!!)
                }
            }

            "Dispatched" -> {
                holder.binding.proceedButton.text = "Delivered"

                holder.binding.proceedButton.setOnClickListener {
                    updateStatus("Delivered", list[position].orderId!!)
                }
            }

            "Delivered" -> {
                holder.binding.cancelButton.visibility = GONE

                holder.binding.cancelButton.isEnabled = false
                holder.binding.proceedButton.text = "Already Delivered"
                holder.binding.proceedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green))

            }

            "Canceled" -> {
                holder.binding.proceedButton.visibility = GONE

                holder.binding.cancelButton.isEnabled = false
                holder.binding.cancelButton.text = "Canceled"
                holder.binding.cancelButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            }
        }
    }

    fun updateStatus(str: String, doc: String) {

        val data = hashMapOf<String, Any>()
        data["status"] = str
        Firebase.firestore.collection("AllOrders").document(doc).update(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }


    class OrderViewHolder(val binding: AllOrdersItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}