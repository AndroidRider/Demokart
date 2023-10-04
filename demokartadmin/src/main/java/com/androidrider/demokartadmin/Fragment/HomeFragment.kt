package com.androidrider.demokartadmin.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentHomeBinding
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        binding.button1.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
        }

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productFragment)
        }

        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sliderFragment)
        }

        binding.button4.setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_allOrderFragment)
        }

        binding.button5.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        binding.button6.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationReminderFragment)
        }

        binding.button7.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_brandFragment)
        }



        return binding.root
    }


}