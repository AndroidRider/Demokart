package com.androidrider.demokart.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var dialog : Dialog

    private lateinit var auth: FirebaseAuth
    private lateinit var number: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {
            number = binding.edtPhoneNumber.text?.trim().toString()
            if (number.isNotEmpty()) {
                if (number.length == 10) {
                    number = "+91 $number"
                    dialog.show()
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                    saveUserNumber()
                } else {
                    Toast.makeText(this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun updateUI(){
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                updateUI()
                Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
            }
            else {
                // Sign in failed, display a message and update the UI
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
            }
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }
        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(this@LoginActivity, "Invalid Request", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                dialog.dismiss()
                Toast.makeText(this@LoginActivity, "Too many request\nThe SMS quota for the project has been exceeded. try after 24 hrs", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Toast.makeText(this@LoginActivity, "Missing", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onCodeSent(verificationId: String,token: PhoneAuthProvider.ForceResendingToken) {
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", number)
            startActivity(intent)
            dialog.dismiss()
            Toast.makeText(this@LoginActivity, "OTP has been sent to your number", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveUserNumber() {
        val phoneNumber = binding.edtPhoneNumber.text.toString()
        val number = "+91 $phoneNumber"
        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("number", number)
        editor.apply()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }




//    private fun sendOTP(number: String) {
//
//        dialog.show()
//
//        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
//            .setPhoneNumber("+91$number") // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(this) // Activity (for callback binding)
//            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }


//    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
//        override fun onVerificationFailed(e: FirebaseException) {}
//        override fun onCodeSent(
//            verificationId: String,
//            token: PhoneAuthProvider.ForceResendingToken,
//        ) {
//            saveLoginStatus(true)
//            dialog.dismiss()
//            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
//            intent.putExtra("verificationId", verificationId)
//            intent.putExtra("number", binding.edtPhoneNumber.text.toString())
//            startActivity(intent)
//        }
//    }

    // for the login
//    private fun isLoggedIn(): Boolean {
//        return sharedPreferences.getBoolean("isLoggedIn", false)
//    }
//    private fun saveLoginStatus(isLoggedIn: Boolean) {
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("isLoggedIn", isLoggedIn)
//        editor.apply()
//    }
//


//
//    private fun navigateToMain() {
//        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//        finish()
//    }

}