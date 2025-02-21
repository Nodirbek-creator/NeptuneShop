package com.example.neptuneshop

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.neptuneshop.model.FavProduct
import com.example.neptuneshop.model.ProductX

@Database(entities = [FavProduct::class, ProductX::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): ProductDao
    abstract fun purchaseDao(): ProductXDao


    companion object{
        const val DB_NAME = "contacts_app_db"
        var instance:AppDatabase? = null

        fun getInstance(context: Context):AppDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}
