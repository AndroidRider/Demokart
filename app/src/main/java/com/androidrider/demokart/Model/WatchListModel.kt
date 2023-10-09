package com.androidrider.demokart.Model

data class WatchListModel (

    val productId : String? = "",
    val productName : String? = "",
    val productSp : String? = "",
    val productMrp : String? = "",
    val productImage : String? = "",
    val rating : Double = 0.0,

    var isInWatchlist: Boolean = false,


)