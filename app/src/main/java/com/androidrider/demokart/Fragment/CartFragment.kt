
package com.androidrider.demokart.Fragment

import CartModel
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Activity.AddressActivity
import com.androidrider.demokart.Adapter.CartAdapter
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentCartBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CartFragment : Fragment() {

    lateinit var binding: FragmentCartBinding

    lateinit var list : ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.VISIBLE

        list = ArrayList()

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        getCartData()


        return binding.root
    }

    private fun getCartData() {

       val preferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)

        val currentUserNumber = preferences.getString("number", "")

        if (!currentUserNumber.isNullOrEmpty()) {
            val cartCollectionRef = Firebase.firestore.collection("Users")
                .document(currentUserNumber).collection("Cart")

            cartCollectionRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val cartItemList = mutableListOf<CartModel>()

                    for (document in querySnapshot) {
                        val productId = document.id
                        val productName = document.getString("productName") ?: ""
                        val productSp = document.getString("productSp") ?: ""
                        val productMrp = document.getString("productMrp") ?: ""
                        val productImage = document.getString("productImage") ?: ""
                        // Retrieve other fields as needed

                        val cartItem = CartModel(productId, productName, productSp, productMrp, productImage)
                        cartItemList.add(cartItem)
                    }
                    binding.cartRecycler.adapter = CartAdapter(requireContext(), cartItemList, Firebase.firestore, currentUserNumber)

                    list.clear()
                    for (data in cartItemList) {
                        list.add(data.productId!!)
                    }
                    // Check if the cart is empty and show/hide the message accordingly
                    val emptyCartMessage = binding.emptyCartMessage
                    val detailCard = binding.cardView
                    if (cartItemList.isEmpty()) {
                        detailCard.visibility = View.GONE
                        emptyCartMessage.visibility = View.VISIBLE
                    } else {
                        emptyCartMessage.visibility = View.GONE
                        detailCard.visibility = View.VISIBLE
                    }
                    // Update cart summary
                    updateCartSummary(cartItemList)
                }
                .addOnFailureListener {
                    // Handle the failure to retrieve cart data
                    Toast.makeText(requireContext(), "Failed to retrieve cart data", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun updateCartSummary(cartItemList: List<CartModel>) {
        var totalCost = 0
        for (cartItem in cartItemList) {
            val formattedPrice = cartItem.productSp!!.replace(",", "").toIntOrNull()
            if (formattedPrice != null) {
                totalCost += formattedPrice
            }
        }
        binding.textView1.text = cartItemList.size.toString()
        binding.tvTotalCost.text = "₹$totalCost"

        binding.checkoutButton.setOnClickListener {
            val intent = Intent(context, AddressActivity::class.java)
            val b = Bundle()
            b.putStringArrayList("productIds", list)
            b.putString("totalCost", totalCost.toString())
            intent.putExtras(b)
            startActivity(intent)
        }
    }


}