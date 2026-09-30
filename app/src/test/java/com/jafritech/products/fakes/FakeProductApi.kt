package com.jafritech.products.fakes

import com.jafritech.gymshark.data.remote.ProductApi
import com.jafritech.gymshark.data.remote.dto.ProductsResponseDto

class FakeProductApi: ProductApi {
    var response: ProductsResponseDto = ProductsResponseDto(hits = emptyList())
    var error: Throwable? = null

    var callCount: Int = 0
        private set

    override suspend fun getProducts(): ProductsResponseDto {
        callCount++
        error?.let { throw it }
        return response
    }
}