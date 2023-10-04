//package com.androidrider.demokart.Adapter
//
//import android.content.ContentValues.TAG
//import android.content.Context
//import android.content.Intent
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.style.StrikethroughSpan
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.RecyclerView
//import com.androidrider.demokart.Activity.ProductDetailActivity
//import com.androidrider.demokart.Model.ProductModel
//import com.androidrider.demokart.Model.WatchListModel
//import com.androidrider.demokart.R
//import com.androidrider.demokart.databinding.LayoutProductItemBinding
//import com.bumptech.glide.Glide
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//
//class ProductAdapter(
//    val context: Context, val list: ArrayList<ProductModel>,
//    val firestore: FirebaseFirestore,
//    private val currentUserNumber: String
//) :
//    RecyclerView.Adapter<ProductAdapter.AddProductViewHolder>() {
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductViewHolder {
//        val binding = LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent, false)
//        return AddProductViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int = list.size
//
//    override fun onBindViewHolder(holder: AddProductViewHolder, position: Int) {
//
//        val data = list[position]
//
//
//        val productId = data.productId
//        val productName = data.productName
//        val productSP = data.productSp
//        val productMRP = data.productMrp
//        val productImage = data.productCoverImage
//        val productDesc = data.productDescription
//
//        Glide.with(context).load(productImage).into(holder.binding.productImageView)
//
//        holder.binding.tvProductName.text = productName
//        holder.binding.tvProductSp.text = "₹$productSP"
//
//        // Set MRP with strikethrough effect
//        val mrpText = "₹$productMRP"
//        val spannable = SpannableString(mrpText)
//        spannable.setSpan(
//            StrikethroughSpan(),
//            0,
//            mrpText.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        holder.binding.tvProductMrp.text = spannable
//
//        holder.binding.productImageView.setOnClickListener {
//            val intent = Intent(context, ProductDetailActivity::class.java)
//            intent.putExtra("id", list[position].productId)
//            context.startActivity(intent)
//        }
//
//
//
//        if (data.favorite) {
//            holder.binding.Favourite.setImageResource(R.drawable.ic_filled_bookmark)
//            removeItemFromWatchList(position)
//        } else {
//            holder.binding.Favourite.setImageResource(R.drawable.ic_empty_bookmark)
//            watchListAction(holder, productId, productName, productSP, productMRP, productImage)
//
//        }
//
//        // Cart Action Method calling
////        watchListAction(holder, productId, productName, productSP, productMRP, productImage)
//
//    }
//
//
//    /*   For Firebase   */
//    private fun watchListAction(
//        holder: AddProductViewHolder,
//        proId: String?,
//        productName: String?,
//        productSp: String?,
//        productMrp: String?,
//        coverImg: String?
//    ) {
//
//        val preferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
//        val currentUserNumber = preferences.getString("number", "")
//
//        // Create references to the user's document and cart collection
//        val userDocumentRef = Firebase.firestore.collection("Users")
//            .document(currentUserNumber!!)
//        val cartCollectionRef = userDocumentRef.collection("WatchList")
//
//        // Check if the product with the given ID exists in the user's cart collection
//        cartCollectionRef.whereEqualTo("productId", proId).get()
//            .addOnSuccessListener { querySnapshot ->
//
//
//            }
//            .addOnFailureListener { e ->
//                // Handle the failure to check the cart
//                Toast.makeText(context, "Failed to check cart", Toast.LENGTH_SHORT).show()
//            }
//
//
//        // Set a click listener for the Add to Cart button
//        holder.binding.Favourite.setOnClickListener {
//
//            addToWatchList(holder, proId, productName, productSp, productMrp, coverImg)
////            holder.binding.Favourite.setImageResource(R.drawable.ic_filled_bookmark)
//        }
//    }
//
//
//    private fun addToWatchList(
//        holder: AddProductViewHolder,
//        proId: String?,
//        name: String?,
//        productSp: String?,
//        productMrp: String?,
//        coverImg: String?
//    ) {
//
//        val data = WatchListModel(proId!!, name, productSp, productMrp, coverImg)
//        val preferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
//        val currentUserNumber = preferences.getString("number", "")
//        // Create a reference to the user's document
//        val userDocumentRef = Firebase.firestore.collection("Users")
//            .document(currentUserNumber!!)
//        // Create a "Cart" collection inside the user's document if it doesn't exist
//        val cartCollectionRef = userDocumentRef.collection("WatchList")
//
//        /*   checking */
//        cartCollectionRef.document(proId).update("favorite", true)
//
//        // Add the product to the "Cart" collection
//        val productDocumentRef = cartCollectionRef.document(proId)
//        productDocumentRef.set(data)
//            .addOnSuccessListener {
//
//
//                // Update the 'isInWatchlist' field to true when adding to watchlist
//
//
//                holder.binding.Favourite.setImageResource(R.drawable.ic_filled_bookmark)
//                Toast.makeText(context, "Product added to WatchList", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(context, "Failed to add product to cart", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun removeItemFromWatchList(position: Int) {
//
//        val listData = list[position]
//
//        val productId = listData.productId
//        val cartCollectionRef = firestore.collection("Users")
//            .document(currentUserNumber)
//            .collection("WatchList")
//
//        /*   checking */
//        // Update the 'isInWatchlist' field to false when removing from watchlist
//        cartCollectionRef.document(productId!!).update("favorite", false)
//
//        cartCollectionRef.document(productId!!).delete()
//            .addOnSuccessListener {
//                list.removeAt(position)
//                notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                // Handle the failure to delete the item from Firestore
//                Log.e("CartAdapter", "Failed to delete item from Firestore: $productId", e)
//            }
//    }
//
//
//    inner class AddProductViewHolder(val binding: LayoutProductItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//    }
//}