//package com.androidrider.demokart.Model

data class CartModel(
    val productId: String? = "",
    val productName: String? = "",
    val productSp: String? = "",
    val productMrp: String? = "",
    val productImage : String? = "",
    val rating : Double = 0.0,

    var quantity: Int = 1 // Set default quantity to 1


)
