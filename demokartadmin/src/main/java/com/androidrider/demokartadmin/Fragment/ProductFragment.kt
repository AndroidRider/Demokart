package com.androidrider.demokartadmin.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.androidrider.demokartadmin.Adapter.ProductAdapter
import com.androidrider.demokartadmin.Model.ProductModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentProductBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class ProductFragment : Fragment() {

    lateinit var binding: FragmentProductBinding

    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.VISIBLE

        // Obtain a reference to the NavController
        navController = findNavController()

        binding.progress.visibility = View.VISIBLE

        binding.floatingActionButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productFragment_to_addProductFragment)
        }

        getProducts()


        return binding.root
    }

    private fun getProducts() {

        val list = ArrayList<ProductModel>()

        Firebase.firestore.collection("Products")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(ProductModel::class.java)
                    list.add(data!!)

                    binding.progress.visibility = GONE
                }

                binding.recyclerView.adapter = ProductAdapter(requireContext(), list, navController)





            }
    }



}