//package com.androidrider.demokart.Fragment
//
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.androidrider.demokart.Adapter.LikeAdapter
//import com.androidrider.demokart.Model.LikeModel
//import com.androidrider.demokart.R
//import com.androidrider.demokart.databinding.FragmentLikeBinding
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.firestore.ktx.toObject
//import com.google.firebase.ktx.Firebase
//
//class LikeFragment() : Fragment(R.layout.fragment_like){
//
//    private lateinit var binding: FragmentLikeBinding
//    private lateinit var adapter: LikeAdapter
//    private lateinit var likedProductList: ArrayList<LikeModel>
//
//
//    private var likeDBRef = Firebase.firestore.collection("LikedProducts")
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding = FragmentLikeBinding.bind(view)
//
//        likedProductList = ArrayList()
//
//        adapter = LikeAdapter(requireContext(), likedProductList, this)
//        binding.rvLikedProducts.adapter = adapter
//
//
//        displayLikedProducts()
//
//    }
//
//
//    private fun displayLikedProducts() {
//
//        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
//        val currentUserNumber = preferences.getString("number", "")
//
//        likeDBRef
//            .whereEqualTo("uid", currentUserNumber)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                for (item in querySnapshot) {
//                    val likedProduct = item.toObject<LikeModel>()
//                    likedProductList.add(likedProduct)
//                    adapter.notifyDataSetChanged()
//                }
//
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//
//    override fun onClickLike(item: LikeModel) {
//        //todo Remove from Liked Items
//
//        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
//        val currentUserNumber = preferences.getString("number", "")
//
//        likeDBRef
//            .whereEqualTo("uid", currentUserNumber)
//            .whereEqualTo("productId", item.productId)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//
//                for (item in querySnapshot) {
//                    likeDBRef.document(item.id).delete()
//                    likedProductList.remove(item.toObject<LikeModel>())
//                    adapter.notifyDataSetChanged()
//
//                    Toast.makeText(requireContext(), "Removed From the Liked Items", Toast.LENGTH_SHORT).show()
//                }
//
//            }
//            .addOnFailureListener {
//
//                Toast.makeText(requireContext(), "Failed To Remove From Liked Items", Toast.LENGTH_SHORT).show()
//            }
//
//    }
//
//}