package com.androidrider.demokartadmin.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.androidrider.demokartadmin.Adapter.CategoryAdapter
import com.androidrider.demokartadmin.Model.CategoryModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentCategoryBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class CategoryFragment : Fragment() {

    lateinit var binding: FragmentCategoryBinding

    lateinit var dialog : Dialog
    private var imageUrl : Uri? = null
    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            imageUrl = it.data!!.data
            binding.imageBox.setImageURI(imageUrl)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = GONE

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.apply {

            imageBox.setOnClickListener {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)
            }

            uploadCategoryButton.setOnClickListener {
                validateData(binding.edtCategoryName.text.toString())
            }
        }

        getDataInRecycleView()

        return binding.root
    }

    private fun getDataInRecycleView() {

        val list = ArrayList<CategoryModel>()

        Firebase.firestore.collection("Category")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                binding.recyclerView.adapter = CategoryAdapter(requireContext(), list)
            }
    }


    private fun validateData(categoryName: String) {

        if (categoryName.isEmpty()){
            Toast.makeText(requireContext(), "please provide category name", Toast.LENGTH_SHORT).show()
        }else if(imageUrl == null){
            Toast.makeText(requireContext(), "please select image", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage(categoryName!!)
        }
    }

    private fun uploadImage(categoryName: String) {
        dialog.show()
        val fileName = UUID.randomUUID().toString()+".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Category/$fileName")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->

                    storeData(categoryName, image.toString())
                }
            }

            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(categoryName: String, image: String) {

        val db = Firebase.firestore

        val data = hashMapOf<String, Any>(
            "category" to categoryName,
            "img" to image
        )

        db.collection("Category").add(data)
            .addOnSuccessListener {
                dialog.dismiss()

                getDataInRecycleView()

                binding.imageBox.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.preview, null))
                binding.edtCategoryName.text = null
                Toast.makeText(requireContext(), "Category Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }


}