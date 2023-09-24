package com.androidrider.demokart.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Activity.AddressActivity
import com.androidrider.demokart.Activity.CategoryActivity
import com.androidrider.demokart.Adapter.CartAdapter
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentCartBinding
import com.androidrider.demokart.databinding.FragmentHomeBinding
import com.androidrider.demokart.roomdb.MyDatabase
import com.androidrider.demokart.roomdb.ProductModel


class CartFragment : Fragment() {

    lateinit var binding: FragmentCartBinding
    lateinit var list: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = MyDatabase.getInstance(requireContext()).productDao()

        list = ArrayList()

        dao.getAllProduct().observe(requireActivity()){
            binding.cartRecycler.adapter = CartAdapter(requireContext(), it)

            list.clear()
            for (data in it){
                list.add(data.productId)
            }
            totalCost(it)
        }

        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {
        var total = 0
        for (item in data!!){
//            total+= item.productSp!!.toInt() // when use comma in price then its not work
            total += item.productSp!!.replace(",", "").toInt()
        }
        binding.textView1.text = "${data.size}"
        binding.textView2.text = "₹$total"

        binding.checkout.setOnClickListener {
            val intent = Intent(context, AddressActivity::class.java)

            val b = Bundle()
            b.putStringArrayList("productIds", list)
            b.putString("totalCost", total.toString())
            intent.putExtras(b)
            startActivity(intent)
        }
    }


}