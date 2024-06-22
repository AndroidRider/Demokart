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
import com.androidrider.demokart.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences

    // Open Gallery For Cover Image
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

    // Open Gallery For Profile Image
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

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        // Set up click listeners
        binding.coverImage.setOnClickListener { launchGallery(launchCoverGalleryActivity) }
        binding.profileImage.setOnClickListener { launchGallery(launchProfileGalleryActivity) }




        // Edit Profile ClickListener
        binding.EditProfileButton.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }

        // Calling Methods
        loadUserInfo()


        return binding.root
    }

    // Open Gallery For Image
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
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong with storage!",
                        Toast.LENGTH_SHORT
                    )
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

                val coverImageUrl = userData?.get("coverImage") as? String
                val profileImageUrl = userData?.get("profileImage") as? String

                binding.apply {

                    val phone = userData?.get("phoneNumber") as? String
                    val email = userData?.get("email") as? String
                    val address = userData?.get("address") as? String
                    val pinCode = userData?.get("pinCode") as? String

                    // Validating Data
                    textViewEmail.text = if (email!!.isNotEmpty()) email else "Email"
                    textViewNumber.text = if (phone!!.isNotEmpty()) phone else "Phone"
                    textViewAddress.text = if (address!!.isNotEmpty()) address else "Address"
                    textViewPinCode.text = if (pinCode!!.isNotEmpty()) "- $pinCode" else ""


                    // Non-Validating Data
                    textViewName.text = userData?.get("name") as? String
                    textViewCityTop.text = userData?.get("city") as? String
                    textViewCityBottom.text = userData?.get("city") as? String
                    textViewState.text = userData?.get("state") as? String


                }

                // Load and display images using Glide
                coverImageUrl?.let {
                    Glide.with(requireContext()).load(it).into(binding.coverImage)
                }

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


}
