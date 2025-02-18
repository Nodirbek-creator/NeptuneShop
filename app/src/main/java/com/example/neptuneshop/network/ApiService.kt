package com.example.neptuneshop.network

import com.example.neptuneshop.model.Login
import com.example.neptuneshop.model.Product
import com.example.neptuneshop.model.Products
import com.example.neptuneshop.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService{
    //      https://dummyjson.com/products
    @GET("/products")
    fun getProducts(
        @Query("limit") limit: Int = 50,
    ): Call<Products>
    // https://dummyjson.com/products/category/smartphones'
    @GET("/products/category/{category}")
    fun getProductsByCategory(
        @Path("category") category: String,
        @Query("limit") limit: Int = 50,
    ): Call<Products>
    //    https://dummyjson.com/products/6
    @GET("/products/{id}")
    fun getProductById(@Path("id") id: Int): Call<Product>

    //    https://dummyjson.com/products/search?q=phone
    @GET("/products/search")
    fun searchProducts(@Query("q") q: String): Call<Products>


    //    https://dummyjson.com/auth/login
    @POST("/auth/login")
    fun login(@Body login: Login): Call<User>
}