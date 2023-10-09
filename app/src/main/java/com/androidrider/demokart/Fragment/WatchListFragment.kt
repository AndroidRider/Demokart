package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Adapter.ProductAdapter
import com.androidrider.demokart.Adapter.WatchListAdapter
import com.androidrider.demokart.Model.ProductModel
import com.androidrider.demokart.Model.WatchListModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentWatchListBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WatchListFragment : Fragment() {

    lateinit var binding: FragmentWatchListBinding
    lateinit var list: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentWatchListBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
//

        list = ArrayList()

        getProducts()

        return binding.root
    }


    private fun getProducts() {

        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")

        val list = ArrayList<WatchListModel>()

        val watchlistRef = Firebase.firestore.collection("Users")
            .document(currentUserNumber!!)
            .collection("WatchList")

        watchlistRef
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(WatchListModel::class.java)
                    list.add(data!!)

                    binding.progressBar.visibility = View.GONE
                }
                binding.watchListRecyclerView.adapter = WatchListAdapter(requireContext(), list, Firebase.firestore, currentUserNumber!!)

                // Check if the cart is empty and show/hide the message accordingly
                if (list.isEmpty()) {
                    binding.noDataTextView.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.noDataTextView.visibility = View.GONE

                }

            }
    }



}