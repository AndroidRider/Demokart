package com.androidrider.demokart.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.androidrider.demokart.R
import com.androidrider.demokart.databinding.ActivityMainBinding
import org.checkerframework.checker.units.qual.s


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


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

                R.id.moreFragment -> {
                    i = 2
                    navController.navigate(R.id.moreFragment)
                }
            }
            true
        }

        /* For smooth Bottombar*/
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

    }


    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment!!.findNavController()
        // Get the current destination
        val currentDestination = navController.currentDestination?.id
        // Check if the current destination is not the home fragment
        if (currentDestination != R.id.homeFragment) {
            // Navigate to the home fragment
            navController.navigate(R.id.homeFragment)
        } else {
            // If already on the home fragment, finish the activity and destroy the app
            finishAffinity()
        }
    }



//    /* For smooth Bottombar*/
//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (i == 0) {
//            finish()
//        }
//    }

}
