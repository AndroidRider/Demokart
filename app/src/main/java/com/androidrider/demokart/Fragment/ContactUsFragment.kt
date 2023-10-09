package com.androidrider.demokart.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentAboutUsBinding
import com.androidrider.demokart.databinding.FragmentAccountBinding
import com.androidrider.demokart.databinding.FragmentContactUsBinding
import com.google.android.material.appbar.MaterialToolbar


class ContactUsFragment : Fragment() {

    private lateinit var binding: FragmentContactUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactUsBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        return binding.root
    }


}