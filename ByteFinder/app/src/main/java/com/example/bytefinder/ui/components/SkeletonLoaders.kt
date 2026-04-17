package com.example.bytefinder.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bytefinder.ui.theme.*

/**
 * SkeletonBlock — Bloco individual de skeleton loading com shimmer Claymorphism.
 * Usa gradiente animado sobre tons pastel quentes.
 */
@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp
) {
    val shimmer = rememberInfiniteTransition(label = "skeleton")
    val shift by shimmer.animateFloat(
        initialValue = -400f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton-shift"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            ClayBeige.copy(alpha = 0.6f),
            ClayCream.copy(alpha = 0.95f),
            ClayBeige.copy(alpha = 0.6f)
        ),
        start = Offset(shift, 0f),
        end = Offset(shift + 250f, 250f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

/**
 * DishSkeletonCard — Skeleton com formato de card de prato.
 */
@Composable
fun DishSkeletonCard(modifier: Modifier = Modifier) {
    ClayCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ClayWhite,
        cornerRadius = 22.dp,
        elevation = 4.dp
    ) {
        Column(Modifier.padding(14.dp)) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                cornerRadius = 14.dp
            )
            Spacer(Modifier.height(12.dp))
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp),
                cornerRadius = 8.dp
            )
            Spacer(Modifier.height(8.dp))
            Row {
                SkeletonBlock(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp),
                    cornerRadius = 6.dp
                )
                Spacer(Modifier.weight(1f))
                SkeletonBlock(
                    modifier = Modifier
                        .width(50.dp)
                        .height(12.dp),
                    cornerRadius = 6.dp
                )
            }
        }
    }
}

/**
 * SkeletonList — Lista de skeleton cards para loading states.
 */
@Composable
fun SkeletonList(count: Int = 3) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(count) {
            DishSkeletonCard()
        }
    }
}

/**
 * SkeletonRow — Linha horizontal de skeleton cards (para carrosséis).
 */
@Composable
fun SkeletonRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        repeat(2) {
            DishSkeletonCard(modifier = Modifier.width(200.dp))
        }
    }
}
