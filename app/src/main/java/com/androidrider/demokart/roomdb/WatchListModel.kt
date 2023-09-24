package com.androidrider.demokart.roomdb

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchList_table")
data class WatchListModel(
    @PrimaryKey
    @NonNull
    val productId : String,

    @ColumnInfo(name = "productName")
    val productName : String? = "",

    @ColumnInfo(name = "productImage")
    val productImage : String? = "",

    @ColumnInfo(name = "productSp")
    val productSp : String? = "",

    @ColumnInfo(name = "productMrp")
    val productMrp : String? = "",


)
