package com.jafritech.gymshark.data.remote

import com.jafritech.gymshark.core.network.ApiJson
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import org.junit.Assert.*


class ProductApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: ProductApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ApiJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ProductApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `requests the products path`() = runTest {
        server.enqueue(MockResponse().setBody("""{"hits":[]}"""))

        api.getProducts()

        assertEquals("/${ProductApi.PRODUCTS_PATH}", server.takeRequest().path)
    }

    @Test
    fun `parses products with nulls and unknown fields`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"hits":[{"id":1,"title":"Speed Leggings","labels":null,"price":1000,"unknown":true}]}"""
            )
        )

        val result = api.getProducts()

        assertEquals(1, result.hits?.size)
        assertEquals("Speed Leggings", result.hits!!.first().title)
        assertNull(result.hits.first().labels)
    }

    @Test(expected = HttpException::class)
    fun `throws on server error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))

        api.getProducts()
    }
}