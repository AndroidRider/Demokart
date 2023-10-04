//package com.androidrider.demokart.Fragment
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.androidrider.demokart.Adapter.WatchListAdapter
//import com.androidrider.demokart.Model.WatchListModel
//import com.androidrider.demokart.R
//import com.androidrider.demokart.databinding.FragmentWatchListBinding
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//
//
//class WatchListFragment : Fragment() {
//
//    lateinit var binding: FragmentWatchListBinding
//    lateinit var list: ArrayList<String>
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        // Inflate the layout for this fragment
//        binding = FragmentWatchListBinding.inflate(layoutInflater)
//
//        // Access the toolbar view - Show/Hide
//        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
//        toolbar.visibility = View.VISIBLE
//
//        /* correct Logic */
//        val preference =
//            requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
//        val editor = preference.edit()
//        editor.putBoolean("isCart", false)
//        editor.apply()
//
//        list = ArrayList()
//
//        getWatchListData()
//
//        return binding.root
//    }
//
//    private fun getWatchListData() {
//
//        val preferences =
//            requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
//
//        val currentUserNumber = preferences.getString("number", "")
//
//        if (!currentUserNumber.isNullOrEmpty()) {
//            val cartCollectionRef = Firebase.firestore.collection("Users")
//                .document(currentUserNumber).collection("WatchList")
//
//            cartCollectionRef.get()
//                .addOnSuccessListener { querySnapshot ->
//
//                    val cartItemList = mutableListOf<WatchListModel>()
//
//                    for (document in querySnapshot) {
//                        val productId = document.id
//                        val productName = document.getString("productName") ?: ""
//                        val productImage = document.getString("productImage") ?: ""
//                        val productSp = document.getString("productSp") ?: ""
//                        val productMrp = document.getString("productMrp") ?: ""
//                        // Retrieve other fields as needed
//
//                        val cartItem = WatchListModel(productId,productName,productSp,productMrp, productImage )
//                        cartItemList.add(cartItem)
//                    }
//
//                    binding.watchListRecyclerView.adapter = WatchListAdapter(requireContext(), cartItemList, Firebase.firestore, currentUserNumber)
//
//                    list.clear()
//                    for (data in cartItemList) {
//                        list.add(data.productId!!)
//                    }
//
//                    // Check if the cart is empty and show/hide the message accordingly
//                    val emptyCartMessage = binding.noDataTextView
//                    if (cartItemList.isEmpty()) {
//                        emptyCartMessage.visibility = View.VISIBLE
//                    } else {
//                        emptyCartMessage.visibility = View.GONE
//                    }
//
//                }
//                .addOnFailureListener {
//                    // Handle the failure to retrieve cart data
//                    Toast.makeText(requireContext(),"Failed to retrieve cart data",Toast.LENGTH_SHORT).show()
//                }
//        }
//
//
//    }
//}