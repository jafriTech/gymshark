package com.jafritech.gymshark.presentation.composable


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jafritech.gymshark.R
import com.jafritech.gymshark.domain.model.Product
import com.jafritech.gymshark.presentation.common.PriceFormatter


@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val price = PriceFormatter.format(product.price) ?: stringResource(R.string.price_unavailable)

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClickLabel = stringResource(R.string.view_details), onClick = onClick),
    ) {
        Box {
            ProductImage(
                url = product.imageUrl,
                contentDescription = null, // title below describes it; clickable merges semantics
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(PRODUCT_IMAGE_RATIO)
                    .clip(MaterialTheme.shapes.medium),
            )
            ProductLabels(
                labels = product.labels,
                inStock = product.inStock,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = product.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        product.colour?.let { colour ->
            Text(
                text = colour,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = price,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}