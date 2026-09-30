package com.jafritech.gymshark.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Mirrors the raw API payload. Every field is nullable with a default so a
 * missing or null key never fails decoding — validation happens in the mapper.
 */
@Serializable
data class ProductsResponseDto(
    @SerialName("hits") val hits: List<ProductDto>? = null,
)

@Serializable
data class ProductDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("sku") val sku: String? = null,
    @SerialName("inStock") val inStock: Boolean? = null,
    @SerialName("sizeInStock") val sizeInStock: List<String>? = null,
    @SerialName("availableSizes") val availableSizes: List<SizeDto>? = null,
    @SerialName("handle") val handle: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("gender") val gender: List<String>? = null,
    @SerialName("fit") val fit: String? = null,
    @SerialName("labels") val labels: List<String>? = null,
    @SerialName("colour") val colour: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("compareAtPrice") val compareAtPrice: Double? = null,
    @SerialName("discountPercentage") val discountPercentage: Double? = null,
    @SerialName("featuredMedia") val featuredMedia: MediaDto? = null,
    @SerialName("media") val media: List<MediaDto>? = null,
    @SerialName("objectID") val objectId: String? = null,
)

@Serializable
data class SizeDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("inStock") val inStock: Boolean? = null,
    @SerialName("inventoryQuantity") val inventoryQuantity: Int? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("size") val size: String? = null,
    @SerialName("sku") val sku: String? = null,
)

@Serializable
data class MediaDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("src") val src: String? = null,
    @SerialName("alt") val alt: String? = null,
    @SerialName("position") val position: Int? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null,
)
