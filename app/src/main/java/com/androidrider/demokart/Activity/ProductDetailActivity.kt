package com.androidrider.demokart.Activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.androidrider.demokart.Model.AddProductModel
import com.androidrider.demokart.databinding.ActivityProductDetailBinding
import com.androidrider.demokart.roomdb.MyDatabase
import com.androidrider.demokart.roomdb.ProductDao
import com.androidrider.demokart.roomdb.ProductModel
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductDetailBinding
    private lateinit var preferences: SharedPreferences

    private lateinit var list: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        getProductDetails(intent.getStringExtra("id"))
        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        getAddress() // for the delivery address

        list = ArrayList()


    }

    private fun getAddress() {
        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "")!!)
            .get().addOnSuccessListener { data ->

                val name = data.getString("name") ?: ""
                val phoneNumber = data.getString("phoneNumber") ?: ""
                val email = data.getString("email") ?: ""
                val address = data.getString("address") ?: ""
                val city = data.getString("city") ?: ""
                val state = data.getString("state") ?: ""
                val pinCode = data.getString("pinCode") ?: ""

                val fullAddress = buildString {
                    append(name)
                    append("\n")
                    append("${address}, ${city}")
                    append("\n")
                    append("${state} - ${pinCode}")

                }
                binding.textViewAddress.text = "Deliver to - $fullAddress"

            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getProductDetails(proId: String?) {

        Firebase.firestore.collection("Products").document(proId!!).get()
            .addOnSuccessListener {

                val list = it.get("productImages") as ArrayList<String>

                val productName = it.getString("productName")
                val productSP = it.getString("productSp")
                val productMRP = it.getString("productMrp")
                val productDesc = it.getString("productDescription")

                binding.textViewPName.text = productName
                binding.textViewPSP.text = ("₹$productSP")
                binding.textViewDescription.text = productDesc
                binding.textViewTotalPrice.text = ("₹$productSP")

                // Set MRP with strikethrough effect
                val mrpText = "₹$productMRP"
                val spannable = SpannableString(mrpText)
                spannable.setSpan(
                    StrikethroughSpan(),
                    0,
                    mrpText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.textViewMRP.text = spannable

                val slideList = ArrayList<SlideModel>()
                for (data in list) {
                    slideList.add(SlideModel(data))
                }

                cartAction(proId, productName, productSP, it.getString("productCoverImage"))

                binding.imageSlider.setImageList(slideList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cartAction(
        proId: String,
        productName: String?,
        productSp: String?,
        coverImg: String?
    ) {

        val productDao = MyDatabase.getInstance(this).productDao()

        if (productDao.isExit(proId) != null) {
            binding.AddToCartButton.text = "Go to Cart"
        } else {
            binding.AddToCartButton.text = "Add to Cart"
        }

        binding.AddToCartButton.setOnClickListener {
            if (productDao.isExit(proId) != null) {
                openCart()
            } else {
                addToCart(productDao, proId, productName, productSp, coverImg)
            }
        }
    }

    private fun addToCart(
        productDao: ProductDao,
        proId: String,
        name: String?,
        productSp: String?,
        coverImg: String?
    ) {

        val data = ProductModel(proId, name, coverImg, productSp)
        lifecycleScope.launch(Dispatchers.IO) {
            productDao.insertProduct(data)
            binding.AddToCartButton.text = "Go to Cart"
        }
    }

    private fun openCart() {
        val preference = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}