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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.androidrider.demokart.Activity.EditProfileActivity
import com.androidrider.demokart.Activity.LoginActivity
import com.androidrider.demokart.Adapter.StatusAdapter
import com.androidrider.demokart.Model.StatusModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val list: ArrayList<StatusModel> = ArrayList()

    private var coverImageUrl: Uri? = null
    private val launchCoverGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coverImageUrl = result.data?.data
            binding.coverImage.setImageURI(coverImageUrl)
            uploadImage(coverImageUrl, "coverImage")
        }
    }

    private var profileImageUrl: Uri? = null
    private val launchProfileGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            profileImageUrl = result.data?.data
            binding.profileImage.setImageURI(profileImageUrl)
            uploadImage(profileImageUrl, "profileImage")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        // Set up click listeners
        binding.coverImage.setOnClickListener { launchGallery(launchCoverGalleryActivity) }
        binding.profileImage.setOnClickListener { launchGallery(launchProfileGalleryActivity) }

        loadUserInfo()
        logout()
        getOrderDetails()

        binding.EditProfileButton.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }

        return binding.root
    }

    private fun launchGallery(callback: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        callback.launch(intent)
    }


    private fun uploadImage(imageUri: Uri?, field: String) {
        imageUri?.let {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val refStorage = FirebaseStorage.getInstance().reference.child("Users/$fileName")
            refStorage.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { image ->
                        storeImageData(field, image.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Something went wrong with storage!", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun storeImageData(field: String, imageUrl: String) {
        val preferences = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        val map = hashMapOf<String, Any>()
        map[field] = imageUrl

        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "defaultValue")!!)
            .update(map)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Image Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserInfo() {
        val preferences = requireContext().getSharedPreferences("user", MODE_PRIVATE)

        Firebase.firestore.collection("Users")
            .document(preferences.getString("number", "")!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val userData = documentSnapshot.data
                binding.apply {
                    textViewName.text = userData?.get("name") as? String
                    textViewCity.text = userData?.get("city") as? String

                    textViewNumber.text = userData?.get("phoneNumber") as? String
                    textViewEmail.text = userData?.get("email") as? String
                    textViewAddress.text = userData?.get("address") as? String
                    textViewCity2.text = userData?.get("city") as? String
                    textViewState.text = userData?.get("state") as? String
                    textViewPinCode.text = userData?.get("pinCode") as? String

                }

                val coverImageUrl = userData?.get("coverImage") as? String
                val profileImageUrl = userData?.get("profileImage") as? String

                // Load and display images using Glide
                coverImageUrl?.let {
                    Glide.with(requireContext()).load(it) .into(binding.coverImage)
                }

                profileImageUrl?.let {
                    Glide.with(requireContext()).load(it).into(binding.profileImage)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getOrderDetails() {
        val preferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)

        Firebase.firestore.collection("AllOrders")
            .whereEqualTo("userId", preferences.getString("number", "")!!)
            .get()
            .addOnSuccessListener { querySnapshot ->
                list.clear()
                for (doc in querySnapshot) {
                    val data = doc.toObject(StatusModel::class.java)
                    list.add(data)
                }
                binding.recyclerView.adapter = StatusAdapter(requireContext(), list)
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
