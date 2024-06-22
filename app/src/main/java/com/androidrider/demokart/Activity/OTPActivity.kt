package com.androidrider.demokart.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.androidrider.demokart.Model.UserModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityOtpactivityBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpactivityBinding
    private lateinit var dialog : Dialog

    private lateinit var auth: FirebaseAuth
    private lateinit var OTP : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)


        auth = FirebaseAuth.getInstance()

        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        binding.tvResendOTP.setOnClickListener {
            resendVerificationCode() // method

        }

        binding.btnVerifyOTP.setOnClickListener {

            val typedOTP = binding.edtOTP.text.toString()
            if(typedOTP.isNotEmpty()){
                if (typedOTP.length == 6){
                    val credential = PhoneAuthProvider.getCredential(OTP, typedOTP)
                    dialog.show()
                    signInWithPhoneAuthCredential(credential)
                }else{
                    Toast.makeText(this, "please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "please Enter Correct OTP", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun updateUI(){
        startActivity(Intent(this@OTPActivity, MainActivity::class.java))
        finish()
    }

    private fun storeData() {
        val data = UserModel(phoneNumber = phoneNumber)
        Firebase.firestore.collection("Users").document(phoneNumber)
            .set(data).addOnSuccessListener {

            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "Number not saved on Firestore", Toast.LENGTH_SHORT).show()
            }
    }


    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                dialog.show()
                updateUI()
                storeData()
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
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
            }
        }

        override fun onCodeSent(verificationId: String,token: PhoneAuthProvider.ForceResendingToken) {

            OTP = verificationId
            resendToken = token
            Toast.makeText(this@OTPActivity, "OTP has been Resent to your number", Toast.LENGTH_SHORT).show()

        }
    }


}