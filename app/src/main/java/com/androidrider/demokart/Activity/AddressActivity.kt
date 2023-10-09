package com.androidrider.demokart.Activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityAddressBinding
import com.androidrider.demokart.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddressActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddressBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var totalCost: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)

        /* Target */
        totalCost = intent.getStringExtra("totalCost")!! // For both CartFragment and ProductDetailActivity

        loadUserInfo()

        binding.proceedButton.setOnClickListener {
            validateData(
                binding.edtUserName.text.toString(),
                binding.edtPhoneNumber.text.toString(),
                binding.edtEmail.text.toString(),
                binding.edtAddress.text.toString(),
                binding.edtCity.text.toString(),
                binding.edtState.text.toString(),
                binding.edtPinCode.text.toString()
            )
        }

    }


    private fun validateData( name: String,number: String, email: String, address: String, city: String, state: String, pinCode: String ) {

        if (name.isEmpty() || number.isEmpty() || email.isEmpty() ||
            address.isEmpty() || city.isEmpty() || state.isEmpty() || pinCode.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            storeData(email, address, city, state, pinCode)
        }
    }

    private fun storeData(email: String, address: String, city: String, state: String, pinCode: String) {

        val map = hashMapOf<String, Any>()
        map["email"] = email
        map["address"] = address
        map["city"] = city
        map["state"] = state
        map["pinCode"] = pinCode

        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "defaultValue")!!)
            .update(map)
            .addOnSuccessListener {

//                val intent1 = Intent(this, CheckoutActivity::class.java)
//                intent1.putStringArrayListExtra("productIds", intent.getStringArrayListExtra("productIds"))
//                intent1.putExtra("totalCost", totalCost)
//
//                intent1.putExtra("productId", intent.getStringExtra("productId"))
//                intent1.putExtra("totalCost", totalCost)
//                startActivity(intent1)


                val myIntent = Intent(this, CheckoutActivity::class.java)
                myIntent.putStringArrayListExtra("productIds", intent.getStringArrayListExtra("productIds"))
                myIntent.putExtra("totalCost", totalCost)

                myIntent.putExtra("productId", intent.getStringExtra("productId"))
                myIntent.putExtra("totalCost", totalCost)

                startActivity(myIntent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserInfo() {

        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "")!!)
            .get().addOnSuccessListener {
                binding.edtUserName.setText(it.getString("name"))
                binding.edtPhoneNumber.setText(it.getString("phoneNumber"))
                binding.edtEmail.setText(it.getString("email"))
                binding.edtAddress.setText(it.getString("address"))
                binding.edtCity.setText(it.getString("city"))
                binding.edtState.setText(it.getString("state"))
                binding.edtPinCode.setText(it.getString("pinCode"))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


}