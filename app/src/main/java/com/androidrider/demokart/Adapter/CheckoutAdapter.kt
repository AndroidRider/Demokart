package com.androidrider.demokart.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Model.CheckoutModel

import com.androidrider.demokart.databinding.RecyclerCheckoutLayoutBinding
import com.bumptech.glide.Glide

class CheckoutAdapter(val context: Context, private val cartItems: List<CheckoutModel>) :
    RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = RecyclerCheckoutLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = cartItems[position]

        // Bind data to views in the ViewHolder
        holder.binding.cartTvName.text = data.productName
        holder.binding.tvProductSp.text = "₹${data.productSp}"
        holder.binding.tvQuantity.text = data.quantity.toString()
        holder.binding.tvProductMrp.text = ""
        // Bind other data to views as needed

        Glide.with(context).load(data.productImage).into(holder.binding.imgCart)


    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class ViewHolder(val binding: RecyclerCheckoutLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}
