package com.androidrider.demokartadmin.Notification

data class NotificationPayload(

    val to: String, // User's FCM token
    val notification: NotificationModel
)


