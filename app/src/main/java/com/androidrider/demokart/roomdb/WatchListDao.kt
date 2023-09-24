package com.androidrider.demokart.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WatchListDao {

    @Insert
    suspend fun insertProduct(product: WatchListModel)

    @Delete
    suspend fun deleteProduct(product: WatchListModel)

    @Update
    suspend fun updateProduct(product: WatchListModel)

    @Query("SELECT * FROM watchList_table")
    fun getAllWatchListItems() : LiveData<List<WatchListModel>>

    @Query("SELECT * FROM watchList_table WHERE productId = :id")
    fun isProductInWatchList(id: String): WatchListModel



}