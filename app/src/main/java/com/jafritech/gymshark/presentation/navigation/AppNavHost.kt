package com.jafritech.gymshark.presentation.navigation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jafritech.gymshark.presentation.screen.productDetail.ProductDetailScreen
import com.jafritech.gymshark.presentation.screen.productListScreen.ProductListScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier.fillMaxSize(),
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ProductListRoute,
        modifier = modifier,
    ) {
        composable<ProductListRoute> {
            ProductListScreen(
                onProductClick = { id ->
                    navController.navigate(ProductDetailRoute(id))
                },
            )
        }

        composable<ProductDetailRoute> {
            ProductDetailScreen(
                onBack = navController::navigateUp,
            )
        }
    }
}