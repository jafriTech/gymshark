package com.jafritech.gymshark.presentation.screen.productDetail

import androidx.lifecycle.SavedStateHandle
import com.jafritech.gymshark.core.util.DataError
import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.products.fakes.FakeProductRepository
import com.jafritech.products.fakes.testProduct
import com.jafritech.products.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeProductRepository
    private val product = testProduct(7)

    @Before
    fun setUp() {
        repository = FakeProductRepository().apply {
            productResult = DataResult.Success(product)
        }
    }

    private fun viewModel(productId: Long = 7L) = ProductDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf(ProductDetailViewModel.PRODUCT_ID_KEY to productId)),
        repository = repository,
    )

    @Test
    fun `requests the product id from navigation args`() = runTest {
        viewModel(productId = 42L)

        assertEquals(42L, repository.lastRequestedId)
    }

    @Test
    fun `shows loading while product is fetched`() = runTest {
        repository.gate = CompletableDeferred()

        val viewModel = viewModel()

        assertEquals(ProductDetailUiState.Loading, viewModel.uiState.value)

        repository.gate!!.complete(Unit)
        advanceUntilIdle()

        assertEquals(ProductDetailUiState.Content(product), viewModel.uiState.value)
    }

    @Test
    fun `shows product on success`() = runTest {
        assertEquals(ProductDetailUiState.Content(product), viewModel().uiState.value)
    }

    @Test
    fun `shows error when load fails`() = runTest {
        repository.productResult = DataResult.Error(DataError.Network)

        assertEquals(ProductDetailUiState.Error(DataError.Network), viewModel().uiState.value)
    }

    @Test
    fun `shows not found when product does not exist`() = runTest {
        repository.productResult = DataResult.Error(DataError.NotFound)

        assertEquals(ProductDetailUiState.Error(DataError.NotFound), viewModel().uiState.value)
    }

    @Test
    fun `retry after error loads product`() = runTest {
        repository.productResult = DataResult.Error(DataError.Network)
        val viewModel = viewModel()

        repository.productResult = DataResult.Success(product)
        viewModel.retry()

        assertEquals(ProductDetailUiState.Content(product), viewModel.uiState.value)
    }

    @Test
    fun `retry shows loading while in flight`() = runTest {
        repository.productResult = DataResult.Error(DataError.Network)
        val viewModel = viewModel()

        repository.gate = CompletableDeferred()
        viewModel.retry()

        assertEquals(ProductDetailUiState.Loading, viewModel.uiState.value)
    }
}