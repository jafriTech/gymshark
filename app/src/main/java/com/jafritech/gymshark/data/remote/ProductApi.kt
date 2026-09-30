package com.jafritech.gymshark.data.remote

import com.jafritech.gymshark.data.remote.dto.ProductsResponseDto
import retrofit2.http.GET

interface ProductApi {

    @GET(PRODUCTS_PATH)
    suspend fun getProducts(): ProductsResponseDto


    companion object {
        const val BASE_URL = "https://cdn.develop.gymshark.com/"
        const val PRODUCTS_PATH = "training/mock-product-responses/algolia-example-payload.json"
    }
}