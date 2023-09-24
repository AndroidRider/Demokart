package com.androidrider.demokartadmin.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidrider.demokartadmin.Adapter.AllOrderAdapter
import com.androidrider.demokartadmin.Model.AllOrderModel
import com.androidrider.demokartadmin.databinding.ActivityAllOrderBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllOrderActivity : AppCompatActivity() {

    lateinit var binding: ActivityAllOrderBinding

    private lateinit var list: ArrayList<AllOrderModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = ArrayList()

        Firebase.firestore.collection("AllOrders").get()
            .addOnSuccessListener {
                list.clear()
                for (doc in it) {
                    val data = doc.toObject(AllOrderModel::class.java)
                    list.add(data)
                }
                binding.allOrderRecyclerView.adapter = AllOrderAdapter(this, list)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }


    }
}