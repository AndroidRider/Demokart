package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.CategoryProductActivity
import com.androidrider.demokart.Model.CategoryModel
import com.androidrider.demokart.databinding.AllBrandViewLayoutBinding
import com.androidrider.demokart.databinding.AllCategoryViewLayoutBinding
import com.bumptech.glide.Glide

class AllCategoryViewAdapter(val context: Context, val list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<AllCategoryViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = AllCategoryViewLayoutBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvCategoryName.text = list[position].category

        Glide.with(context).load(list[position].img).into(holder.binding.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CategoryProductActivity::class.java)
            intent.putExtra("cat", list[position].category)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(val binding: AllCategoryViewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}