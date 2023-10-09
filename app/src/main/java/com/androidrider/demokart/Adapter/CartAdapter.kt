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
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.LayoutCartItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore


class CartAdapter(val context: Context,
                  val list: MutableList<CartModel>,
                  val firestore: FirebaseFirestore,
                  val currentUserNumber: String,
                  private var cartListener: CartListener) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


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

        ProductAdapter.updateStarIcons(holder.starIcons, listData.rating, context)

        // Set MRP with strikethrough effect
        val mrpText = "₹$productMrp"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(StrikethroughSpan(),0,mrpText.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
        holder.binding.tvProductMrp.text = spannable

       var quantity = listData.quantity
        holder.binding.tvNumber.text = quantity.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", listData.productId)
            intent.putExtra("starIcons", listData.rating)
            context.startActivity(intent)
        }

        holder.binding.iconDeleteItem.setOnClickListener {
            removeItemFromCart(position)
        }

        holder.binding.tvPlus.setOnClickListener {
            val currentQuantityStr = holder.binding.tvNumber.text.toString()
            if (currentQuantityStr.isNotEmpty()) {
                val currentQuantity = currentQuantityStr.toInt()
                val newQuantity = currentQuantity + 1
                holder.binding.tvNumber.text = newQuantity.toString()// Update the UI with the new quantity
//                quantity = newQuantity // Update the quantity in your data source (list)
                updateQuantityInFirebase(listData.productId, newQuantity)// Optionally, update the quantity in Firebase if needed


                // Update the quantity in your data source (list)
                listData.quantity = newQuantity
                // Calculate the total cost
                val totalCost = calculateTotalCost(list)
                // Update the item count and total cost in the summary TextViews immediately
                cartListener.onCartItemUpdated(totalCost)
            }
        }


        holder.binding.tvMinus.setOnClickListener {
            val currentQuantityStr = holder.binding.tvNumber.text.toString()
            if (currentQuantityStr.isNotEmpty()) {
                var currentQuantity = currentQuantityStr.toInt()
                // Ensure that the quantity doesn't go below 1
                if (currentQuantity > 1) {
                    currentQuantity--
                    holder.binding.tvNumber.text = currentQuantity.toString()// Update the UI with the new quantity
                    quantity = currentQuantity // Update the quantity in your data source (list)
                    updateQuantityInFirebase(listData.productId, currentQuantity) // Optionally, update the quantity in Firebase if needed

                    // Update the quantity in your data source (list)
                    listData.quantity = currentQuantity
                    // Calculate the total cost
                    val totalCost = calculateTotalCost(list)
                    // Update the item count and total cost in the summary TextViews immediately
                    cartListener.onCartItemUpdated(totalCost)
                }
            }
        }


    }


    private fun calculateTotalCost(cartItemList: List<CartModel>): Int {
        var totalCost = 0
        for (cartItem in cartItemList) {
            val formattedPrice = cartItem.productSp!!.replace(",", "").toIntOrNull()
            if (formattedPrice != null) {
                totalCost += formattedPrice * cartItem.quantity
            }
        }
        return totalCost
    }



    private fun updateQuantityInFirebase(productId: String?, newQuantity: Int) {
        if (productId != null) {
            val cartCollectionRef = firestore.collection("Users")
                .document(currentUserNumber)
                .collection("Cart")

            cartCollectionRef.document(productId)
                .update("quantity", newQuantity) // Assuming you have a "quantity" field in your Firestore document
                .addOnSuccessListener {
                    // Handle the successful update

                }
                .addOnFailureListener { e ->
                    // Handle the failure to update the quantity in Firestore
                    Log.e("CartAdapter", "Failed to update quantity in Firestore: $productId", e)
                }
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
                cartListener.onCartItemRemoved(listData)
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle the failure to delete the item from Firestore
                Log.e("CartAdapter", "Failed to delete item from Firestore: $productId", e)
            }
    }

    class CartViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // for star icon
        var starIcons: List<ImageView> = listOf(
            itemView.findViewById(R.id.star1),
            itemView.findViewById(R.id.star2),
            itemView.findViewById(R.id.star3),
            itemView.findViewById(R.id.star4),
            itemView.findViewById(R.id.star5)
        )
    }


    interface CartListener {
        fun onCartItemRemoved(cartItem: CartModel)
        fun onCartItemUpdated(totalCost: Int)
    }
}