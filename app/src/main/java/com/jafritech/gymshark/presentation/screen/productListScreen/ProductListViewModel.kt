package com.jafritech.gymshark.presentation.screen.productListScreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jafritech.gymshark.core.util.DataResult
import com.jafritech.gymshark.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load(forceRefresh = false)
    }

    fun retry() {
        _uiState.value = ProductListUiState.Loading
        load(forceRefresh = true)
    }

    fun refresh() {
        _uiState.update { current ->
            if (current is ProductListUiState.Content) {
                current.copy(isRefreshing = true, refreshError = null)
            } else {
                ProductListUiState.Loading
            }
        }
        load(forceRefresh = true)
    }

    fun onRefreshErrorShown() {
        _uiState.update { current ->
            if (current is ProductListUiState.Content) current.copy(refreshError = null) else current
        }
    }

    private fun load(forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val previous = _uiState.value as? ProductListUiState.Content
            println("MIJ $previous")
            _uiState.value = when (val result = repository.getProducts(forceRefresh)) {
                is DataResult.Success ->
                    if (result.data.isEmpty()) {
                        ProductListUiState.Empty
                    } else {
                        println("MIJ ${result.data}")
                        ProductListUiState.Content(result.data)
                    }

                is DataResult.Error ->
                    previous?.copy(isRefreshing = false, refreshError = result.error)
                        ?: ProductListUiState.Error(result.error)
            }
        }
    }
}