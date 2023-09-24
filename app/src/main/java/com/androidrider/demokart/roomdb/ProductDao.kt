package com.androidrider.demokart.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: ProductModel)

    @Delete
    suspend fun deleteProduct(product: ProductModel)

    @Update
    suspend fun updateProduct(product: ProductModel)

    @Query("SELECT * FROM products")
    fun getAllProduct() : LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun isExit(id: String): ProductModel


    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: String): ProductModel?
//
//    // Define a method to check if a product is in the cart or marked as a favorite
//    @Query("SELECT * FROM products WHERE productId = :productId AND isFavorite = 1")
//    suspend fun isProductInCart(productId: String): ProductModel?

}