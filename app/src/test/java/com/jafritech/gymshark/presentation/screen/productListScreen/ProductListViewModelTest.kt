package com.jafritech.gymshark.presentation.screen.productListScreen

import com.jafritech.gymshark.core.util.DataError
import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.products.fakes.FakeProductRepository
import com.jafritech.products.fakes.testProduct
import com.jafritech.products.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeProductRepository
    private val products = listOf(testProduct(1), testProduct(2))

    @Before
    fun setUp() {
        repository = FakeProductRepository().apply {
            productsResult = DataResult.Success(products)
        }
    }

    private fun viewModel() = ProductListViewModel(repository)

    // --- initial load ---

    @Test
    fun `shows loading while products are fetched`() = runTest {
        repository.gate = CompletableDeferred()

        val viewModel = viewModel()

        assertEquals(ProductListUiState.Loading, viewModel.uiState.value)

        repository.gate!!.complete(Unit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProductListUiState.Content)
    }

    @Test
    fun `shows products on success`() = runTest {
        val viewModel = viewModel()

        assertEquals(ProductListUiState.Content(products), viewModel.uiState.value)
    }

    @Test
    fun `initial load does not force refresh`() = runTest {
        viewModel()

        assertEquals(false, repository.lastForceRefresh)
    }

    @Test
    fun `shows empty when no products`() = runTest {
        repository.productsResult = DataResult.Success(emptyList())

        assertEquals(ProductListUiState.Empty, viewModel().uiState.value)
    }

    @Test
    fun `shows error when load fails`() = runTest {
        repository.productsResult = DataResult.Error(DataError.Network)

        assertEquals(ProductListUiState.Error(DataError.Network), viewModel().uiState.value)
    }

    // --- retry ---

    @Test
    fun `retry after error loads products`() = runTest {
        repository.productsResult = DataResult.Error(DataError.Network)
        val viewModel = viewModel()

        repository.productsResult = DataResult.Success(products)
        viewModel.retry()

        assertEquals(ProductListUiState.Content(products), viewModel.uiState.value)
        assertEquals(2, repository.getProductsCalls)
    }

    @Test
    fun `retry shows loading while in flight`() = runTest {
        repository.productsResult = DataResult.Error(DataError.Network)
        val viewModel = viewModel()

        repository.gate = CompletableDeferred()
        viewModel.retry()

        assertEquals(ProductListUiState.Loading, viewModel.uiState.value)
    }

    // --- refresh ---

    @Test
    fun `refresh forces a network fetch`() = runTest {
        val viewModel = viewModel()

        viewModel.refresh()

        assertEquals(true, repository.lastForceRefresh)
        assertEquals(2, repository.getProductsCalls)
    }

    @Test
    fun `refresh keeps content visible while refreshing`() = runTest {
        val viewModel = viewModel()

        repository.gate = CompletableDeferred()
        viewModel.refresh()

        assertEquals(
            ProductListUiState.Content(products, isRefreshing = true),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `refresh updates content with new products`() = runTest {
        val viewModel = viewModel()
        val updated = listOf(testProduct(3))

        repository.productsResult = DataResult.Success(updated)
        viewModel.refresh()

        assertEquals(ProductListUiState.Content(updated), viewModel.uiState.value)
    }

    @Test
    fun `failed refresh keeps content and exposes refresh error`() = runTest {
        val viewModel = viewModel()

        repository.productsResult = DataResult.Error(DataError.Network)
        viewModel.refresh()

        assertEquals(
            ProductListUiState.Content(products, isRefreshing = false, refreshError = DataError.Network),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `refresh error is cleared once shown`() = runTest {
        val viewModel = viewModel()
        repository.productsResult = DataResult.Error(DataError.Network)
        viewModel.refresh()

        viewModel.onRefreshErrorShown()

        assertEquals(ProductListUiState.Content(products), viewModel.uiState.value)
    }
}