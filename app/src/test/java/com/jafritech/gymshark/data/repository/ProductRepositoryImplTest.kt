package com.jafritech.gymshark.data.repository



import com.jafritech.gymshark.core.util.DataError
import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.gymshark.data.remote.dto.ProductDto
import com.jafritech.gymshark.data.remote.dto.ProductsResponseDto
import com.jafritech.products.fakes.FakeProductApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ProductRepositoryImplTest {

    private lateinit var api: FakeProductApi

    private fun TestScope.repository() =
        ProductRepositoryImpl(api, StandardTestDispatcher(testScheduler))

    private fun responseOf(vararg ids: Long) = ProductsResponseDto(
        hits = ids.map { ProductDto(id = it, title = "Product $it") },
    )

    @Before
    fun setUp() {
        api = FakeProductApi().apply { response = responseOf(1, 2, 3) }
    }

    // --- getProducts ---

    @Test
    fun `returns mapped products on success`() = runTest {
        val result = repository().getProducts()

        assertTrue(result is DataResult.Success)
        assertEquals(listOf(1L, 2L, 3L), (result as DataResult.Success).data.map { it.id })
    }

    @Test
    fun `drops invalid products from the response`() = runTest {
        api.response = ProductsResponseDto(
            hits = listOf(ProductDto(id = 1, title = "Valid"), ProductDto(id = null, title = "No id")),
        )

        val result = repository().getProducts() as DataResult.Success

        assertEquals(1, result.data.size)
    }

    @Test
    fun `second call is served from cache`() = runTest {
        val repo = repository()

        repo.getProducts()
        repo.getProducts()

        assertEquals(1, api.callCount)
    }

    @Test
    fun `force refresh hits the api again`() = runTest {
        val repo = repository()

        repo.getProducts()
        repo.getProducts(forceRefresh = true)

        assertEquals(2, api.callCount)
    }

    @Test
    fun `io exception maps to network error`() = runTest {
        api.error = IOException("offline")

        assertEquals(DataResult.Error(DataError.Network), repository().getProducts())
    }

    @Test
    fun `http exception maps to server error`() = runTest {
        api.error = HttpException(Response.error<Any>(500, "".toResponseBody()))

        assertEquals(DataResult.Error(DataError.Server), repository().getProducts())
    }

    @Test
    fun `serialization exception maps to parsing error`() = runTest {
        api.error = SerializationException("bad json")

        assertEquals(DataResult.Error(DataError.Parsing), repository().getProducts())
    }

    @Test
    fun `unexpected exception maps to unknown error`() = runTest {
        api.error = IllegalStateException("boom")

        assertEquals(DataResult.Error(DataError.Unknown), repository().getProducts())
    }

    @Test
    fun `errors are not cached so retry can succeed`() = runTest {
        val repo = repository()
        api.error = IOException("offline")
        repo.getProducts()

        api.error = null
        val result = repo.getProducts()

        assertTrue(result is DataResult.Success)
        assertEquals(2, api.callCount)
    }

    // --- getProduct ---

    @Test
    fun `getProduct returns product from cache without extra call`() = runTest {
        val repo = repository()
        repo.getProducts()

        val result = repo.getProduct(2) as DataResult.Success

        assertEquals(2L, result.data.id)
        assertEquals(1, api.callCount)
    }

    @Test
    fun `getProduct fetches when cache is empty`() = runTest {
        val result = repository().getProduct(3)

        assertTrue(result is DataResult.Success)
        assertEquals(1, api.callCount)
    }

    @Test
    fun `getProduct with unknown id returns not found`() = runTest {
        assertEquals(DataResult.Error(DataError.NotFound), repository().getProduct(99))
    }

    @Test
    fun `getProduct propagates fetch errors`() = runTest {
        api.error = IOException("offline")

        assertEquals(DataResult.Error(DataError.Network), repository().getProduct(1))
    }
}