package com.androidrider.demokart.Activity


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Adapter.CheckoutAdapter
import com.androidrider.demokart.Model.CheckoutModel
import com.androidrider.demokart.databinding.ActivityCheckoutBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CheckoutActivity : AppCompatActivity() {

    lateinit var binding: ActivityCheckoutBinding

    val productsList = mutableListOf<CheckoutModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("productId")
        var productIds = intent.getStringArrayListExtra("productIds") ?: emptyList<String>()



        fetchProductsByIds(productIds!!)

        if (productId != null) {
            getSingleProduct(productId)
            binding.singleDataCard.visibility = View.VISIBLE
            binding.recyclerView.visibility = GONE
        }





        val totalCost = intent.getStringExtra("totalCost")
        binding.tvItemPrice.text =  "₹${totalCost}.00"
        binding.tvTotalPrice.text = "₹${totalCost}.00"

        loadUserInfo()

        binding.proceedButton.setOnClickListener {

            val myIntent = Intent(this, PaymentActivity::class.java)
            myIntent.putStringArrayListExtra("productIds", intent.getStringArrayListExtra("productIds"))
            myIntent.putExtra("totalCost", totalCost)

            myIntent.putExtra("productId", intent.getStringExtra("productId"))
            myIntent.putExtra("totalCost", totalCost)


            // Add quantity information to the intent
            val quantityList = ArrayList<String>()
            for (product in productsList) {
                quantityList.add((product.quantity ?: "0").toString())
            }
            myIntent.putStringArrayListExtra("quantities", quantityList)


            startActivity(myIntent)

        }

        binding.imgEdit.setOnClickListener {

//            val intent = Intent(this, AddressActivity::class.java)
//
//            val b = Bundle()
//            b.putString("productId", productId)
//            b.putString("totalCost", totalCost.toString())
//            intent.putExtras(b)
//            startActivity(intent)

            val myIntent = Intent(this, AddressActivity::class.java)
            myIntent.putStringArrayListExtra("productIds", intent.getStringArrayListExtra("productIds"))
            myIntent.putExtra("totalCost", totalCost)

            myIntent.putExtra("productId", intent.getStringExtra("productId"))
            myIntent.putExtra("totalCost", totalCost)

            startActivity(myIntent)
            finish()

        }

    }

    private fun getSingleProduct(productId: String) {

            Firebase.firestore.collection("Products")
                .document(productId).get()
                .addOnSuccessListener {

                            val productName = it.getString("productName")
                            val productSP = it.getString("productSp")
                            val coverImage = it.getString("productCoverImage")
                            val quantity = "1"

                            Glide.with(this).load(coverImage).into(binding.imgCart)

                            binding.cartTvName.text = productName
                            binding.tvProductSp.text = "₹$productSP"
                            binding.tvQuantity.text = quantity

                }
                .addOnFailureListener {
                    // Handle errors
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }



    }

    private fun fetchProductsByIds(productIds: List<String>): List<CheckoutModel> {


        val preferences = this.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")
        // Loop through each product ID and fetch data from Firestore
        for (productId in productIds) {
            Firebase.firestore.collection("Users")
                .document(currentUserNumber!!).collection("Cart")
                .document(productId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Document exists, parse data and add it to the list
                        val product = documentSnapshot.toObject(CheckoutModel::class.java)
                        if (product != null) {
                            // Add the quantity information to the CheckoutModel
                            productsList.add(product)
                            binding.recyclerView.adapter = CheckoutAdapter(this, productsList)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.e(ContentValues.TAG, "Error fetching product with ID $productId: ${exception.message}")
                }
        }
        return productsList
    }

    private fun loadUserInfo() {

        val preferences = this.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "")!!)
            .get().addOnSuccessListener {

                binding.tvName.text = it.getString("name")
                binding.tvNumber.text = it.getString("phoneNumber")
                binding.tvAddress.text = it.getString("address")
                binding.tvState.text = it.getString("state")

                val city = it.getString("city")
                binding.tvCity.text = ", $city"

                val pinCode = it.getString("pinCode")
                binding.tvPinCode.text = "- $pinCode"

            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


}
