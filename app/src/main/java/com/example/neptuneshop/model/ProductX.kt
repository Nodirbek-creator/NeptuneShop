package com.example.neptuneshop.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchased_products")
data class ProductX(
    val discountPercentage: Double,
    val discountedTotal: Double,
    val id: Int,
    val price: Double,
    val quantity: Int,
    @PrimaryKey
    val thumbnail: String,
    val title: String,
    val total: Double
)