package com.androidrider.demokart.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidrider.demokart.databinding.ActivityCheckoutBinding
import com.androidrider.demokart.roomdb.MyDatabase
import com.androidrider.demokart.roomdb.ProductModel
import com.bumptech.glide.load.model.DataUrlLoader.DataDecoder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {

    lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val razorpayCheckout = Checkout() // Initialize the Checkout instance here
        razorpayCheckout.setKeyID("rzp_test_IPDdrnFu7i9Ae0")

        val price = intent.getStringExtra("totalCost")

        try {
            val options = JSONObject()
            options.put("name","DemoKart")
            options.put("description","Best Ecommerce App")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
            options.put("amount",(price!!.toInt()*100))//pass amount in currency subunits

            val prefill = JSONObject()
            prefill.put("email","mdarfealam15@gmail.com")
            prefill.put("contact","7667815236")

            razorpayCheckout.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this,"Payment Success",Toast.LENGTH_LONG).show()
        try {
            uploadData()
        } catch (e: Exception) {
            Toast.makeText(this,"onPayment-Error "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun uploadData() {
        val id = intent.getStringArrayListExtra("productIds")
        for (currentId in id!!){
            fetchData(currentId)
        }
    }

    private fun fetchData(productId: String?) {
        val dao = MyDatabase.getInstance(this).productDao()
        Firebase.firestore.collection("Products")
            .document(productId!!).get()
            .addOnSuccessListener {
                lifecycleScope.launch(Dispatchers.IO){
                    dao.deleteProduct(ProductModel(productId))
                }
                saveData(it.getString("productName"), it.getString("productSp"),productId
                )
            }
    }

    private fun saveData(name: String?, price: String?, productId: String) {

        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val data = hashMapOf<String, Any>()
        data["name"] = name!!
        data["price"] = price!!
        data["productId"] = productId
        data["status"] = "Ordered"
        data["userId"] = preferences.getString("number", "")!!

        val firestore = Firebase.firestore.collection("AllOrders")
        val key = firestore.document().id
        data["orderId"] = key

        try {
            firestore.document(key).set(data)
                .addOnSuccessListener {
                    Toast.makeText(this,"Order placed",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show()
                }
        }catch (e: Exception) {
            Toast.makeText(this,"saveData "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this,"Payment Error",Toast.LENGTH_LONG).show()
    }


}

//// Online
//private fun uploadMyData() {
//    val id = intent.getStringArrayListExtra("productIds")
//    try {
//        for (currentId in id!!){
//            fetchMyData(currentId)
//
//        }
//        //fetchMyData(id) // Online
//    } catch (e: Exception) {
//        Toast.makeText(this,"uploadMyData "+ e.message,Toast.LENGTH_LONG).show()
//        e.printStackTrace()
//    }
//}

//// Online
//private fun fetchMyData(productId: String?){
//    try {
//        Firebase.firestore.collection("Products")
//            .document(productId!!).get()
//            .addOnSuccessListener {
//                saveData(it.getString("productName"),
//                    it.getString("productSp"),
//                    productId
//                )
//            }
//    } catch (e: Exception) {
//        Toast.makeText(this,"fetchMyData "+ e.message,Toast.LENGTH_LONG).show()
//        e.printStackTrace()
//    }
//}