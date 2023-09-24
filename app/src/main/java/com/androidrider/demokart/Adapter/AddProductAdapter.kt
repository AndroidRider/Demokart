package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.Model.AddProductModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.LayoutProductItemBinding
import com.androidrider.demokart.roomdb.MyDatabase
import com.androidrider.demokart.roomdb.WatchListDao
import com.androidrider.demokart.roomdb.WatchListModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddProductAdapter(val context: Context,  val list:ArrayList<AddProductModel>):
    RecyclerView.Adapter<AddProductAdapter.AddProductViewHolder>() {

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

        Glide.with(context).load(productImage).into(holder.binding.imageView2)

        holder.binding.textView.text = productName
        holder.binding.textView2.text = "₹$productSP"

        // Set MRP with strikethrough effect
        val mrpText = "₹${data.productMrp}"
        val spannable = SpannableString(mrpText)
        spannable.setSpan(
            StrikethroughSpan(),
            0,
            mrpText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        holder.binding.textView3.text = spannable

        holder.binding.imageView2.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }


        // Start WatchList Action
        holder.binding.Favourite.setOnClickListener {
            // Cart Action Method calling
            watchListAction(holder,data, productId!!, productName, productMRP, productSP, productImage
            )
        }

    }

    private fun watchListAction(
        holder: AddProductViewHolder,
        data: AddProductModel,
        proId: String,
        productName: String?,
        productMrp: String?,
        productSp: String?,
        coverImg: String?
    ) {
        val watchListDao = MyDatabase.getInstance(context).watchListDao()

        val productInCart = watchListDao.isProductInWatchList(data.productId!!)
        if (productInCart != null) {

            // Product is in the cart, remove it
            GlobalScope.launch(Dispatchers.IO) {
                watchListDao.deleteProduct(productInCart)
            }
            holder.binding.Favourite.setImageResource(R.drawable.ic_empty_bookmark)
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()

        } else {

            // Product is not in the cart, add it as a favorite
            addToWatchList(watchListDao, proId, productName, productMrp, productSp, coverImg)
            holder.binding.Favourite.setImageResource(R.drawable.ic_filled_bookmark)
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addToWatchList(productDao: WatchListDao,proId: String,
        name: String?,productSp: String?,productMrp: String?,productImage: String?){

        val data = WatchListModel(proId, name, productImage, productMrp, productSp)

        CoroutineScope(Dispatchers.IO).launch {
            productDao.insertProduct(data)

            }
    }



   inner class AddProductViewHolder(val binding : LayoutProductItemBinding):
       RecyclerView.ViewHolder(binding.root) {

    }
}