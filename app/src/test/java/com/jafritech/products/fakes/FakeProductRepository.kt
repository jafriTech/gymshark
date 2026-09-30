package com.jafritech.products.fakes


import com.jafritech.gymshark.core.util.DataError
import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.gymshark.domain.model.Product
import com.jafritech.gymshark.domain.repository.ProductRepository
import kotlinx.coroutines.CompletableDeferred

class FakeProductRepository : ProductRepository {

    var productsResult: DataResult<List<Product>> = DataResult.Success(emptyList())
    var productResult: DataResult<Product> = DataResult.Error(DataError.NotFound)

    /** Set to hold calls in-flight so loading states can be asserted. */
    var gate: CompletableDeferred<Unit>? = null

    var getProductsCalls: Int = 0
        private set
    var lastForceRefresh: Boolean? = null
        private set
    var lastRequestedId: Long? = null
        private set

    override suspend fun getProducts(forceRefresh: Boolean): DataResult<List<Product>> {
        getProductsCalls++
        lastForceRefresh = forceRefresh
        gate?.await()
        return productsResult
    }

    override suspend fun getProduct(id: Long): DataResult<Product> {
        lastRequestedId = id
        gate?.await()
        return productResult
    }
}