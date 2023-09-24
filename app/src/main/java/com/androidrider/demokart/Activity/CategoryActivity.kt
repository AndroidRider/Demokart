package com.androidrider.demokart.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidrider.demokart.Adapter.ProductCategoryAdapter
import com.androidrider.demokart.Model.AddProductModel
import com.androidrider.demokart.databinding.ActivityCategoryBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getProduct(intent.getStringExtra("cat"))
    }

    private fun getProduct(category: String?) {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("Products").whereEqualTo("productCategory", category)
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)
                }
                binding.recyclerView.adapter = ProductCategoryAdapter(this, list)
            }
    }
}