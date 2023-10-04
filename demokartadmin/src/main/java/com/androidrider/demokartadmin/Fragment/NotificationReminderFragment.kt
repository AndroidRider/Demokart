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
import com.androidrider.demokartadmin.Notification.NotificationModel
import com.androidrider.demokartadmin.Notification.NotificationPayload
import com.androidrider.demokartadmin.Notification.NotificationService
import com.androidrider.demokartadmin.R
import com.androidrider.demokartadmin.databinding.FragmentNotificationReminderBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class NotificationReminderFragment : Fragment() {

    lateinit var binding: FragmentNotificationReminderBinding

    lateinit var dialog : Dialog
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

        binding = FragmentNotificationReminderBinding.inflate(layoutInflater)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)


        binding.sendNotificationButton.setOnClickListener {
            sendNotification()
            binding.notificationTitleEdt.text!!.clear()
            binding.notificationBodyEdt.text!!.clear()
        }

        binding.apply {
            selectImageButton.setOnClickListener {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)

                imageBox.visibility = View.VISIBLE

                imageUrl = null
            }
        }

        binding.uploadImageButton.setOnClickListener {
            validateData()
        }

        return binding.root
    }



    private fun sendNotification() {

        val notificationTitle = binding.notificationTitleEdt.text.toString()
        val notificationBody = binding.notificationBodyEdt.text.toString()


        val imageUrl = "https://firebasestorage.googleapis.com/v0/b/demokart-38c8f.appspot.com/o/Users%2F29967167-b6df-4a2b-b23c-aae59c1d04ad.jpg?alt=media&token=5a44d5b4-c203-4829-9aa0-610d570e4217" // Replace with the actual image URL

        // Prepare the notification payload
        val notificationPayload = NotificationPayload(
            to = "/topics/all", // Replace "yourTopic" with the desired topic name
            notification = NotificationModel(title = notificationTitle, body = notificationBody, image = imageUrl)
        )

        // Create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /// Create a NotificationService instance
        val notificationService = retrofit.create(NotificationService::class.java)

        // Send the notification
        val call = notificationService.sendNotification(notificationPayload)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {



                    Toast.makeText(requireContext(), "Notification send successfully", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(requireContext(), "Failed to send Notification", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun validateData() {

        if(imageUrl != null){
            uploadImage()
        }else{
            Toast.makeText(requireContext(), "please select image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString()+".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("NotificationImages/$fileName")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->

                    storeImage(image.toString())
                }
            }

            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeImage(image: String) {
        val db = Firebase.firestore
        val data = hashMapOf<String, Any>( "NotificationImg" to image )
//        db.collection("NotificationImages").add(data) // it is adding one by  one
        db.collection("NotificationImages").document("item").set(data) // it is replacing
            .addOnSuccessListener {
                dialog.dismiss()
                binding.imageBox.visibility = GONE
                Toast.makeText(requireContext(), "Image Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }

}