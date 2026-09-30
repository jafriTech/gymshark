package com.jafritech.gymshark.presentation.composable

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun PlaceholderBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "placeholder")
    val pulse by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 800), RepeatMode.Reverse),
        label = "pulse",
    )
    Box(
        modifier
            .graphicsLayer { alpha = pulse }
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}