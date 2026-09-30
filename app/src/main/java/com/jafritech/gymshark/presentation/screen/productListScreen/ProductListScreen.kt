package com.jafritech.gymshark.presentation.screen.productListScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jafritech.gymshark.R
import com.jafritech.gymshark.domain.model.Product
import com.jafritech.gymshark.domain.model.ProductLabel
import com.jafritech.gymshark.presentation.common.messageRes
import com.jafritech.gymshark.presentation.composable.EmptyState
import com.jafritech.gymshark.presentation.composable.ErrorState
import com.jafritech.gymshark.presentation.composable.PRODUCT_IMAGE_RATIO
import com.jafritech.gymshark.presentation.composable.PlaceholderBox
import com.jafritech.gymshark.presentation.composable.ProductCard
import com.jafritech.gymshark.presentation.ui.theme.GymSharkTheme


/** Stateful entry point: wires the ViewModel. */
@Composable
fun ProductListScreen(
    onProductClick: (Long) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProductListContent(
        state = state,
        onProductClick = onProductClick,
        onRetry = viewModel::retry,
        onRefresh = viewModel::refresh,
        onRefreshErrorShown = viewModel::onRefreshErrorShown,
    )
}

/** Stateless: driven purely by state, so it's previewable and UI-testable. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListContent(
    state: ProductListUiState,
    onProductClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onRefreshErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshError = (state as? ProductListUiState.Content)?.refreshError
    val refreshErrorMessage = refreshError?.let { stringResource(it.messageRes()) }

    LaunchedEffect(refreshError) {
        if (refreshErrorMessage != null) {
            snackbarHostState.showSnackbar(refreshErrorMessage)
            onRefreshErrorShown()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.products_title)) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (state) {
                ProductListUiState.Loading -> ProductGridPlaceholder()

                ProductListUiState.Empty -> EmptyState(
                    message = stringResource(R.string.empty_products),
                    onRetry = onRetry,
                )

                is ProductListUiState.Error -> ErrorState(
                    message = stringResource(state.error.messageRes()),
                    onRetry = onRetry,
                )

                is ProductListUiState.Content -> PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ProductGrid(products = state.products, onProductClick = onProductClick)
                }
            }
        }
    }
}

private val GridMinCellWidth = 160.dp
private val GridPadding = PaddingValues(16.dp)

@Composable
private fun ProductGrid(
    products: List<Product>,
    onProductClick: (Long) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(GridMinCellWidth),
        contentPadding = GridPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(product = product, onClick = { onProductClick(product.id) })
        }
    }
}

@Composable
private fun ProductGridPlaceholder() {
    val loadingDescription = stringResource(R.string.loading_products)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(GridMinCellWidth),
        contentPadding = GridPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = loadingDescription },
    ) {
        items(6) {
            Column {
                PlaceholderBox(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(PRODUCT_IMAGE_RATIO)
                        .clip(MaterialTheme.shapes.medium),
                )
                Spacer(Modifier.height(8.dp))
                PlaceholderBox(
                    Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp),
                )
                Spacer(Modifier.height(6.dp))
                PlaceholderBox(
                    Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp),
                )
            }
        }
    }
}

// --- previews ---

private val previewProducts = listOf(
    Product(
        id = 1, title = "Speed Leggings", colour = "Navy", type = "Womens Leggings",
        price = 45.0, imageUrl = null, imageUrls = emptyList(),
        labels = listOf(ProductLabel.GoingFast), sizes = emptyList(),
        inStock = true, descriptionHtml = "",
    ),
    Product(
        id = 2, title = "Vital Seamless 2.0 Leggings", colour = "Tahoe Teal Marl", type = "Womens Leggings",
        price = 1000.0, imageUrl = null, imageUrls = emptyList(),
        labels = emptyList(), sizes = emptyList(),
        inStock = false, descriptionHtml = "",
    ),
)

@Preview(showBackground = true)
@Composable
private fun ProductListContentPreview() {
    GymSharkTheme {
        ProductListContent(
            state = ProductListUiState.Content(previewProducts),
            onProductClick = {}, onRetry = {}, onRefresh = {}, onRefreshErrorShown = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListLoadingPreview() {
    GymSharkTheme {
        ProductListContent(
            state = ProductListUiState.Loading,
            onProductClick = {}, onRetry = {}, onRefresh = {}, onRefreshErrorShown = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListErrorPreview() {
    GymSharkTheme {
        ProductListContent(
            state = ProductListUiState.Error(com.jafritech.gymshark.core.util.DataError.Network),
            onProductClick = {}, onRetry = {}, onRefresh = {}, onRefreshErrorShown = {},
        )
    }
}