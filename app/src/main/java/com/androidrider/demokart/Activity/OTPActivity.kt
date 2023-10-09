package com.androidrider.demokart.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityOtpactivityBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OTPActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpactivityBinding
    private lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)


        binding.btnVerifyOTP.setOnClickListener {
            if (binding.edtOTP.text!!.isEmpty()) {
                Toast.makeText(this, "please provide OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyUser(binding.edtOTP.text.toString())
            }
        }
    }

    private fun verifyUser(otp: String) {


        dialog.show()

        val credential =
            PhoneAuthProvider.getCredential(intent.getStringExtra("verificationId")!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {


                    val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
                    val editor = preferences.edit()

                    editor.putString("number", intent.getStringExtra("number")!!)
                    editor.apply()

                    dialog.dismiss()
                    startActivity(Intent(this@OTPActivity, MainActivity::class.java))
                    finish()

                } else {

                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                // Update UI
            }
    }
}