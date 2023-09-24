package com.androidrider.demokartadmin.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.databinding.ImageItemBinding

class AddProductImageAdapter(val list:ArrayList<Uri>):
    RecyclerView.Adapter<AddProductImageAdapter.AddProductImageViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductImageViewholder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddProductImageViewholder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: AddProductImageViewholder, position: Int) {
        holder.binding.itemImage.setImageURI(list[position])
    }

    class AddProductImageViewholder(val binding : ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}