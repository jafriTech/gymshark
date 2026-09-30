package com.jafritech.gymshark.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jafritech.gymshark.R
import com.jafritech.gymshark.domain.model.ProductLabel

@Composable
fun LabelChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier,
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun ProductLabelChip(label: ProductLabel, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (label) {
        ProductLabel.GoingFast -> colors.errorContainer to colors.onErrorContainer
        is ProductLabel.Other -> colors.secondaryContainer to colors.onSecondaryContainer
    }
    LabelChip(label.displayName, container, content, modifier)
}

@Composable
fun ProductLabels(
    labels: List<ProductLabel>,
    inStock: Boolean,
    modifier: Modifier = Modifier,
) {
    if (labels.isEmpty() && inStock) return
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (!inStock) {
            LabelChip(
                text = stringResource(R.string.out_of_stock),
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
        labels.forEach { ProductLabelChip(it) }
    }
}