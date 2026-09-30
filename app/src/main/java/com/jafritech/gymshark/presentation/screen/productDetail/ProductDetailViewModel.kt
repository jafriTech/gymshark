package com.jafritech.gymshark.presentation.screen.productDetail


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.gymshark.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository,
) : ViewModel() {

    private val productId: Long = checkNotNull(savedStateHandle[PRODUCT_ID_KEY]) {
        "ProductDetailViewModel requires a '$PRODUCT_ID_KEY' argument"
    }

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun retry() {
        _uiState.value = ProductDetailUiState.Loading
        load()
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = when (val result = repository.getProduct(productId)) {
                is DataResult.Success -> ProductDetailUiState.Content(result.data)
                is DataResult.Error -> ProductDetailUiState.Error(result.error)
            }
        }
    }

    companion object {
        /** Must match the property name in ProductDetailRoute. */
        const val PRODUCT_ID_KEY = "productId"
    }
}