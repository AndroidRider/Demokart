package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Model.LikeModel
import com.androidrider.demokart.databinding.LayoutProductItemBinding
import com.bumptech.glide.Glide


class LikeAdapter(private val context: Context,
                  private val list: ArrayList<LikeModel>) :
    RecyclerView.Adapter<LikeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutProductItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = list[position]

        holder.binding.tvProductName.text = currentItem.productName
        holder.binding.tvProductMrp.text = "₹${currentItem.productMrp}"
        holder.binding.tvProductSp.text = "₹${currentItem.productSp}"
        Glide.with(context) .load(currentItem.productCoverImage) .into(holder.binding.productImageView)


    }


    inner class ViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        }

}
