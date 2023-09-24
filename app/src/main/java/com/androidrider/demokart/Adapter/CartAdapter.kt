package com.androidrider.demokart.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.demokart.Activity.ProductDetailActivity
import com.androidrider.demokart.databinding.LayoutCartItemBinding
import com.androidrider.demokart.roomdb.MyDatabase
import com.androidrider.demokart.roomdb.ProductModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartAdapter(val context: Context, val list: List<ProductModel>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutCartItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val listData = list[position]


        val productName = listData.productName
        val productSp = listData.productSp
        val productImage = listData.productImage

        Glide.with(context).load(productImage).into(holder.binding.imgCart)
        holder.binding.cartTvName.text = productName
        holder.binding.cartTvSellingPrice.text = "₹$productSp"
//        holder.binding.cartTvSellingPrice.text = productSp

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", listData.productId)
            context.startActivity(intent)
        }


        val dao = MyDatabase.getInstance(context).productDao()
        holder.binding.imageView5.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                dao.deleteProduct(
                    ProductModel(
                        listData.productId,
                        listData.productName,
                        listData.productImage,
                        listData.productSp
                    )
                )
            }
        }

    }

    class CartViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}