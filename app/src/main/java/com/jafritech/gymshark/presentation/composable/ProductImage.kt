package com.jafritech.gymshark.presentation.composable


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jafritech.gymshark.R


/** Product photos are 1692 x 2018. */
const val PRODUCT_IMAGE_RATIO = 1692f / 2018f

/**
 * Handles all three image cases:
 * - null/invalid url  -> fallback icon immediately (mapper already rejected bad urls)
 * - loading           -> pulsing placeholder
 * - load failure/404  -> fallback icon
 */
@Composable
fun ProductImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (url == null) {
            ImageFallback()
        } else {
            var state by remember(url) {
                mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                onState = { state = it },
                modifier = Modifier.matchParentSize(),
            )

            when (state) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Loading -> PlaceholderBox(Modifier.matchParentSize())
                is AsyncImagePainter.State.Error -> ImageFallback()
                is AsyncImagePainter.State.Success -> Unit
            }
        }
    }
}

@Composable
private fun ImageFallback() {
    Icon(
        imageVector = Icons.Outlined.ImageNotSupported,
        contentDescription = stringResource(R.string.image_unavailable),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(40.dp),
    )
}