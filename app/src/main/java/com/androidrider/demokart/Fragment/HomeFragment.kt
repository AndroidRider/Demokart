package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.androidrider.demokart.Adapter.AddProductAdapter
import com.androidrider.demokart.Adapter.CategoryAdapter
import com.androidrider.demokart.Model.AddProductModel
import com.androidrider.demokart.Model.CategoryModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentHomeBinding
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val preference =requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        if (preference.getBoolean("isCart", false))
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)

        getSliderImage()
        getCategories()
        getProducts()

        return binding.root
    }

    private fun getSliderImage() {
        Firebase.firestore.collection("Slider").document("item")
            .get().addOnSuccessListener {
                val list = it.get("listImages") as ArrayList<String>
                val slideList = ArrayList<SlideModel>()
                for (data in list) {
                    slideList.add(SlideModel(data))
                }
                binding.imageSlider.setImageList(slideList)

            }
    }

    private fun getCategories() {
        val list = ArrayList<CategoryModel>()
        Firebase.firestore.collection("Category")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                binding.CategoryRecyclerView.adapter = CategoryAdapter(requireContext(), list)
            }
    }

    private fun getProducts() {

        val list = ArrayList<AddProductModel>()

        Firebase.firestore.collection("Products")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)
                }
                binding.productRecyclerView.adapter = AddProductAdapter(requireContext(), list)
            }
    }

}