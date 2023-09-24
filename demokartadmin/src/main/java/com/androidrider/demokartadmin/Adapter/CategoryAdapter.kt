package com.androidrider.demokartadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.Model.CategoryModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.ItemCategoryLayoutBinding
import com.bumptech.glide.Glide

class CategoryAdapter(val context: Context, val list: ArrayList<CategoryModel>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_category_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvCategoryName.text = list[position].category

        Glide.with(context).load(list[position].img).into(holder.binding.imgCategoryImage)
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var binding = ItemCategoryLayoutBinding.bind(view)
    }

}