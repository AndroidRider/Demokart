package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.Model.ProductModel
import com.androidrider.demokart.Model.WatchListModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.LayoutProductItemBinding
import com.androidrider.demokart.databinding.LayoutWatchlistBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class WatchListAdapter(
    val context: Context, val list: ArrayList<WatchListModel>,
    val firestore: FirebaseFirestore,
    private val currentUserNumber: String) :
    RecyclerView.Adapter<WatchListAdapter.WatchListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListViewHolder {
        val view = LayoutWatchlistBinding.inflate(LayoutInflater.from(context), parent, false)
        return WatchListViewHolder(view)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WatchListViewHolder, position: Int) {

        val listData = list[position]

//        val productId = listData.productId
        val productImage = listData.productImage
        val productName = listData.productName
        val productMrp = listData.productMrp
        val productSp = listData.productSp

        ProductAdapter.updateStarIcons(holder.starIcons, listData.rating, context)

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
            intent.putExtra("starIcons", listData.rating)
            context.startActivity(intent)
        }

        removeItemFromWatchList(holder, position)


    }



    private fun removeItemFromWatchList(holder: WatchListViewHolder, position: Int) {

        val productId = list[position].productId

        holder.binding.delete.setOnClickListener {

            val preferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
            val currentUserNumber = preferences.getString("number", "")

            val watchlistRef = Firebase.firestore.collection("Users")
                .document(currentUserNumber!!)
                .collection("WatchList")
            // Product is in the watchlist, remove it
            watchlistRef.document(productId!!).delete()
                .addOnSuccessListener {
                    list.removeAt(position)
                    notifyDataSetChanged()

                    Toast.makeText(context, "Removed from watchlist", Toast.LENGTH_SHORT).show()
                }
        }
    }

    class WatchListViewHolder(val binding: LayoutWatchlistBinding) : RecyclerView.ViewHolder(binding.root) {
        // for star icon
        var starIcons: List<ImageView> = listOf(
            itemView.findViewById(R.id.star1),
            itemView.findViewById(R.id.star2),
            itemView.findViewById(R.id.star3),
            itemView.findViewById(R.id.star4),
            itemView.findViewById(R.id.star5)
        )
    }


}