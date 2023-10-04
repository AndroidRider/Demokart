package com.androidrider.demokart.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidrider.demokart.Adapter.CategoryProductAdapter
import com.androidrider.demokart.Model.ProductModel
import com.androidrider.demokart.databinding.ActivityCategoryProductBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryProductActivity : AppCompatActivity() {

    lateinit var binding: ActivityCategoryProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getProduct(intent.getStringExtra("cat"))
    }

    private fun getProduct(category: String?) {

        val preferences = this.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")

        val list = ArrayList<ProductModel>()
        Firebase.firestore.collection("Products").whereEqualTo("productCategory", category)
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(ProductModel::class.java)
                    list.add(data!!)

                }
                binding.recyclerView.adapter = CategoryProductAdapter(this, list, Firebase.firestore, currentUserNumber!!)
            }
    }




}