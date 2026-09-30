package com.jafritech.gymshark.data.mapper

import com.jafritech.gymshark.data.remote.dto.MediaDto
import com.jafritech.gymshark.data.remote.dto.ProductDto
import com.jafritech.gymshark.data.remote.dto.ProductsResponseDto
import com.jafritech.gymshark.domain.model.Product
import com.jafritech.gymshark.domain.model.ProductLabel
import com.jafritech.gymshark.domain.model.ProductSize

/**
 * DTO -> domain. All defensive handling of bad/missing API data lives here,
 * so it stays pure Kotlin and fully unit-testable without Android.
 */
fun ProductsResponseDto.toDomain(): List<Product> =
    hits.orEmpty().mapNotNull { it.toDomainOrNull() }

/** Returns null for products that can't be shown (no id or no title). */
fun ProductDto.toDomainOrNull(): Product? {
    val safeId = id ?: return null
    val safeTitle = title?.trim().takeUnless { it.isNullOrEmpty() } ?: return null

    val gallery = media.orEmpty()
        .sortedBy { it.position ?: Int.MAX_VALUE }
        .mapNotNull { it.validUrlOrNull() }
        .distinct()

    return Product(
        id = safeId,
        title = safeTitle,
        colour = colour?.trim()?.takeIf { it.isNotEmpty() },
        type = type?.trim()?.takeIf { it.isNotEmpty() },
        price = price?.takeIf { it >= 0 },
        imageUrl = featuredMedia?.validUrlOrNull() ?: gallery.firstOrNull(),
        imageUrls = gallery,
        labels = labels.orEmpty().mapNotNull { ProductLabel.fromRaw(it) }.distinct(),
        sizes = availableSizes.orEmpty().mapNotNull { size ->
            val label = size.size?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            ProductSize(label = label.uppercase(), inStock = size.inStock == true)
        },
        inStock = inStock ?: availableSizes.orEmpty().any { it.inStock == true },
        descriptionHtml = description?.let(::sanitiseHtml).orEmpty(),
    )
}

/** Blank, non-http(s) or malformed URLs become null; http is upgraded to https. */
internal fun MediaDto.validUrlOrNull(): String? {
    val raw = src?.trim().orEmpty()
    if (raw.isEmpty() || raw.any { it.isWhitespace() }) return null
    val url = when {
        raw.startsWith("https://", ignoreCase = true) -> raw
        raw.startsWith("http://", ignoreCase = true) -> "https://" + raw.substring(7)
        raw.startsWith("//") -> "https:$raw"
        else -> return null
    }
    return url.takeIf { it.length > "https://".length }
}

private val META_TAG = Regex("<meta[^>]*>", RegexOption.IGNORE_CASE)
private val GTX_BLOCK = Regex(
    "<div[^>]*gtx-trans[^>]*>.*?</div>\\s*</div>",
    setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
)
private val ZERO_WIDTH = Regex("[\\u200B\\u200C\\u200D\\uFEFF]")

private val LEADING_BREAKS = Regex("(<p[^>]*>)(?:\\s|\\u00A0|<br[^>]*>)+", RegexOption.IGNORE_CASE)
private val TRAILING_BREAKS = Regex("(?:\\s|\\u00A0|<br[^>]*>)+(</p>)", RegexOption.IGNORE_CASE)
private val EMPTY_PARAGRAPH = Regex(
    "<p[^>]*>(?:\\s|&nbsp;|\\u00A0|<br[^>]*>)*</p>",
    RegexOption.IGNORE_CASE,
)
private val REPEATED_NEWLINES = Regex("\\n{2,}")

/** Strips junk the CMS leaves in descriptions; keeps real formatting tags. */
internal fun sanitiseHtml(html: String): String =
    html
        .replace(META_TAG, "")
        .replace(GTX_BLOCK, "")
        .replace(ZERO_WIDTH, "")
        .replace(LEADING_BREAKS, "$1")
        .replace(TRAILING_BREAKS, "$1")
        .replace(EMPTY_PARAGRAPH, "")
        .replace(REPEATED_NEWLINES, "\n")
        .trim()
