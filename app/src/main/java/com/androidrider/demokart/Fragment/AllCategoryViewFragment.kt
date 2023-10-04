package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidrider.demokart.Adapter.AllCategoryViewAdapter
import com.androidrider.demokart.Adapter.CategoryAdapter
import com.androidrider.demokart.Model.CategoryModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentAllCategoryViewBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AllCategoryViewFragment : Fragment() {

    lateinit var binding: FragmentAllCategoryViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllCategoryViewBinding.inflate(layoutInflater)

        getCategories()


        return binding.root
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
                binding.categoryRecyclerView.adapter = AllCategoryViewAdapter(requireContext(), list)
            }
    }


}