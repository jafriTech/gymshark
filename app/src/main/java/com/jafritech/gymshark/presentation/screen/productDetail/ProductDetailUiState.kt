package com.jafritech.gymshark.presentation.screen.productDetail

import com.jafritech.gymshark.core.util.DataError
import com.jafritech.gymshark.domain.model.Product

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data class Content(val product: Product) : ProductDetailUiState
    data class Error(val error: DataError) : ProductDetailUiState
}