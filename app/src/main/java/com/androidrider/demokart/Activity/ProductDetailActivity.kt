package com.androidrider.demokart.Activity

import CartModel
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.widget.ImageView
import android.widget.Toast
import com.androidrider.demokart.Adapter.ProductAdapter
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityProductDetailBinding
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProductDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductDetailBinding
    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)

        getAddress() // for the delivery address
        getProductDetails(intent.getStringExtra("id"))

    }

    private fun getAddress() {
        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "")!!)
            .get().addOnSuccessListener { data ->

                var phoneNumber = data.getString("phoneNumber") ?: ""
                var email = data.getString("email") ?: ""
                var name = data.getString("name") ?: ""
                var address = data.getString("address") ?: ""
                var city = data.getString("city") ?: ""
                var state = data.getString("state") ?: ""
                var pinCode = data.getString("pinCode") ?: ""

                name= if (name.isNotEmpty()) name else "Name"
                address= if (address.isNotEmpty()) address else "Address"
                city= if (city.isNotEmpty()) city else ""
                state= if (state.isNotEmpty()) state else ""
                pinCode= if (pinCode.isNotEmpty()) pinCode else ""


                val fullAddress = buildString {
                    append(name)
                    append("\n")
                    append(address)
                    if (address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && pinCode.isNotEmpty()) {
                        append(", ")
                    }
                    append(city)
                    append("\n")
                    append(state)
                    if (address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && pinCode.isNotEmpty()) {
                        append(" - ")
                    }
                    append(pinCode)
                }
                binding.textViewAddress.text = "Deliver to - $fullAddress"

            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    /*   For Firebase   */
    private fun getProductDetails(proId: String?) {

        Firebase.firestore.collection("Products").document(proId!!).get()
            .addOnSuccessListener {

                // For Slideshow
                val list = it.get("productImages") as ArrayList<String>
                val slideList = ArrayList<SlideModel>()
                for (data in list) {
                    slideList.add(SlideModel(data))
                }
                binding.imageSlider.setImageList(slideList)

                val productName = it.getString("productName")
                val productSP = it.getString("productSp")
                val productMRP = it.getString("productMrp")
                val productDesc = it.getString("productDescription")
                val productFeatures = it.getString("productFeatures")



                binding.textViewPName.text = productName
                binding.textViewPSP.text = ("₹$productSP")
                binding.textViewTotalPrice.text = ("₹$productSP")
                binding.textViewDescription.text = productDesc
                binding.textViewFeature.text = productFeatures



                // Set MRP with strikethrough effect
                val mrpText = "₹$productMRP"
                val spannable = SpannableString(mrpText)
                spannable.setSpan(StrikethroughSpan(),0,mrpText.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.textViewMRP.text = spannable


                // Find the star icons in the ProductDetailsActivity layout
                val productRating = intent.getDoubleExtra("starIcons", 0.0)
//                binding.textViewFeature.text = productRating.toString()

                var starIcons: List<ImageView> = listOf(
                    findViewById(R.id.star1),
                    findViewById(R.id.star2),
                    findViewById(R.id.star3),
                    findViewById(R.id.star4),
                    findViewById(R.id.star5)
                )
                // for star icon
                ProductAdapter.updateStarIcons(starIcons, productRating, this)



                // Cart Action Method calling
                cartAction(proId, productName, productSP, productMRP, it.getString("productCoverImage"))

                binding.buyNowButton.setOnClickListener {
                    var totalCost = 0
                    var formattedPrice = productSP!!.replace(",", "").toIntOrNull()
                    if (formattedPrice != null) {
                        totalCost = formattedPrice
                    }
                    // In your ProductDetailActivity, when launching the CheckoutActivity
                    val intent = Intent(this, AddressActivity::class.java)
                    intent.putExtra("productId", proId) // single product ID
                    intent.putExtra("totalCost", totalCost.toString()) // total cost as a string
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


    private fun cartAction(proId: String,productName: String?,productSp: String?, productMrp : String?,coverImg: String? ) {

        val currentUserNumber = preferences.getString("number", "")
        val documentRef = Firebase.firestore.collection("Users")
            .document(currentUserNumber!!).collection("Cart")
        documentRef.whereEqualTo("productId", proId).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    binding.AddToCartButton.text = "Go to Cart"
                } else {
                    binding.AddToCartButton.text = "Add to Cart"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to check cart", Toast.LENGTH_SHORT).show()
            }

        // Set a click listener for the Add to Cart button
        binding.AddToCartButton.setOnClickListener {
            if (binding.AddToCartButton.text == "Go to Cart") {
                openCart()
            } else {
                addToCart(proId, productName, productSp, productMrp, coverImg)
            }
        }
    }

    private fun addToCart(proId: String?, productName: String?, productSp: String?, productMrp: String?, coverImg: String?) {

        val data = CartModel(proId, productName, productSp, productMrp, coverImg)
        val currentUserNumber = preferences.getString("number", "")
        val documentRef = Firebase.firestore.collection("Users")
            .document(currentUserNumber!!).collection("Cart")
            .document(proId!!)
        documentRef.set(data)
            .addOnSuccessListener {
                binding.AddToCartButton.text = "Go to Cart"
                Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add product to cart", Toast.LENGTH_SHORT).show()
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