package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.Model.ProductModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.LayoutProductItemBinding
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProductAdapter(
    val context: Context, val list: ArrayList<ProductModel>, val firestore: FirebaseFirestore,
    private val currentUserNumber: String) :
    RecyclerView.Adapter<ProductAdapter.AddProductViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductViewHolder {
        val binding = LayoutProductItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return AddProductViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AddProductViewHolder, position: Int) {

        val data = list[position]


        val productId = data.productId
        val productName = data.productName
        val productSP = data.productSp
        val productMRP = data.productMrp
        val productImage = data.productCoverImage
        val productDesc = data.productDescription

        Glide.with(context).load(productImage).into(holder.binding.productImageView)

        // Convert rating to star rating
        updateStarIcons(holder.starIcons, data.rating, context)

        holder.binding.tvProductName.text = productName
        holder.binding.tvProductSp.text = "₹$productSP"

        // Set MRP with strikethrough effect
        val mrpText = "₹$productMRP"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(StrikethroughSpan(),0,mrpText.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.binding.tvProductMrp.text = spannable

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", data.productId)
            intent.putExtra("starIcons", data.rating)
            context.startActivity(intent)
        }


    }


    inner class AddProductViewHolder(val binding: LayoutProductItemBinding) :
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

    // for star icon
    companion object{
        fun updateStarIcons(starIcons: List<ImageView>, rating: Double, context:Context) {

            val fullStarDrawable = ContextCompat.getDrawable(context, R.drawable.ic_star_filled)!!
            val halfStarDrawable = ContextCompat.getDrawable(context, R.drawable.ic_star_half)!!
            val emptyStarDrawable = ContextCompat.getDrawable(context, R.drawable.ic_star_empty)!!

            val integerPart = rating.toInt()
            val decimalPart = rating - integerPart

            for (i in 0 until 5) {
                if (i < integerPart) {
                    starIcons[i].setImageDrawable(fullStarDrawable)
                } else if (i == integerPart && decimalPart >= 0.25) {
                    starIcons[i].setImageDrawable(halfStarDrawable)
                } else {
                    starIcons[i].setImageDrawable(emptyStarDrawable)
                }
            }
        }
    }
}
