package com.androidrider.demokart.Fragment

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidrider.demokart.Activity.EditProfileActivity
import com.androidrider.demokart.Activity.LoginActivity
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentAccountBinding
import com.androidrider.demokart.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        binding.cardView.setOnClickListener {
            navigateProfile() // Navigate Profile Fragment
        }
        binding.myProfile.setOnClickListener {
            navigateProfile() // Navigate Profile Fragment
        }
        binding.myCart.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_cartFragment) // Navigate Cart Fragment
        }
        binding.myOrder.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_myOrderFragment) // Navigate My Order Fragment
        }

        // Calling Methods
        loadUserInfo()
        logout()

        return binding.root
    }

    private fun navigateProfile(){
        findNavController().navigate(R.id.action_accountFragment_to_profileFragment)
    }

    private fun loadUserInfo() {
        val preferences = requireContext().getSharedPreferences("user", MODE_PRIVATE)

        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "")!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->

                val userData = documentSnapshot.data


                val profileImageUrl = userData?.get("profileImage") as? String
                val phone = userData?.get("phoneNumber") as? String

                binding.textViewName.text = userData?.get("name") as? String
                binding.textViewNumber.text = if (phone!!.isNotEmpty()) "+91 $phone" else "Phone"

                // using the 'let' extension function to conditionally load an image into an ImageView using Glide.
                // If the profileImageUrl is not null, Glide is used to load the image; otherwise, a default drawable is set.
                profileImageUrl?.let {
                    Glide.with(requireContext()).load(it).into(binding.profileImage)
                } ?: run {
                    binding.profileImage.setImageResource(R.drawable.profile_preview)
                }


            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logout() {
        binding.logoutButton.setOnClickListener {
            sharedPreferences = requireContext().getSharedPreferences("loginPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }
}
