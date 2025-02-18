package com.example.neptuneshop.model

import com.example.neptuneshop.model.Product

data class Products(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)