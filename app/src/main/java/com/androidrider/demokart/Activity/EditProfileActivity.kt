package com.androidrider.demokart.Activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityAddressBinding
import com.androidrider.demokart.databinding.ActivityEditProfileBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding

    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)

        loadUserInfo()

        binding.SaveButton.setOnClickListener {
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

    private fun validateData(
        name: String,
        number: String,
        email: String,
        address: String,
        city: String,
        state: String,
        pinCode: String
    ) {

        if (name.isEmpty() || number.isEmpty() || email.isEmpty() ||
            address.isEmpty() || city.isEmpty() || state.isEmpty() || pinCode.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            storeData(email, address, city, state, pinCode)
        }
    }

    private fun storeData(
        email: String,
        address: String,
        city: String,
        state: String,
        pinCode: String
    ) {

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

                // Create an intent to open MainActivity
                val intent = Intent(this, MainActivity::class.java)
                // Add an extra to indicate that you want to open the "Profile" fragment
                intent.putExtra("openProfileFragment", true)
                // Start the MainActivity
                startActivity(intent)
                finish() // Finish the EditProfileActivity to prevent going back to it.



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