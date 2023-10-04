package com.androidrider.demokart.Activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityLoginBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.github.ybq.android.spinkit.style.RotatingCircle
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var progressBar: ProgressBar
    lateinit var dialog : Dialog

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        // Check if the user is already logged in
        if (isLoggedIn()) {
            navigateToMain()
            return
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnSignIn.setOnClickListener {

            val phoneNumber = binding.edtPhoneNumber.text.toString()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please provide Number", Toast.LENGTH_SHORT).show()
            }else if (phoneNumber.length != 10 ){
                Toast.makeText(this, "Phone number should be 10 digit", Toast.LENGTH_SHORT).show()
            }else {
                sendOTP(phoneNumber)
            }
        }
    }



    private fun sendOTP(number: String) {

        //Progressbar Code
//        progressBar = binding.spinKit
//        val doubleBounce: Sprite = Circle()
//        progressBar.indeterminateDrawable = doubleBounce
//        progressBar.visibility = View.VISIBLE
        dialog.show()


        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+91$number") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
        override fun onVerificationFailed(e: FirebaseException) {}
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {

            saveLoginStatus(true)

//            progressBar.visibility = View.GONE
            dialog.dismiss()

            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("number", binding.edtPhoneNumber.text.toString())
            startActivity(intent)
        }
    }

    // for the login
    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun navigateToMain() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//        startActivity(Intent(this@LoginActivity, NotificationTestActivity::class.java))
        finish()
    }

}