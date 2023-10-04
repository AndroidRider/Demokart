package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.CategoryProductActivity
import com.androidrider.demokart.Model.BrandModel
import com.androidrider.demokart.Model.CategoryModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.AllBrandViewLayoutBinding
import com.androidrider.demokart.databinding.CategoryProductLayoutBinding
import com.androidrider.demokart.databinding.LayoutCategoryItemBinding
import com.androidrider.demokart.databinding.NotificationRecyclerLayoutBinding
import com.androidrider.demokart.databinding.RecycleBrandLayoutBinding
import com.bumptech.glide.Glide

class AllBrandViewAdapter(val context: Context, val list: ArrayList<BrandModel>) :
    RecyclerView.Adapter<AllBrandViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = AllBrandViewLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvBrandName.text = list[position].brand

        Glide.with(context).load(list[position].image).into(holder.binding.brandImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CategoryProductActivity::class.java)
            intent.putExtra("brand", list[position].brand)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(val binding: AllBrandViewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}