package com.androidrider.demokart.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidrider.demokart.Adapter.AllBrandViewAdapter
import com.androidrider.demokart.Model.BrandModel
import com.androidrider.demokart.databinding.FragmentAllBrandViewBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AllBrandViewFragment : Fragment() {

    lateinit var binding: FragmentAllBrandViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllBrandViewBinding.inflate(layoutInflater)



        getBrands()

        return binding.root
    }

    private fun getBrands() {
        val list = ArrayList<BrandModel>()
        Firebase.firestore.collection("Brand")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(BrandModel::class.java)
                    list.add(data!!)
                }
                binding.brandRecyclerView.adapter = AllBrandViewAdapter(requireContext(), list)
            }
    }

}