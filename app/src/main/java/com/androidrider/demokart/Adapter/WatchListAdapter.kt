package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.LayoutCartItemBinding
import com.androidrider.demokart.databinding.LayoutProductItemBinding
import com.androidrider.demokart.roomdb.MyDatabase
import com.androidrider.demokart.roomdb.ProductModel
import com.androidrider.demokart.roomdb.WatchListModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WatchListAdapter(val context: Context, val list: List<WatchListModel>) :
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

        holder.binding.root.tag = listData // Set the data item as the tag

        val productImage = listData.productImage
        val productName = listData.productName
        val productMrp = listData.productMrp
        val productSp = listData.productSp

        holder.binding.textView.text = productName
        holder.binding.textView2.text = "₹$productSp"

        Glide.with(context).load(productImage).into(holder.binding.imageView2)

        // Set MRP with strikethrough effect
        val mrpText = "₹$productMrp"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(StrikethroughSpan(), 0, mrpText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.binding.textView3.text = spannable


        // When click it on Item go to product detail activity
        holder.binding.imageView2.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", listData.productId)
            context.startActivity(intent)
        }

    }

    class WatchListViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.Favourite.setOnClickListener {
                val dao = MyDatabase.getInstance(binding.root.context).watchListDao()
                val listData = binding.root.tag as WatchListModel // Get the associated ProductModel

                GlobalScope.launch(Dispatchers.IO) {
                    dao.deleteProduct(
                        WatchListModel(
                        listData.productId,
                        listData.productName,
                        listData.productImage,
                        listData.productMrp,
                        listData.productSp
                    )
                    )
                    // Show toast on the main thread
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(binding.root.context, "Removed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}