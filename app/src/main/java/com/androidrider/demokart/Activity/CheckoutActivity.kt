package com.androidrider.demokart.Activity


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityCheckoutBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.internal.bind.TypeAdapters.URL
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {

    lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        paymentProcess()

    }

    private fun paymentProcess() {

        val razorpayCheckout = Checkout() // Initialize the Checkout instance here
        razorpayCheckout.setKeyID("rzp_test_IPDdrnFu7i9Ae0")

        val price = intent.getStringExtra("totalCost")

        try {
            val options = JSONObject()
            options.put("name", "DemoKart")
            options.put("description", "Best Ecommerce App")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")
            options.put("amount", (price!!.toInt() * 100))//pass amount in currency subunits

            val prefill = JSONObject()
            prefill.put("email", "mdarfealam15@gmail.com")
            prefill.put("contact", "7667815236")

            razorpayCheckout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_LONG).show()
        try {
            uploadData()

        } catch (e: Exception) {
            Toast.makeText(this, "onPayment-Error " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun uploadData() {
        try {
            val productIds = intent.getStringArrayListExtra("productIds")
            val productId = intent.getStringExtra("productId")

            if (productIds != null && !productIds.contains(productId)) {
                for (currentId in productIds) {
                    fetchData(currentId)

                    // Remove the item from the cart
                    val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
                    val userId = preferences.getString("number", "")!!
                    removeItemFromCart(userId, currentId)
                }
            } else if (productId != null) {
                fetchData(productId)
            }

        } catch (e: Exception) {
            Toast.makeText(this, "on Upload Data " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun removeItemFromCart(userId: String, productId: String) {
        val firestore = Firebase.firestore
        // Reference to the cart document
        val cartRef = firestore.collection("Users").document(userId)
            .collection("Cart").document(productId)
        // Delete the entire cart document
        cartRef.delete()
            .addOnSuccessListener {
                Log.d(TAG, "Cart document deleted: $productId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting cart document: $productId", e)
            }
    }

    private fun fetchData(productId: String?) {
        try {
            Firebase.firestore.collection("Products")
                .document(productId!!).get()
                .addOnSuccessListener {

                    saveData(
                        it.getString("productName"), it.getString("productCoverImage"),
                        it.getString("productSp"), productId
                    )
                }

        } catch (e: Exception) {
            Toast.makeText(this, "on Fetch Data " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun saveData(name: String?, coverImage: String?, price: String?, productId: String) {

        try {
            val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
            val data = hashMapOf<String, Any>()

            data["name"] = name!!
            data["price"] = price!!
            data["productId"] = productId
            data["coverImage"] = coverImage!!
            data["status"] = "Ordered"
            data["timestamp"] = FieldValue.serverTimestamp()
            data["userId"] = preferences.getString("number", "")!!

            val firestore = Firebase.firestore.collection("AllOrders")
            val key = firestore.document().id
            data["orderId"] = key

            firestore.document(key).set(data)
                .addOnSuccessListener {

                    // After successfully saving the order, create a notification
                    createNotification(name, price)

                    Toast.makeText(this, "Order placed", Toast.LENGTH_LONG).show()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, "on Save Data " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }


    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Error", Toast.LENGTH_LONG).show()
    }

    private fun createNotification(name: String, price: String) {

        val channelId = "order_notification_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Set your notification icon
            .setContentTitle("Order Placed!")
            .setContentText("Your order has been successfully placed.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Your order $name, Rs.$price has been successfully placed."))


        // Create a notification channel for devices running Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Order Notifications"
            val channelDescription = "Notifications for placed orders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.POST_NOTIFICATIONS )
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        notificationManagerCompat.notify(1, notificationBuilder.build())
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(null) // Unregister your broadcast receiver here
        }catch (e: Exception) {
            // Handle any exceptions if they occur
        }
    }


}
