package com.androidrider.demokart.Adapter

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Model.MyOrderModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.MyOrderLayoutBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class MyOrderAdapter(val context: Context, val list: ArrayList<MyOrderModel>,
                     val firestore: FirebaseFirestore,
                     val currentUserNumber: String) :
    RecyclerView.Adapter<MyOrderAdapter.OrderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val binding = MyOrderLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        val listData = list[position]

        holder.binding.productTitle.text = listData.productName
        holder.binding.tvQuantity.text = listData.quantity
        holder.binding.tvTotalPrice.text = "₹${listData.totalPrice}"

        val productImage = listData.coverImage
        Glide.with(context).load(productImage).into(holder.binding.imageView)

        val timestamp = listData.timestamp!!.toDate()?.time ?: 0L
        val formattedTimestamp = formatTimestampToRelative(timestamp) // Format the timestamp as needed
        holder.binding.tvTimeStamp.text = formattedTimestamp


        holder.itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(position)
            true // Return 'true' to indicate that the long click event is handled
        }

        holder.binding.cancelButton.setOnClickListener {
            updateStatus("Canceled", list[position].orderId!!)
            holder.binding.cancelButton.text = "Canceled"
        }

        when (list[position].status) {
            "Ordered" -> {
               holder.binding.productStatus.text = "Ordered "
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.orderedTextColor))
            }
            "Dispatched" -> {
                holder.binding.productStatus.text = "Dispatched "
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.dispatchedTextColor))
            }
            "Delivered" -> {
                holder.binding.productStatus.text = "Delivered "
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.deliveredTextColor))
            }
            "Canceled" -> {
                holder.binding.productStatus.text = "Canceled "
                holder.binding.productStatus.setTextColor(ContextCompat.getColor(context, R.color.canceledTextColor))

                holder.binding.cancelButton.isEnabled = false
                holder.binding.cancelButton.text = "Canceled"
//                holder.binding.cancelButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            }
        }
    }



    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Delete") { _, _ ->
                // User confirmed deletion, delete the product from Firestore
                removeItem(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateStatus(str: String, doc: String) {

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

    private fun removeItem(position: Int) {

        val listData = list[position]
        val productId = listData.orderId
        val cartCollectionRef = firestore.collection("AllOrders")
        cartCollectionRef.document(productId!!).delete()
            .addOnSuccessListener {
                list.removeAt(position)
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle the failure to delete the item from Firestore
                Log.e("Status", "Failed to delete item from Firestore: $productId", e)
            }
    }

    private fun formatTimestampToRelative(timestampMillis: Long): String {

        val istTimeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val istCalendar = Calendar.getInstance(istTimeZone)
        val currentTimeMillis = System.currentTimeMillis()
        istCalendar.timeInMillis = timestampMillis
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


    class OrderViewHolder(val binding: MyOrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}