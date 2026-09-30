package com.jafritech.gymshark.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ProductListRoute

@Serializable
data class ProductDetailRoute(val productId: Long)