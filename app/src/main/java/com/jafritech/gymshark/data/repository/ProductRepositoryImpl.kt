package com.jafritech.gymshark.data.repository

import com.jafritech.gymshark.core.di.IoDispatcher
import com.jafritech.gymshark.core.util.DataError
import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.gymshark.data.mapper.toDomain
import com.jafritech.gymshark.data.remote.ProductApi
import com.jafritech.gymshark.domain.model.Product
import com.jafritech.gymshark.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException


@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): ProductRepository {

    private val mutex = Mutex()
    private var cache: List<Product>? = null

    override suspend fun getProducts(forceRefresh: Boolean): DataResult<List<Product>> =
        withContext(ioDispatcher) {
            mutex.withLock {
                val cached = cache
                if (!forceRefresh && cached != null) {
                    return@withLock DataResult.Success(cached)
                }
                safeCall { api.getProducts().toDomain() }.also { result ->
                    if (result is DataResult.Success) cache = result.data
                }
            }
        }

    override suspend fun getProduct(id: Long): DataResult<Product> =
        when (val result = getProducts()) {
            is DataResult.Success -> result.data.firstOrNull { it.id == id }
                ?.let { DataResult.Success(it) }
                ?: DataResult.Error(DataError.NotFound)
            is DataResult.Error -> result
        }

    private suspend fun <T> safeCall(block: suspend () -> T): DataResult<T> =
        try {
            DataResult.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            DataResult.Error(DataError.Network)
        } catch (e: HttpException) {
            DataResult.Error(DataError.Server)
        } catch (e: SerializationException) {
            DataResult.Error(DataError.Parsing)
        } catch (e: Exception) {
            DataResult.Error(DataError.Unknown)
        }
}