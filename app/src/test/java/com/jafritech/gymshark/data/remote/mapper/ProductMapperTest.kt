package com.jafritech.gymshark.data.remote.mapper

import com.jafritech.gymshark.core.network.ApiJson
import com.jafritech.gymshark.data.mapper.sanitiseHtml
import com.jafritech.gymshark.data.mapper.toDomain
import com.jafritech.gymshark.data.mapper.toDomainOrNull
import com.jafritech.gymshark.data.remote.dto.MediaDto
import com.jafritech.gymshark.data.remote.dto.ProductDto
import com.jafritech.gymshark.data.remote.dto.ProductsResponseDto
import com.jafritech.gymshark.data.remote.dto.SizeDto
import com.jafritech.gymshark.domain.model.ProductLabel
import org.junit.Test
import org.junit.Assert.*


class ProductMapperTest {

    private fun dto(
        id: Long? = 1L,
        title: String? = "Speed Leggings",
        featured: String? = "https://cdn.test/a.jpg",
        media: List<String?> = listOf("https://cdn.test/a.jpg", "https://cdn.test/b.jpg"),
        labels: List<String>? = null,
    ) = ProductDto(
        id = id,
        title = title,
        colour = "Navy",
        price = 1000.0,
        featuredMedia = featured?.let { MediaDto(src = it) },
        media = media.mapIndexed { i, src -> MediaDto(src = src, position = i + 1) },
        labels = labels,
    )

    // --- decoding ---

    @Test
    fun `decodes payload with nulls and unknown keys`() {
        val json = """
            {"hits":[{"id":1,"title":"T","labels":null,"fit":null,"price":1000,
              "featuredMedia":null,"extra":"ignored"}]}
        """.trimIndent()

        val result = ApiJson.decodeFromString<ProductsResponseDto>(json)

        assertEquals(1, result.hits?.size)
        assertNull(result.hits!!.first().labels)
        assertEquals(1000.0, result.hits!!.first().price)
    }

    @Test
    fun `missing hits maps to empty list`() {
        assertTrue(ProductsResponseDto(hits = null).toDomain().isEmpty())
    }

    // --- required fields ---

    @Test
    fun `product without id is dropped`() {
        assertNull(dto(id = null).toDomainOrNull())
    }

    @Test
    fun `product with blank title is dropped`() {
        assertNull(dto(title = "  ").toDomainOrNull())
    }

    // --- images ---

    @Test
    fun `uses featured image when valid`() {
        assertEquals("https://cdn.test/a.jpg", dto().toDomainOrNull()!!.imageUrl)
    }

    @Test
    fun `falls back to first gallery image when featured is missing`() {
        val product = dto(featured = null, media = listOf("https://cdn.test/b.jpg")).toDomainOrNull()!!
        assertEquals("https://cdn.test/b.jpg", product.imageUrl)
    }

    @Test
    fun `falls back to gallery when featured url is malformed`() {
        val product = dto(featured = "not a url", media = listOf("https://cdn.test/b.jpg")).toDomainOrNull()!!
        assertEquals("https://cdn.test/b.jpg", product.imageUrl)
    }

    @Test
    fun `image is null when no valid urls exist`() {
        val product = dto(featured = "", media = listOf(null, "", "ftp://x")).toDomainOrNull()!!
        assertNull(product.imageUrl)
        assertTrue(product.imageUrls.isEmpty())
    }

    @Test
    fun `http urls are upgraded to https`() {
        val product = dto(featured = "http://cdn.test/a.jpg").toDomainOrNull()!!
        assertEquals("https://cdn.test/a.jpg", product.imageUrl)
    }

    @Test
    fun `gallery is ordered by position and deduplicated`() {
        val raw = dto().copy(
            media = listOf(
                MediaDto(src = "https://cdn.test/c.jpg", position = 3),
                MediaDto(src = "https://cdn.test/a.jpg", position = 1),
                MediaDto(src = "https://cdn.test/a.jpg", position = 2),
            ),
        )
        assertEquals(
            listOf("https://cdn.test/a.jpg", "https://cdn.test/c.jpg"),
            raw.toDomainOrNull()!!.imageUrls,
        )
    }

    // --- labels ---

    @Test
    fun `null labels map to empty list`() {
        assertTrue(dto(labels = null).toDomainOrNull()!!.labels.isEmpty())
    }

    @Test
    fun `going-fast maps to GoingFast`() {
        assertEquals(listOf(ProductLabel.GoingFast), dto(labels = listOf("going-fast")).toDomainOrNull()!!.labels)
    }

    @Test
    fun `unknown label is kept with readable name`() {
        val label = dto(labels = listOf("limited-edition")).toDomainOrNull()!!.labels.single()
        assertEquals("Limited Edition", label.displayName)
    }

    @Test
    fun `blank and duplicate labels are removed`() {
        val labels = dto(labels = listOf("", "going-fast", "GOING-FAST ")).toDomainOrNull()!!.labels
        assertEquals(listOf(ProductLabel.GoingFast), labels)
    }

    // --- sizes / stock ---

    @Test
    fun `sizes are uppercased and keep stock state`() {
        val product = dto().copy(
            availableSizes = listOf(SizeDto(size = "xs", inStock = true), SizeDto(size = "s", inStock = false)),
        ).toDomainOrNull()!!

        assertEquals(listOf("XS", "S"), product.sizes.map { it.label })
        assertTrue(product.sizes[0].inStock)
        assertFalse(product.sizes[1].inStock)
    }

    // --- description ---

    @Test
    fun `strips meta tags and translator junk from html`() {
        val html = "<meta charset=\"utf-8\">\n<p>Hi</p>\n<div id=\"gtx-trans\" style=\"x\">\n<div class=\"gtx-trans-icon\"></div>\n</div>"
        assertEquals("<p>Hi</p>", sanitiseHtml(html))
    }

    // --- html ---
    @Test
    fun `removes empty paragraphs`() {
        assertEquals(
            "<p>A</p>\n<p>B</p>",
            sanitiseHtml("<p>A</p>\n<p> </p>\n<p>&nbsp;</p>\n<p>B</p>"),
        )
    }

    @Test
    fun `strips leading and trailing line breaks inside paragraphs`() {
        assertEquals(
            "<p>Text</p>",
            sanitiseHtml("<p><br data-mce-fragment=\"1\">Text<br data-mce-fragment=\"1\"></p>"),
        )
    }
}
