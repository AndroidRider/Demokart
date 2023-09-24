package com.androidrider.demokart.Fragment


import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.demokart.Activity.LoginActivity
import com.androidrider.demokart.Adapter.StatusAdapter
import com.androidrider.demokart.Model.StatusModel
import com.androidrider.demokart.databinding.FragmentMoreBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MoreFragment : Fragment() {

    lateinit var binding: FragmentMoreBinding
    lateinit var list : ArrayList<StatusModel>
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(layoutInflater)

        list = ArrayList()


        val preferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)

        Firebase.firestore.collection("AllOrders")
            .whereEqualTo("userId", preferences.getString("number", "")!!)
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it) {
                    val data = doc.toObject(StatusModel::class.java)
                    list.add(data)
                }
                binding.recyclerView.adapter = StatusAdapter(requireContext(), list)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }

        logoutUser() // logout method

        return binding.root
    }

    // method to logout User
    private fun logoutUser() {
        binding.logoutButton.setOnClickListener {
            sharedPreferences =requireContext().getSharedPreferences("loginPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))

        }
    }




}