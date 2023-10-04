package com.androidrider.demokartadmin.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.androidrider.demokartadmin.Adapter.AddProductImageAdapter
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentSliderBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class SliderFragment : Fragment() {

    lateinit var binding: FragmentSliderBinding

    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages: ArrayList<String>
    private lateinit var adapter: AddProductImageAdapter

    lateinit var dialog : Dialog
    private var imageUrl : Uri? = null
    private var launchProductActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSliderBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        list = ArrayList()
        listImages = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.apply {
            imageView.setOnClickListener {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchProductActivity.launch(intent)
            }

            adapter = AddProductImageAdapter(list)
            binding.productRecyclerView.adapter = adapter

            UploadBtn.setOnClickListener {
                validateData()
            }
        }

        return binding.root
    }

    private fun validateData(){
        if (list.size < 1) {
            Toast.makeText(requireContext(), "Please select product image", Toast.LENGTH_SHORT)
                .show()
        } else {
            uploadProductImage()
        }
    }

    var i = 0
    private fun uploadProductImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Slider/$fileName")
        refStorage.putFile(list[i]!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    listImages.add(image.toString())
                    if (list.size == listImages.size) {
                        storeData()
                    } else {
                        i++
                        uploadProductImage()
                    }

                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(),"Something went wrong with storage!",Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData() {

        val db = Firebase.firestore

        val data = hashMapOf<String, Any>("listImages" to listImages)

        db.collection("Slider").document("item").set(data)
            .addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Slider Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }


}