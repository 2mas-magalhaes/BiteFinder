package com.example.bytefinder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
 * ClayRatingSelector — Seletor de estrelas douradas com animação spring bounce.
 * Cada estrela tem animação de scale ao ser selecionada.
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
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.2f else 1f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
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
