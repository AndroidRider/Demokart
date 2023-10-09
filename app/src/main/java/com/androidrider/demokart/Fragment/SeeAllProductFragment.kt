package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Adapter.ProductAdapter
import com.androidrider.demokart.Model.ProductModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentHomeBinding
import com.androidrider.demokart.databinding.FragmentSeeAllProductBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SeeAllProductFragment : Fragment() {

    lateinit var binding: FragmentSeeAllProductBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSeeAllProductBinding.inflate(layoutInflater)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.VISIBLE


        getProducts()

        return binding.root
    }



    private fun getProducts() {

        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)

        val currentUserNumber = preferences.getString("number", "")

        val list = ArrayList<ProductModel>()

        Firebase.firestore.collection("Products")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(ProductModel::class.java)
                    list.add(data!!)
                }


                binding.productRecyclerView.adapter = ProductAdapter(requireContext(), list, Firebase.firestore, currentUserNumber!!)

            }
    }

}