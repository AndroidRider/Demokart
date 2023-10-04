package com.androidrider.demokartadmin.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.androidrider.demokartadmin.Adapter.NotificationAdapter
import com.androidrider.demokartadmin.Model.NotificationModel
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentNotificationBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class NotificationFragment : Fragment() {

    lateinit var binding: FragmentNotificationBinding

    private lateinit var dialog : Dialog
    private var imageUrl : Uri? = null
    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            imageUrl = it.data?.data
            binding.imageBox.setImageURI(imageUrl)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentNotificationBinding.inflate(layoutInflater)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectImageButton.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
            binding.imageBox.visibility = View.VISIBLE
        }

        binding.sendNotificationButton.setOnClickListener {

            val title = binding.notificationTitleEdt.text.toString()
            val content = binding.notificationContentEdt.text.toString()

            uploadNotification(title,content)

        }

        getDataInRecycleView()


        binding.progressBar.visibility = View.VISIBLE
        return binding.root
    }



    private fun uploadNotification(notificationTitle: String, notificationContent: String) {
        dialog.show()
        val fileName = UUID.randomUUID().toString()+".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Category/$fileName")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->

                    storeData(notificationTitle, notificationContent, image.toString())
                }
            }

            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(notificationTitle: String, notificationContent: String, image: String) {

        // Create a timestamp on the client side
        val currentTime = Timestamp.now()

// Convert the timestamp to a Date object (optional)
        val date = currentTime.toDate()



        val db = Firebase.firestore
        val data = hashMapOf(
            "title" to notificationTitle,
            "content" to notificationContent,
            "image" to image,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("Notification").add(data)
            .addOnSuccessListener {
                dialog.dismiss()

                binding.notificationTitleEdt.text!!.clear()
                binding.notificationContentEdt.text!!.clear()
                binding.imageBox.visibility = GONE

                Toast.makeText(requireContext(), "Notification Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
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