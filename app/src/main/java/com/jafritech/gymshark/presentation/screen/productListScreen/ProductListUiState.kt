package com.jafritech.gymshark.presentation.screen.productListScreen

import com.jafritech.gymshark.core.util.DataError
import com.jafritech.gymshark.domain.model.Product

sealed interface ProductListUiState {

    data object Loading : ProductListUiState

    data object Empty : ProductListUiState

    data class Content(
        val products: List<Product>,
        val isRefreshing: Boolean = false,
        val refreshError: DataError? = null,
    ) : ProductListUiState

    data class Error(val error: DataError) : ProductListUiState
}