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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.MainActivity
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.Model.ProductModel
import com.androidrider.demokart.databinding.CategoryProductLayoutBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryProductAdapter(val context: Context, val list: ArrayList<ProductModel>,
                             val firestore: FirebaseFirestore,
                             val currentUserNumber: String):
    RecyclerView.Adapter<CategoryProductAdapter.ProductCategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        val binding = CategoryProductLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {

        val listData = list[position]

        val coverImage = listData.productCoverImage
        Glide.with(context).load(coverImage).into(holder.binding.imageView)

        val productId = listData.productId
        val productName = listData.productName
        val productMrp = listData.productMrp
        val productSp = listData.productSp

        holder.binding.productTitle.text = productName
        holder.binding.tvProductSP.text = "₹$productSp"

        // Set MRP with strikethrough effect
        val mrpText = "₹$productMrp"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(StrikethroughSpan(),0,mrpText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
        holder.binding.tvProductMRP.text = spannable


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", productId)
            context.startActivity(intent)
        }

        // Cart Action Method calling
        cartAction(holder!!, productId, productName, productSp, productMrp, coverImage)
    }

    private fun cartAction(
        holder: ProductCategoryViewHolder, proId: String?,
        productName: String?,
        productSp: String?,
        productMrp: String?,
        coverImg: String? ) {

        val preferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")

        // Create references to the user's document and cart collection
        val userDocumentRef = Firebase.firestore.collection("Users")
            .document(currentUserNumber!!)
        val cartCollectionRef = userDocumentRef.collection("Cart")

        // Check if the product with the given ID exists in the user's cart collection
        cartCollectionRef.whereEqualTo("productId", proId).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Product exists in the cart
                   holder.binding.addButton.text = "Go to Cart"
                } else {
                    // Product doesn't exist in the cart
                    holder.binding.addButton.text = "Add to Cart"
                }
            }
            .addOnFailureListener { e ->
                // Handle the failure to check the cart
                Toast.makeText(context, "Failed to check cart", Toast.LENGTH_SHORT).show()
            }

        // Set a click listener for the Add to Cart button
        holder.binding.addButton.setOnClickListener {
            if (holder.binding.addButton.text == "Go to Cart") {
                openCart()
            } else {
                addToCart(holder, proId, productName, productSp, productMrp, coverImg)
            }
        }
    }

    private fun addToCart(holder: ProductCategoryViewHolder, proId: String?, productName: String?, productSp: String?, productMrp: String?, coverImg: String?) {

        val data = CartModel(proId, productName, productSp, productMrp, coverImg)
        val preferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")
        val userDocumentRef = Firebase.firestore.collection("Users")
            .document(currentUserNumber!!)
        val cartCollectionRef = userDocumentRef.collection("Cart")
        val productDocumentRef = cartCollectionRef.document(proId!!)
        productDocumentRef.set(data)
            .addOnSuccessListener {
                holder.binding.addButton.text = "Go to Cart"
                Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add product to cart", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun removeItemFromCart(position: Int) {
//
//        val listData = list[position]
//
//        val productId = listData.productId
//        val cartCollectionRef = firestore.collection("Users")
//            .document(currentUserNumber)
//            .collection("Cart")
//
//        cartCollectionRef.document(productId!!).delete()
//            .addOnSuccessListener {
//                list.removeAt(position)
//                notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                // Handle the failure to delete the item from Firestore
//                Log.e("CategoryAdapter", "Failed to delete item from Firestore: $productId", e)
//            }
//    }

    private fun openCart() {
        val preference = context.getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()
        context.startActivity(Intent(context, MainActivity::class.java))
    }

    class ProductCategoryViewHolder(val binding: CategoryProductLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}