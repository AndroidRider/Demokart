package com.androidrider.demokartadmin.Activity

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App: Application() {

    private var CHANNEL_ID = "CHANNEL_ID"

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            val channel = NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "This is my High importance notification channel"

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

    }
}