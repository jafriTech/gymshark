package com.jafritech.gymshark.presentation.screen.productDetail


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jafritech.gymshark.R
import com.jafritech.gymshark.domain.model.Product
import com.jafritech.gymshark.domain.model.ProductLabel
import com.jafritech.gymshark.domain.model.ProductSize
import com.jafritech.gymshark.presentation.common.PriceFormatter
import com.jafritech.gymshark.presentation.common.messageRes
import com.jafritech.gymshark.presentation.composable.ErrorState
import com.jafritech.gymshark.presentation.composable.PRODUCT_IMAGE_RATIO
import com.jafritech.gymshark.presentation.composable.ProductImage
import com.jafritech.gymshark.presentation.composable.ProductLabels
import com.jafritech.gymshark.presentation.ui.theme.GymSharkTheme

/** Stateful entry point: wires the ViewModel. */
@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProductDetailContent(
        state = state,
        onBack = onBack,
        onRetry = viewModel::retry,
    )
}

/** Stateless: driven purely by state, so it's previewable and UI-testable. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    state: ProductDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (state) {
                ProductDetailUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )

                is ProductDetailUiState.Error -> ErrorState(
                    message = stringResource(state.error.messageRes()),
                    onRetry = onRetry,
                )

                is ProductDetailUiState.Content -> ProductDetailBody(product = state.product)
            }
        }
    }
}

@Composable
private fun ProductDetailBody(product: Product) {
    val price = PriceFormatter.format(product.price) ?: stringResource(R.string.price_unavailable)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ImageGallery(
            images = product.imageUrls.ifEmpty { listOfNotNull(product.imageUrl) },
            title = product.title,
            labels = product.labels,
            inStock = product.inStock,
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                product.type?.let { type ->
                    Text(
                        text = type.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                product.colour?.let { colour ->
                    Text(
                        text = colour,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = price,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if (product.sizes.isNotEmpty()) {
                SizeSection(sizes = product.sizes)
            }

            if (product.descriptionHtml.isNotBlank()) {
                DescriptionSection(html = product.descriptionHtml)
            }
        }
    }
}

@Composable
private fun ImageGallery(
    images: List<String>,
    title: String,
    labels: List<ProductLabel>,
    inStock: Boolean,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(PRODUCT_IMAGE_RATIO),
    ) {
        if (images.isEmpty()) {
            ProductImage(
                url = null,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            val pagerState = rememberPagerState(pageCount = { images.size })

            HorizontalPager(
                state = pagerState,
                key = { images[it] },
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                ProductImage(
                    url = images[page],
                    contentDescription = stringResource(R.string.product_image, title, page + 1, images.size),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (images.size > 1) {
                PagerIndicator(
                    count = images.size,
                    current = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                )
            }
        }

        ProductLabels(
            labels = labels,
            inStock = inStock,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
        )
    }
}

@Composable
private fun PagerIndicator(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.surface.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clearAndSetSemantics {}, // image descriptions already announce "image x of y"
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val selected = index == current
            Box(
                Modifier
                    .size(if (selected) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (selected) colors.onSurface else colors.onSurface.copy(alpha = 0.4f)),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SizeSection(sizes: List<ProductSize>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(R.string.sizes))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sizes.forEach { SizeChip(it) }
        }
    }
}

@Composable
private fun SizeChip(size: ProductSize) {
    val colors = MaterialTheme.colorScheme
    val description = if (size.inStock) {
        stringResource(R.string.size_in_stock, size.label)
    } else {
        stringResource(R.string.size_out_of_stock, size.label)
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, if (size.inStock) colors.outline else colors.outlineVariant),
        contentColor = if (size.inStock) colors.onSurface else colors.onSurface.copy(alpha = 0.38f),
        modifier = Modifier.clearAndSetSemantics { contentDescription = description },
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = size.label,
                style = MaterialTheme.typography.labelLarge,
                textDecoration = if (size.inStock) TextDecoration.None else TextDecoration.LineThrough,
            )
        }
    }
}

@Composable
private fun DescriptionSection(html: String) {
    val linkColor = MaterialTheme.colorScheme.primary
    val text = remember(html, linkColor) {
        AnnotatedString.fromHtml(
            htmlString = html,
            linkStyles = TextLinkStyles(
                style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
            ),
        ).trimWhitespace()
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(R.string.description))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.semantics { heading() },
    )
}

private fun AnnotatedString.trimWhitespace(): AnnotatedString {
    val start = text.indexOfFirst { !it.isWhitespace() }
    if (start == -1) return AnnotatedString("")
    val end = text.indexOfLast { !it.isWhitespace() } + 1
    return subSequence(start, end)
}

// --- previews ---

private val previewProduct = Product(
    id = 1,
    title = "Vital Seamless 2.0 Leggings",
    colour = "Tahoe Teal Marl",
    type = "Womens Leggings",
    price = 45.0,
    imageUrl = null,
    imageUrls = emptyList(),
    labels = listOf(ProductLabel.GoingFast),
    sizes = listOf(
        ProductSize("XS", true),
        ProductSize("S", true),
        ProductSize("M", false),
        ProductSize("L", true),
        ProductSize("XL", true),
    ),
    inStock = true,
    descriptionHtml = "<p><strong>DO IT ALL IN VITAL</strong></p><p>Sweat-wicking and seamless. " +
            "Find out more <a href=\"https://example.com\">here</a>.</p><p>- High-waisted fit<br>- 93% Nylon, 7% Elastane</p>",
)

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun ProductDetailContentPreview() {
    GymSharkTheme {
        ProductDetailContent(
            state = ProductDetailUiState.Content(previewProduct),
            onBack = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailErrorPreview() {
    GymSharkTheme {
        ProductDetailContent(
            state = ProductDetailUiState.Error(com.jafritech.gymshark.core.util.DataError.NotFound),
            onBack = {},
            onRetry = {},
        )
    }
}