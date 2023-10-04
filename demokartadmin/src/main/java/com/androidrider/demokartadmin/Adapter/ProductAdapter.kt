package com.androidrider.demokartadmin.Adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokartadmin.Fragment.ProductFragmentDirections
import com.androidrider.demokartadmin.Model.ProductModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.LayoutProductItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProductAdapter(
    val context: Context, val list: ArrayList<ProductModel>,
    private val navController: NavController) :
    RecyclerView.Adapter<ProductAdapter.AddProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int ): ProductAdapter.AddProductViewHolder {

        val binding = LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return AddProductViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ProductAdapter.AddProductViewHolder, position: Int) {
        val data = list[position]


        val productId = data.productId
        val productName = data.productName
        val productSP = data.productSp
        val productMRP = data.productMrp
        val productImage = data.productCoverImage
        val productDesc = data.productDescription

        Glide.with(context).load(productImage).into(holder.binding.productImageView)

        holder.binding.tvProductName.text = productName
        holder.binding.tvProductSp.text = "₹$productSP"

        // Set MRP with strikethrough effect
        val mrpText = "₹$productMRP"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(StrikethroughSpan(),0,mrpText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
        holder.binding.tvProductMrp.text = spannable


        holder.itemView.setOnClickListener {
            // Navigate to EditProductFragment when an item is clicked
            val action = ProductFragmentDirections.actionProductFragmentToEditProductFragment(productId!!)
            navController.navigate(action)
        }

        // Handle long-click to delete the item
        holder.itemView.setOnLongClickListener { view ->
            showDeleteConfirmationDialog(productId!!, position)
            true // Consume the long click
        }


    }

    private fun showDeleteConfirmationDialog(productId: String, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Delete") { _, _ ->
                // User confirmed deletion, delete the product from Firestore
                deleteProduct(productId)
                // delete the product from list
                list.removeAt(position)
                notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(productId: String) {

        Firebase.firestore.collection("Products")
            .document(productId)
            .delete()
            .addOnSuccessListener {
                // Successfully deleted from Firestore
                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                // Failed to delete from Firestore
                Toast.makeText(context, "Failed to delete product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class AddProductViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}
