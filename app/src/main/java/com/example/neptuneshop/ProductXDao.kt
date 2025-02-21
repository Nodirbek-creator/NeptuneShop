package com.example.neptuneshop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.neptuneshop.model.ProductX

@Dao
interface ProductXDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun buyProduct(productX: ProductX)
    @Query("SELECT * FROM purchased_products")
    fun getAllPurchased():List<ProductX>
}