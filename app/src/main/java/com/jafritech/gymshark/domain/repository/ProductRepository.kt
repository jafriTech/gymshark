package com.jafritech.gymshark.domain.repository

import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.gymshark.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(forceRefresh: Boolean = false): DataResult<List<Product>>
    suspend fun getProduct(id: Long): DataResult<Product>
}