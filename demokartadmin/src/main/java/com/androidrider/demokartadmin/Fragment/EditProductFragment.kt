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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.androidrider.demokartadmin.Adapter.AddProductImageAdapter
import com.androidrider.demokartadmin.Adapter.EditProductImageAdapter
import com.androidrider.demokartadmin.Model.CategoryModel
import com.androidrider.demokartadmin.Model.EditProductImageModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentEditProductBinding
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class EditProductFragment : Fragment() {

    lateinit var binding: FragmentEditProductBinding

    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages: ArrayList<String>
    private lateinit var adapter: AddProductImageAdapter
    private lateinit var categoryList: ArrayList<String>
    private lateinit var dialog: Dialog

    private var i = 0
    private var coverImage: Uri? = null
    private var coverImgUrl: String? = ""


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

            binding.tvNewImages.visibility = View.VISIBLE

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = FragmentEditProductBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        list = ArrayList()
        listImages = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectCoverImageButton.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }
        binding.selectProductImagesButton.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)

        }

        //For Local Image
        adapter = AddProductImageAdapter(list)
        binding.recyclerView.adapter = adapter


        binding.productSubmitBtn.setOnClickListener {
            validateData()

        }

        setProductCategory()
        loadProductInfo()


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
            updateImages()
        }
    }

    private fun updateImages() {
        dialog.show()
        val newImagesList = ArrayList<String>()

        // Delete existing images first
        for (imageUrl in listImages) {
            val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            oldImageRef.delete()
                .addOnSuccessListener {
                    // Existing image deleted successfully
                }
                .addOnFailureListener { exception ->
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Failed to delete existing images: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Upload new images
        uploadImages(newImagesList!!)
    }

    private fun uploadImages(newImagesList: ArrayList<String>) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Products/$fileName")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener { coverImageUploadTask ->
                coverImageUploadTask.storage.downloadUrl.addOnSuccessListener { coverImage ->
                    coverImgUrl = coverImage.toString()
                    newImagesList.add(coverImgUrl!!)

                    // Check if all new images are uploaded
                    if (newImagesList.size == list.size) {
                        listImages = newImagesList

                        // getting productId from previous fragment
                        val productId = arguments?.getString("productId")
                        updateData(productId!!)
                    } else {
                        i++
                        uploadProductImage(newImagesList)
                    }
                }
            }
            .addOnFailureListener { exception ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProductImage(newImagesList: ArrayList<String>) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Products/$fileName")
        refStorage.putFile(list[i]!!)
            .addOnSuccessListener { productImageUploadTask ->
                productImageUploadTask.storage.downloadUrl.addOnSuccessListener { productImage ->
                    newImagesList.add(productImage.toString())

                    // Check if all new images are uploaded
                    if (newImagesList.size == list.size) {
                        listImages = newImagesList

                        // getting productId from previous fragment
                        val productId = arguments?.getString("productId")
                        updateData(productId!!)
                    } else {
                        i++
                        uploadProductImage(newImagesList)
                    }
                }
            }
            .addOnFailureListener { exception ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateData(productId: String) {
        val db = Firebase.firestore.collection("Products")
        val dataToUpdate = mapOf(
            "productName" to binding.productNameEdt.text.toString(),
            "productMrp" to binding.productMrpEdt.text.toString(),
            "productSp" to binding.productSpEdt.text.toString(),
            "productDescription" to binding.productDescriptionEdt.text.toString(),
            "productFeatures" to binding.productFeaturesEdt.text.toString(),
            "productCoverImage" to coverImgUrl.toString(),
            "productCategory" to categoryList[binding.productCategoryDropdown.selectedItemPosition],
            "productImages" to listImages
        )

        db.document(productId).update(dataToUpdate)
            .addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Product Updated Successfully!", Toast.LENGTH_SHORT).show()

                // Clear input fields
                binding.productNameEdt.text = null
                binding.productMrpEdt.text = null
                binding.productSpEdt.text = null
                binding.productDescriptionEdt.text = null
                binding.productFeaturesEdt.text = null
                binding.productCoverImg.visibility = View.GONE

                // Clear the RecyclerView items (if needed)
                list.clear()
                adapter.notifyDataSetChanged()


            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProductInfo() {

        // getting productId from previous fragment
        val productId = arguments?.getString("productId")

        if (productId != null) {
            Firebase.firestore.collection("Products")
                .document(productId)
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    if (documentSnapshot.exists()) {
                        val productData = documentSnapshot.data

                        binding.productNameEdt.setText(productData?.get("productName") as? String)
                        binding.productMrpEdt.setText(productData?.get("productMrp") as? String)
                        binding.productSpEdt.setText(productData?.get("productSp") as? String)
                        binding.productDescriptionEdt.setText(productData?.get("productDescription") as? String)
                        binding.productFeaturesEdt.setText(productData?.get("productFeatures") as? String)

                        // Load the cover image using Glide
                        val coverImageUrl = productData?.get("productCoverImage") as? String
                        if (coverImageUrl != null) {
                            Glide.with(requireContext()).load(coverImageUrl).into(binding.productCoverImg)
                            binding.productCoverImg.visibility = View.VISIBLE
                        }

                        // For the dropdown fetch category
                        val productCategory = productData?.get("productCategory") as? String
                        val adapter = binding.productCategoryDropdown.adapter as? ArrayAdapter<String>
                        if (adapter != null) {
                            val position = adapter.getPosition(productCategory)
                            if (position != -1) {
                                binding.productCategoryDropdown.setSelection(position)
                            }
                        }


                        // For the fetch image list in recyclerview
                        val imageUrls = productData?.get("productImages") as? ArrayList<String>
                        val editProductImages = ArrayList(imageUrls?.map { imageUrl ->
                            EditProductImageModel(imageUrl) } ?: emptyList())

                        binding.oldRecyclerView.adapter = EditProductImageAdapter(requireContext(), editProductImages)



                    } else {
                        Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(),"Something went wrong: ${e.message}",Toast.LENGTH_SHORT).show()
                }
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
            val arrayAdapter =ArrayAdapter(requireContext(), R.layout.dropdown_item_layout, categoryList)
            binding.productCategoryDropdown.adapter = arrayAdapter

        }
    }


}