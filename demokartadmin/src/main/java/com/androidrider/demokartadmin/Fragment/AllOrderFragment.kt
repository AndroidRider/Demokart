package com.androidrider.demokartadmin.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidrider.demokartadmin.Adapter.AllOrderAdapter
import com.androidrider.demokartadmin.Model.AllOrderModel
import com.androidrider.demokartadmin.databinding.FragmentAllOrderBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AllOrderFragment : Fragment() {

    lateinit var binding: FragmentAllOrderBinding

    private lateinit var list: ArrayList<AllOrderModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllOrderBinding.inflate(layoutInflater)

        list = ArrayList()



        Firebase.firestore.collection("AllOrders").get()
            .addOnSuccessListener {
                list.clear()
                for (doc in it) {
                    val data = doc.toObject(AllOrderModel::class.java)
                    list.add(data)
                }
                binding.allOrderRecyclerView.adapter = AllOrderAdapter(requireContext(), list)


            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }


        return  binding.root
    }


}