package com.androidrider.demokartadmin.Model

data class ProductModel(

    val productId: String? = "",
    val productName: String? = "",
    val productMrp: String? = "",
    val productSp: String? = "",
    val productDescription: String? = "",
    val productFeatures: String? = "",
    val productCoverImage: String? = "",
    val productCategory: String? = "",
    val productImages: ArrayList<String> = ArrayList(),
)

