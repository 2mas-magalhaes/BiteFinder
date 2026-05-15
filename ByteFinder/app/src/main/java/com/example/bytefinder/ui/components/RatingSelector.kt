package com.example.bytefinder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.*

/**
 * ClayRatingSelector - restrained premium star feedback.
 */
@Composable
fun ClayRatingSelector(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        (1..5).forEach { star ->
            val selected = star <= rating
            val biteEase = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.08f else 1f,
                animationSpec = tween(durationMillis = 160, easing = biteEase),
                label = "star-$star"
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clickable { onRatingSelected(star) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "★",
                    fontSize = 32.sp,
                    color = if (selected) ClayYellow else ClayBeigeDeep
                )
            }
        }
    }
}
