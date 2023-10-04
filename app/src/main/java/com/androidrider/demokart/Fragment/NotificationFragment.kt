package com.androidrider.demokart.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import com.androidrider.demokart.Adapter.NotificationAdapter
import com.androidrider.demokart.Model.NotificationModel
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.FragmentNotificationBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotificationFragment : Fragment(){

    lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentNotificationBinding.inflate(layoutInflater)

        // Access the toolbar view
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        // Set the visibility of the toolbar (for example, hide it)
        toolbar.visibility = View.VISIBLE

        getDataInRecycleView()

        binding.progressBar.visibility = View.VISIBLE

        return binding.root
    }

    private fun getDataInRecycleView() {

        val list = ArrayList<NotificationModel>()

        Firebase.firestore.collection("Notification")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get().addOnSuccessListener {querySnapshot->

                list.clear()
                for (doc in querySnapshot.documents){
                    val data = doc.toObject(NotificationModel::class.java)
                    list.add(data!!)

                }

                binding.notificationRecyclerView.adapter = NotificationAdapter(requireContext(), list)


                binding.progressBar.visibility = GONE
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Toast.makeText(requireContext(), "Error fetching notifications: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

    }



}