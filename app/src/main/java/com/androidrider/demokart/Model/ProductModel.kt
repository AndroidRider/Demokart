package com.androidrider.demokart.Model

data class ProductModel(

    val productId: String? = "",
    val uid: String? = "",
    val productName: String? = "",
    val productDescription: String? = "",
    val productFeatures: String? = "",
    val productCoverImage: String? = "",
    val productCategory: String? = "",
    val productMrp: String? = "",
    val productSp: String? = "",
    val rating: Double = 0.0,
    val productImages: ArrayList<String> = ArrayList(),

    val checkFavorite: Boolean = false // Default to false

    )
