package com.androidrider.demokart.Adapter

import CartModel
import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.databinding.LayoutCartItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore


class CartAdapter(val context: Context,
                  val list: MutableList<CartModel>,
                  val firestore: FirebaseFirestore,
                  val currentUserNumber: String) :RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutCartItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return CartViewHolder(view)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {


        val listData = list[position]

        Glide.with(context).load(listData.productImage).into(holder.binding.imgCart)

        holder.binding.cartTvName.text = listData.productName
        holder.binding.tvProductSp.text = "₹${listData.productSp}"
        val productMrp= listData.productMrp

        // Set MRP with strikethrough effect
        val mrpText = "₹$productMrp"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(StrikethroughSpan(),0,mrpText.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
        holder.binding.tvProductMrp.text = spannable


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", listData.productId)
            context.startActivity(intent)
        }

        holder.binding.iconDeleteItem.setOnClickListener {
            removeItemFromCart(position)
        }


    }

    private fun removeItemFromCart(position: Int) {

        val listData = list[position]

        val productId = listData.productId
        val cartCollectionRef = firestore.collection("Users")
            .document(currentUserNumber)
            .collection("Cart")

        cartCollectionRef.document(productId!!)
            .delete()
            .addOnSuccessListener {
                list.removeAt(position)
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle the failure to delete the item from Firestore
                Log.e("CartAdapter", "Failed to delete item from Firestore: $productId", e)
            }
    }


    class CartViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}