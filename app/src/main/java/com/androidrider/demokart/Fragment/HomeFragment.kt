package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.androidrider.demokart.Adapter.BrandAdapter
import com.androidrider.demokart.Adapter.ProductAdapter
import com.androidrider.demokart.Adapter.CategoryAdapter
import com.androidrider.demokart.Adapter.FeaturedProductAdapter
import com.androidrider.demokart.Adapter.ShopByCategoryAdapter
import com.androidrider.demokart.Model.BrandModel
import com.androidrider.demokart.Model.CategoryModel
import com.androidrider.demokart.Model.ProductModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentHomeBinding
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(){

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.VISIBLE

        val preference =requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        if (preference.getBoolean("isCart", false))
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)

        binding.tvSeeAllBrand.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allBrandViewFragment)
        }
        binding.tvSeeAllProduct.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_seeAllProductFragment)
        }
        binding.tvSeeAllFeaturedProduct.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_seeAllProductFragment)
        }
        binding.tvSeeAllProduct2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_seeAllProductFragment)
        }
        binding.tvSeeAllCategory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allCategoryViewFragment)
        }



        getSliderImage()
        getCategories()
        getShopByCategory()
        getBrands()
        getProducts()
        getFeaturedProducts()
        getMoreProducts()

        binding.progressBar.visibility = View.VISIBLE

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
                    binding.progressBar.visibility = GONE
                }
                binding.CategoryRecyclerView.adapter = CategoryAdapter(requireContext(), list)

            }
    }

    private fun getShopByCategory() {

        val list = ArrayList<CategoryModel>()

        Firebase.firestore.collection("Category").limit(6)
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                    binding.progressBar.visibility = GONE
                }
                binding.categoryRecyclerView2.adapter = ShopByCategoryAdapter(requireContext(), list)

            }
    }

    private fun getBrands() {
        val list = ArrayList<BrandModel>()
        Firebase.firestore.collection("Brand")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(BrandModel::class.java)
                    list.add(data!!)
                    binding.progressBar.visibility = GONE
                }
                binding.brandRecyclerView.adapter = BrandAdapter(requireContext(), list)
            }
    }



    private fun getProducts() {

        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")

        val list = ArrayList<ProductModel>()

        Firebase.firestore.collection("Products").limit(10)
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(ProductModel::class.java)
                    list.add(data!!)

                    binding.progressBar.visibility = GONE
                }
                binding.productRecyclerView.adapter = ProductAdapter(requireContext(), list, Firebase.firestore, currentUserNumber!!)
            }
    }

    private fun getMoreProducts() {

        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")

        val list = ArrayList<ProductModel>()

        Firebase.firestore.collection("Products")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(ProductModel::class.java)
                    list.add(data!!)

                    binding.progressBar.visibility = GONE
                }
                binding.productRecyclerView2.adapter = ProductAdapter(requireContext(), list, Firebase.firestore, currentUserNumber!!)
            }
    }

    private fun getFeaturedProducts() {

        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")

        val list = ArrayList<ProductModel>()

        Firebase.firestore.collection("Products")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(ProductModel::class.java)
                    list.add(data!!)

                    binding.progressBar.visibility = GONE
                }
                binding.featuredRecyclerView.adapter = FeaturedProductAdapter(requireContext(), list, Firebase.firestore, currentUserNumber!!)
            }
    }


}