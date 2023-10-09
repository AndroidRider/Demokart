package com.androidrider.demokart.Model

import com.google.firebase.Timestamp

data class MyOrderModel(

    val productName : String? = "",
    val productPrice : String? = "",
    val totalPrice : String? = "",
    val coverImage : String? = "",
    val userId : String? = "",
    val productId : String? = "",
    val orderId : String? = "",
    val status : String? = "",
    val quantity : String? = "",

    val timestamp: Timestamp? = null
)
{
    constructor() : this("", "", null)
}

