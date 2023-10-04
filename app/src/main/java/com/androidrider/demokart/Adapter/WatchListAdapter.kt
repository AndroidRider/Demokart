package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.Model.WatchListModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.LayoutProductItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore


class WatchListAdapter(
    val context: Context, val list: MutableList<WatchListModel>,
    val firestore: FirebaseFirestore,
    private val currentUserNumber: String) :
    RecyclerView.Adapter<WatchListAdapter.WatchListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListViewHolder {
        val view = LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return WatchListViewHolder(view)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WatchListViewHolder, position: Int) {

        val listData = list[position]

        val productImage = listData.productImage
        val productName = listData.productName
        val productMrp = listData.productMrp
        val productSp = listData.productSp

        holder.binding.tvProductName.text = productName
        holder.binding.tvProductSp.text = "₹$productSp"

        Glide.with(context).load(productImage).into(holder.binding.productImageView)

        // Set MRP with strikethrough effect
        val mrpText = "₹$productMrp"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(StrikethroughSpan(), 0, mrpText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.binding.tvProductMrp.text = spannable


        // When click it on Item go to product detail activity
        holder.binding.productImageView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", listData.productId)
            context.startActivity(intent)
        }


//        holder.binding.Favourite.setOnClickListener {
//            removeItemFromWatchList(position)
//        }

    }

    private fun removeItemFromWatchList(position: Int) {

        val listData = list[position]

        val productId = listData.productId
        val cartCollectionRef = firestore.collection("Users")
            .document(currentUserNumber)
            .collection("WatchList")

        cartCollectionRef.document(productId!!).delete()
            .addOnSuccessListener {
                list.removeAt(position)
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle the failure to delete the item from Firestore
                Log.e("CartAdapter", "Failed to delete item from Firestore: $productId", e)
            }
    }

    class WatchListViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}