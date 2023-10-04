package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Adapter.MyOrderAdapter
import com.androidrider.demokart.Model.MyOrderModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentMyOrderBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyOrderFragment : Fragment() {
    private lateinit var binding: FragmentMyOrderBinding

    private val list: ArrayList<MyOrderModel> = ArrayList()//
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyOrderBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        getOrderDetails()

        return binding.root
    }


    private fun getOrderDetails() {
        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)

        val currentUserNumber = preferences.getString("number", "")

        Firebase.firestore.collection("AllOrders")
            .whereEqualTo("userId", preferences.getString("number", "")!!)
            .get()
            .addOnSuccessListener { querySnapshot ->
                list.clear()
                for (doc in querySnapshot) {
                    val data = doc.toObject(MyOrderModel::class.java)
                    list.add(data)
                }
                binding.recyclerView.adapter = MyOrderAdapter(requireContext(), list, Firebase.firestore, currentUserNumber!!)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }



}