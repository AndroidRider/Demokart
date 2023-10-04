package com.androidrider.demokart.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Model.UserModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityRegisterBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.tvSignIn.setOnClickListener {
            openLogin()
        }

        binding.btnSignUp.setOnClickListener {
            validateUser()
        }
    }

    private fun validateUser() {

        val phoneNumber = binding.edtPhoneNumber.text.toString()
        val userName = binding.edtName.text.toString()

        if (phoneNumber.isEmpty() || userName.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }else if (phoneNumber.length != 10){
            Toast.makeText(this, "Phone number should be 10 digit", Toast.LENGTH_SHORT).show()
        }else{
            storeData()
        }
    }

    private fun storeData() {

//        //Progressbar Code
//        val progressBar = binding.spinKit as ProgressBar
//        val doubleBounce: Sprite = Circle()
//        progressBar.indeterminateDrawable = doubleBounce
//        progressBar.visibility = View.VISIBLE
        dialog.show()

        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putString("number", binding.edtPhoneNumber.text.toString())
        editor.putString("name", binding.edtName.text.toString())
        editor.apply()

        val data = UserModel(name = binding.edtName.text.toString(),
            phoneNumber = binding.edtPhoneNumber.text.toString())


        Firebase.firestore.collection("Users").document(binding.edtPhoneNumber.text.toString())
            .set(data).addOnSuccessListener {

//                progressBar.visibility = GONE
                dialog.dismiss()

                Toast.makeText(this, "User Register Successfully", Toast.LENGTH_SHORT).show()
                openLogin()

            }.addOnFailureListener {

//                progressBar.visibility = GONE
                dialog.dismiss()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}