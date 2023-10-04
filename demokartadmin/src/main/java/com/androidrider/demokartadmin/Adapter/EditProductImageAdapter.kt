package com.androidrider.demokartadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.Model.EditProductImageModel
import com.androidrider.demokartadmin.databinding.AddProductRecyclerImageLayoutBinding
import com.bumptech.glide.Glide

class EditProductImageAdapter(val context: Context, var list:ArrayList<EditProductImageModel>):
    RecyclerView.Adapter<EditProductImageAdapter.EditProductImageViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditProductImageViewholder {
        val binding = AddProductRecyclerImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditProductImageViewholder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: EditProductImageViewholder, position: Int) {

        val imageList = list[position]

        Glide.with(context).load(imageList.imageUrl).into(holder.binding.itemImage)
    }

    class EditProductImageViewholder(val binding : AddProductRecyclerImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}