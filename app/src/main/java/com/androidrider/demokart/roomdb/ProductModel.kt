package com.androidrider.demokart.roomdb

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey
    @NonNull
    val productId : String,

    @ColumnInfo(name = "productName")
    val productName : String? = "",

    @ColumnInfo(name = "productImage")
    val productImage : String? = "",

    @ColumnInfo(name = "productSp")
    val productSp : String? = "",

//    // for watchlist added
//    @ColumnInfo(name = "productMrp")
//    val productMrp : String? = "",
//
//    @ColumnInfo(name = "isFavorite")
//    var isFavorite: Boolean = false // New field for favorite status

)
