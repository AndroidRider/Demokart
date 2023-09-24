package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Adapter.CartAdapter
import com.androidrider.demokart.Adapter.WatchListAdapter
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentMoreBinding
import com.androidrider.demokart.databinding.FragmentWatchListBinding
import com.androidrider.demokart.roomdb.MyDatabase


class WatchListFragment : Fragment() {

    lateinit var binding: FragmentWatchListBinding
    lateinit var list: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentWatchListBinding.inflate(layoutInflater)

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = MyDatabase.getInstance(requireContext()).watchListDao()

        list = ArrayList()

        dao.getAllWatchListItems().observe(requireActivity()) {
            binding.watchListRecyclerView.adapter = WatchListAdapter(requireContext(), it)
        }

        return binding.root
    }

}