package com.jafritech.gymshark.domain.model


data class Product(
    val id: Long,
    val title: String,
    val colour: String?,
    val type: String?,
    val price: Double?,
    val imageUrl: String?,
    val imageUrls: List<String>,
    val labels: List<ProductLabel>,
    val sizes: List<ProductSize>,
    val inStock: Boolean,
    val descriptionHtml: String,
)

data class ProductSize(
    val label: String,
    val inStock: Boolean,
)

sealed interface ProductLabel {
    data object GoingFast : ProductLabel
    data class Other(val raw: String) : ProductLabel

    /** Human-readable text, e.g. "going-fast" -> "Going Fast". */
    val displayName: String
        get() = when (this) {
            GoingFast -> "Going Fast"
            is Other -> raw.split('-', '_', ' ')
                .filter { it.isNotBlank() }
                .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }
        }

    companion object {
        fun fromRaw(raw: String): ProductLabel? {
            val normalised = raw.trim().lowercase()
            if (normalised.isEmpty()) return null
            return when (normalised) {
                "going-fast" -> GoingFast
                else -> Other(normalised)
            }
        }
    }
}
