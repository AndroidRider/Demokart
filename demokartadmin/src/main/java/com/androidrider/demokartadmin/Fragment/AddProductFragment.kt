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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.androidrider.demokartadmin.Adapter.AddProductImageAdapter
import com.androidrider.demokartadmin.Model.ProductModel
import com.androidrider.demokartadmin.Model.CategoryModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentAddProductBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddProductFragment : Fragment() {

    lateinit var binding: FragmentAddProductBinding

    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages: ArrayList<String>
    private lateinit var adapter: AddProductImageAdapter
    private var coverImage: Uri? = null
    private lateinit var dialog: Dialog
    private var coverImgUrl: String? = ""
    private lateinit var categoryList: ArrayList<String>

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        if (it.resultCode == Activity.RESULT_OK) {
            coverImage = it.data!!.data
            binding.productCoverImg.setImageURI(coverImage)
            binding.productCoverImg.visibility = View.VISIBLE
        }
    }
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
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = GONE

        list = ArrayList()
        listImages = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)


        binding.selectCoverImg.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }


        binding.productImgBtn.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)
        }

        setProductCategory()

        adapter = AddProductImageAdapter(list)
        binding.productRecyclerView.adapter = adapter

        binding.productSubmitBtn.setOnClickListener {
            validateData()
        }

        return binding.root
    }

    private fun validateData() {
        if (binding.productNameEdt.text.toString().isEmpty()) {
            binding.productNameEdt.requestFocus()
            binding.productNameEdt.error = "Empty"

        } else if (binding.productMrpEdt.text.toString().isEmpty()) {
            binding.productMrpEdt.requestFocus()
            binding.productMrpEdt.error = "Empty"

        } else if (binding.productSpEdt.text.toString().isEmpty()) {
            binding.productSpEdt.requestFocus()
            binding.productSpEdt.error = "Empty"
        }else if (binding.productDescriptionEdt.text.toString().isEmpty()) {
            binding.productDescriptionEdt.requestFocus()
            binding.productDescriptionEdt.error = "Empty"

        }else if (binding.productFeaturesEdt.text.toString().isEmpty()) {
            binding.productFeaturesEdt.requestFocus()
            binding.productFeaturesEdt.error = "Empty"

        }else if (coverImage == null) {
            Toast.makeText(requireContext(), "Please select cover image", Toast.LENGTH_SHORT).show()

        } else if (list.size < 1) {
            Toast.makeText(requireContext(), "Please select product image", Toast.LENGTH_SHORT)
                .show()

        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Products/$fileName")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    coverImgUrl = image.toString()
                    uploadProductImage()
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Something went wrong with storage!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    var i = 0
    private fun uploadProductImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Products/$fileName")
        refStorage.putFile(list[i]!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    listImages.add(image.toString())
                    if (list.size == listImages.size) {
                        storedata()
                    } else {
                        i++
                        uploadProductImage()
                    }
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Something went wrong with storage!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun storedata() {
        val db = Firebase.firestore.collection("Products")
        val key = db.document().id
        val data = ProductModel(
            key,
            binding.productNameEdt.text.toString(),
            binding.productMrpEdt.text.toString(),
            binding.productSpEdt.text.toString(),
            binding.productDescriptionEdt.text.toString(),
            binding.productFeaturesEdt.text.toString(),
            coverImgUrl.toString(),
            categoryList[binding.productCategoryDropdown.selectedItemPosition],
            listImages
        )

        db.document(key).set(data)
            .addOnSuccessListener {

                dialog.dismiss()
                Toast.makeText(requireContext(), "product Added Successfully!", Toast.LENGTH_SHORT).show()

                binding.productNameEdt.text = null
                binding.productMrpEdt.text = null
                binding.productSpEdt.text = null
                binding.productDescriptionEdt.text = null
                binding.productFeaturesEdt.text = null
                binding.productCoverImg.visibility = View.GONE
                list.clear()
                adapter.notifyDataSetChanged() // Clear the RecyclerView items
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setProductCategory() {
        categoryList = ArrayList()
        Firebase.firestore.collection("Category").get().addOnSuccessListener {
            categoryList.clear()
            for (doc in it.documents) {
                val data = doc.toObject(CategoryModel::class.java)
                categoryList.add(data!!.category!!)
            }
            categoryList.add(0, "Select Category")
            val arrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item_layout, categoryList)
            binding.productCategoryDropdown.adapter = arrayAdapter
        }
    }

}