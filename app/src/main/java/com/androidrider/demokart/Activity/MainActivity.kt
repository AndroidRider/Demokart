package com.androidrider.demokart.Activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.androidrider.demokart.Fragment.NotificationFragment
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var i = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openFragment()
        cartBadgeCountListener()
        notificationBadgeCountListener()

    }


    // Function to Navigate Fragments
    private fun openFragment() {

        // Check for the "openProfileFragment" extra
        val openProfileFragment = intent.getBooleanExtra("openProfileFragment", false)
        // If the extra is true, navigate to the "Profile" fragment
        if (openProfileFragment) {
            val navController = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                ?.findNavController()
            navController?.navigate(R.id.profileFragment)
        }
        //****

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment!!.findNavController()

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav)
        NavigationUI.setupWithNavController(binding.bottomBar, navController)

        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    i = 0
                    navController.navigate(R.id.homeFragment)
                }
                R.id.watchListFragment -> {
                    i = 1
                    navController.navigate(R.id.watchListFragment)
                }
                R.id.cartFragment -> {
                    i = 2
                    navController.navigate(R.id.cartFragment)
                }
                R.id.notificationFragment -> {
                    i = 2
                    navController.navigate(R.id.notificationFragment)
                }
                R.id.accountFragment -> {
                    i = 3
                    navController.navigate(R.id.accountFragment)
                }
            }
            true
        }
    }


    // Function to show the cart badge
    private fun showCartBadge(number: Int) {
        val badge = binding.bottomBar.getOrCreateBadge(R.id.cartFragment)
        badge.backgroundColor = Color.BLUE
        badge.badgeTextColor = Color.WHITE
        badge.isVisible = true
        badge.number = number
    }
    // Function to hide the cart badge
    fun hideCartBadge() {
        val badge = binding.bottomBar.getOrCreateBadge(R.id.cartFragment)
        badge?.isVisible = false
    }
    private fun showNotificationBadge(number: Int) {
        val badge = binding.bottomBar.getOrCreateBadge(R.id.notificationFragment)
        badge.isVisible = true
        badge.number = number
    }
    private fun hideNotificationBadge() {
        val badge = binding.bottomBar.getOrCreateBadge(R.id.notificationFragment)
        badge?.isVisible = false
    }

    // Function to Live update for badge count
    private fun notificationBadgeCountListener() {

        val cartCollectionRef = Firebase.firestore.collection("Notification")
        cartCollectionRef.addSnapshotListener { querySnapshot, error ->

            if (querySnapshot != null) {
                // Calculate the badge count based on the number of items in the cart
                val badgeCount = querySnapshot.documents.size
                // Update the badge count in your MainActivity
                if(badgeCount == 0){
                    hideNotificationBadge()
                }else{
                    showNotificationBadge(badgeCount)
                }
            }
        }
    }

    // Function to Live update for badge count
    private fun cartBadgeCountListener() {

        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val currentUserNumber = preferences.getString("number", "")


        val cartCollectionRef = Firebase.firestore.collection("Users")
            .document(currentUserNumber!!)
            .collection("Cart")
        cartCollectionRef.addSnapshotListener { querySnapshot, error ->

            if (querySnapshot != null) {
                // Calculate the badge count based on the number of items in the cart
                val badgeCount = querySnapshot.documents.size
                // Update the badge count in your MainActivity
                if(badgeCount == 0){
                    hideCartBadge()
                }else{
                showCartBadge(badgeCount)
                }
            }
        }
    }



    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment!!.findNavController()
        // Get the current destination
        val currentDestination = navController.currentDestination?.id

        // Check if the current destination is the account fragment
        if (currentDestination == R.id.profileFragment) {
            navController.navigateUp()

        }else if (currentDestination == R.id.myOrderFragment) {
            navController.navigateUp()

        }else if (currentDestination == R.id.contactUsFragment) {
            navController.navigateUp()

        }else if (currentDestination == R.id.aboutUsFragment) {
            navController.navigateUp()

        }else if (currentDestination == R.id.FAQsFragment) {
            navController.navigateUp()

        }else if (currentDestination == R.id.helpAndSupportFragment) {
            navController.navigateUp()

        }else if (currentDestination != R.id.homeFragment) {
            navController.navigate(R.id.homeFragment)

        } else {
            // If already on the home fragment, finish the activity and destroy the app
            finishAffinity()
        }
    }





        /*      End    */
//**************************************************************************
//    /* For smooth Bottombar*/
//    private fun smoothBottomBar() {
//
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//        val navController = navHostFragment!!.findNavController()
//
//        val popupMenu = PopupMenu(this, null)
//        popupMenu.inflate(R.menu.bottom_nav)
//        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)
//
//        binding.bottomBar.onItemSelected = {
//            when (it) {
//                0 -> {
//                    i = 0;
//                    navController.navigate(R.id.homeFragment)
//                }
//                1 -> i = 1
//                2 -> i = 2
//            }
//        }
//    }

//    /* For smooth Bottombar*/
//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (i == 0) {
//            finish()
//        }
//    }


}
