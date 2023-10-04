package com.androidrider.demokartadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.Model.BrandModel
import com.androidrider.demokartadmin.Model.CategoryModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.ItemCategoryLayoutBinding
import com.androidrider.demokartadmin.databinding.RecycleBrandLayoutBinding
import com.bumptech.glide.Glide

class BrandAdapter(val context: Context, val list: ArrayList<BrandModel>) :
    RecyclerView.Adapter<BrandAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.recycle_brand_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvBrandName.text = list[position].brand

        Glide.with(context).load(list[position].image).into(holder.binding.imgBrandImage)
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var binding = RecycleBrandLayoutBinding.bind(view)
    }

}