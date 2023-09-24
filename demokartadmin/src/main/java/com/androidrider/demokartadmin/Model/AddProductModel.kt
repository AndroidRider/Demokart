package com.androidrider.demokartadmin.Model

data class AddProductModel(

    val productName: String? = "",
    val productDescription: String? = "",
    val productCoverImage: String? = "",
    val productCategory: String? = "",
    val productId: String? = "",
    val productMrp: String? = "",
    val productSp: String? = "",
    val productImages: ArrayList<String>


)
