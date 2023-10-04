package com.androidrider.demokartadmin.Adapter

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.Model.AllOrderModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.AllOrdersItemLayoutBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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

        // For Image
        val productImage = listData.coverImage
        Glide.with(context).load(productImage).into(holder.binding.imageView)

        // For Date
        val timestamp = listData.timestamp!!.toDate()?.time ?: 0L
        val formattedTimestamp = formatTimestampToRelative(timestamp) // Format the timestamp as needed
        holder.binding.tvTimeStamp.text = formattedTimestamp


        holder.binding.cancelButton.setOnClickListener {
            updateStatus("Canceled", list[position].orderId!!)
            holder.binding.proceedButton.visibility = GONE
        }
        holder.binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(position)
        }

        when (list[position].status) {
            "Ordered" -> {
                holder.binding.proceedButton.text = "Dispatched"
                holder.binding.proceedButton.setOnClickListener {
                    updateStatus("Dispatched", list[position].orderId!!)
                    notifyDataSetChanged()
                }
            }
            "Dispatched" -> {
                holder.binding.proceedButton.text = "Delivered"
                holder.binding.proceedButton.setOnClickListener {
                    updateStatus("Delivered", list[position].orderId!!)
                    notifyDataSetChanged()
                }
            }
            "Delivered" -> {
                holder.binding.cancelButton.visibility = GONE
                holder.binding.cancelButton.isEnabled = false
                holder.binding.proceedButton.text = "Already Delivered"
                holder.binding.proceedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                notifyDataSetChanged()

            }
            "Canceled" -> {
                holder.binding.proceedButton.visibility = GONE
                holder.binding.cancelButton.isEnabled = false
                holder.binding.cancelButton.text = "Canceled"
                holder.binding.cancelButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                notifyDataSetChanged()
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

    private fun removeItem(position: Int) {

        val listData = list[position]
        val productId = listData.orderId
        val cartCollectionRef = Firebase.firestore.collection("AllOrders")
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


    // Fuction for timestamp
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
            diffMillis < DateUtils.MINUTE_IN_MILLIS -> "now"

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


    class OrderViewHolder(val binding: AllOrdersItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}