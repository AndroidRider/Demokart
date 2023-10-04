package com.androidrider.demokart.Model

import com.google.firebase.Timestamp

data class NotificationModel(

    var title : String? = "",
    var content : String? = "",
    var image: String? = "",

    val timestamp: Timestamp? = null
)
 {
    constructor() : this("", "", null)
}