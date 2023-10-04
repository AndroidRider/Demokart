//package com.androidrider.demokart.roomdb
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Update
//
//@Dao
//interface WatchListDao {
//
//    @Insert
//    suspend fun insertProduct(product: WatchlistModel)
//
//    @Delete
//    suspend fun deleteProduct(product: WatchlistModel)
//
//    @Update
//    suspend fun updateProduct(product: WatchlistModel)
//
//    @Query("SELECT * FROM watchList_table")
//    fun getAllWatchListItems() : LiveData<List<WatchlistModel>>
//
//    @Query("SELECT * FROM watchList_table WHERE productId = :id")
//    fun isProductInWatchList(id: String): WatchlistModel
//
//
//
//
//}