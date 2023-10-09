package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.CategoryProductActivity
import com.androidrider.demokart.Model.CategoryModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.LayoutCategoryItemBinding
import com.bumptech.glide.Glide

class CategoryAdapter(val context: Context, val list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.layout_category_item, parent, false)

        return ViewHolder(view)
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

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var binding = LayoutCategoryItemBinding.bind(view)
    }

}