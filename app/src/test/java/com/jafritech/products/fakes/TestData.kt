package com.jafritech.products.fakes

import com.jafritech.gymshark.domain.model.Product

fun testProduct(id: Long, title: String = "Product $id") = Product(
    id = id,
    title = title,
    colour = "Black",
    type = "Womens Leggings",
    price = 45.0,
    imageUrl = "https://cdn.test/$id.jpg",
    imageUrls = listOf("https://cdn.test/$id.jpg"),
    labels = emptyList(),
    sizes = emptyList(),
    inStock = true,
    descriptionHtml = "<p>Description</p>",
)