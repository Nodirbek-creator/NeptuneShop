package com.example.neptuneshop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.neptuneshop.model.FavProduct
import com.example.neptuneshop.model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM favorite_products")
    fun allFavorites():List<FavProduct>
    @Query("SELECT * FROM favorite_products WHERE id = :id")
    fun favoriteById(id:Int):FavProduct?
    @Insert
    fun addFavorite(favProduct: FavProduct)

    @Query("DELETE FROM favorite_products WHERE id = :id")
    fun deleteFavById(id: Int)
}