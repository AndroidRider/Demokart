package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.Model.AddProductModel
import com.androidrider.demokart.databinding.ItemProductCategoryLayoutBinding
import com.bumptech.glide.Glide

class ProductCategoryAdapter(val context: Context, val list: ArrayList<AddProductModel>):
    RecyclerView.Adapter<ProductCategoryAdapter.ProductCategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        val binding = ItemProductCategoryLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {

        val listData = list[position]

        Glide.with(context).load(listData.productCoverImage).into(holder.binding.imageView4)

        holder.binding.textView4.text = listData.productName
        holder.binding.textView5.text = "₹${listData.productSp}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", listData.productId)
            context.startActivity(intent)
        }
    }

    class ProductCategoryViewHolder(val binding: ItemProductCategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}