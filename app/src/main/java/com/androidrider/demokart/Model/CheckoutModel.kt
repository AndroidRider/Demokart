package com.androidrider.demokart.Model

data class CheckoutModel(
    val productId: String = "",
    val productName: String = "",
    val productSp: String = "",
    val productMrp: String = "",
    val productImage: String = "",
    var quantity: Int = 0
)

