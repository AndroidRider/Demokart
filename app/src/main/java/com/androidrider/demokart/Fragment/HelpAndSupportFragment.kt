package com.androidrider.demokart.Fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Model.QueryModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentHelpAndSupportBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HelpAndSupportFragment : Fragment() {

    private lateinit var binding: FragmentHelpAndSupportBinding
    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHelpAndSupportBinding.inflate(layoutInflater)

        // Access the toolbar view - Show/Hide
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)


        binding.submitButton.setOnClickListener {
            uploadQuery()
        }



        return binding.root
    }

    private fun uploadQuery() {

        val preferences =requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val phoneNumber = preferences.getString("number", "")

        val topic = binding.edtTopic.text.toString()

        val data = QueryModel(
        binding.edtUserName.text.toString(),
        binding.edtPhoneNumber.text.toString(),
        binding.edtEmail.text.toString(),
        binding.edtQuery.text.toString()
        )


        Firebase.firestore.collection("Contact, Help-Supports").document(phoneNumber!!)
            .collection("Help & Supports").document(topic)
            .set(data).addOnSuccessListener {

                binding.edtUserName.text = null
                binding.edtPhoneNumber.text = null
                binding.edtEmail.text = null
                binding.edtTopic.text = null
                binding.edtQuery.text = null
                Toast.makeText(requireContext(), "Query Submitted", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


}