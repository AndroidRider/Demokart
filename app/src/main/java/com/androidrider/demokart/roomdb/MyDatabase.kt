package com.androidrider.demokart.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


//@Database(entities = [ProductModel::class], version = 1)
@Database(entities = [ProductModel::class, WatchListModel::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    companion object{
        private var database : MyDatabase? = null
        private val DATABASE_NAME ="demokart"

        @Synchronized
        fun getInstance(context: Context) : MyDatabase{

            if (database == null){
                database = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    DATABASE_NAME
                ).allowMainThreadQueries().fallbackToDestructiveMigration()
                    .build()
            }
            return database!!
        }
    }
    abstract fun productDao(): ProductDao
    abstract fun watchListDao(): WatchListDao
}