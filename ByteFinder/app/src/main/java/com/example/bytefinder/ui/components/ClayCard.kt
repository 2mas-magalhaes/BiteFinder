package com.example.bytefinder.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.data.displayImageUrl
import com.example.bytefinder.ui.theme.*
import java.util.Locale

/**
 * ClayCard — Componente base Claymorphism com Triple Shadow.
 *
 * PERFORMANCE:
 * - Outer shadow: Modifier.shadow() — hardware-accelerated, gratuito
 * - Inset shadows: drawWithCache em claySurface() — zero allocs/frame
 * - Animações: 2 (era 6) — scale + elevation
 *
 * PRESS EFFECT:
 * - scale: 1.0 -> 0.985 with a short premium easing
 * - elevation: baseElevation -> 4dp while pressed
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = ClayBlueLight,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(cornerRadius)

    val biteEase = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(durationMillis = 160, easing = biteEase),
        label = "card-scale"
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (pressed) 4.dp else elevation,
        animationSpec = tween(durationMillis = 180, easing = biteEase),
        label = "card-elev"
    )

    Box(
        modifier = modifier
            // graphicsLayer → hardware layer; scale aplicado na GPU
            .graphicsLayer { scaleX = scale; scaleY = scale }
            // 1. OUTER SHADOW — hardware-accelerated, não redraw por frame
            .shadow(
                elevation = animatedElevation,
                shape = shape,
                clip = false,
                ambientColor = Color(0xFF0E223F).copy(alpha = 0.08f),
                spotColor = Color(0xFF0E223F).copy(alpha = 0.14f)
            )
            // 2 + 3. INSET SHADOWS — drawWithCache, zero allocs/frame
            .claySurface(
                cornerRadius = cornerRadius,
                fillColor = backgroundColor
            )
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onClick
                ) else Modifier
            )
    ) {
        content()
    }
}

// ─── CLAY DISH CARD (Reutilizado em HomeScreen, Search, etc.) ─────────────
@Composable
fun ClayDishCard(
    prato: PratoDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ClayCard(
        modifier = modifier,
        onClick = onClick,
        backgroundColor = ClayBlueLight,
        cornerRadius = 24.dp,
        elevation = 8.dp
    ) {
        Column {
            // Imagem do prato
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val imageUrl = prato.displayImageUrl()
                if (!imageUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = prato.nome,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                        loading = { DishImageFallback(prato.nome) },
                        error = { DishImageFallback(prato.nome) }
                    )
                } else {
                    DishImageFallback(prato.nome)
                }

                // Badge de rating
                if (prato.ratingMedio > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ClayYellow.copy(alpha = 0.95f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("★", fontSize = 12.sp, color = ClayTextDark)
                            Spacer(Modifier.width(2.dp))
                            Text(
                                String.format(Locale.US, "%.1f", prato.ratingMedio),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayTextDark
                            )
                        }
                    }
                }
            }

            // Informações do prato
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = prato.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ClayTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = prato.restauranteNome,
                    color = ClayTextMedium,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (prato.preco != null) String.format(Locale.US, "%.2f €", prato.preco) else "-",
                        fontWeight = FontWeight.ExtraBold,
                        color = ClayBlue,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${prato.totalAvaliacoes} aval.",
                        fontSize = 11.sp,
                        color = ClayTextLight
                    )
                }
            }
        }
    }
}

@Composable
private fun DishImageFallback(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClayBluePale.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Restaurant,
                contentDescription = null,
                tint = ClayBlue,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = label.take(18),
                color = ClayTextMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
