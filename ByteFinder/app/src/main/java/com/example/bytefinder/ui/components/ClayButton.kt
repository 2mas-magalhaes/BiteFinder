package com.example.bytefinder.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.*

/**
 * ClayButton — Botão Claymorphism com Triple Shadow.
 *
 * PERFORMANCE:
 * - Outer shadow: Modifier.shadow() — hardware-accelerated, gratuito
 * - Inset shadows: drawWithCache em claySurface() — zero allocs/frame
 * - Animações: 2 (era 7) — scale + elevation
 *
 * SQUISH EFFECT:
 * - scale: 1.0 → 0.95 com Spring
 * - elevation: 8dp → 2dp (botão "afunda" reduzindo sombra)
 */
@Composable
fun ClayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = ClayBlue,
    contentColor: Color = Color.White,
    isSecondary: Boolean = false,
    cornerRadius: Dp = 24.dp
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val fillColor = if (isSecondary) ClayBlueLight else containerColor
    val txtColor = if (isSecondary) ClayTextDark else contentColor
    val shape = RoundedCornerShape(cornerRadius)

    // ── 2 animações (era 7) ─────────────────────────────────────
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 400f),
        label = "btn-scale"
    )
    val elevation by animateDpAsState(
        targetValue = if (pressed) 2.dp else 8.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "btn-elev"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            // graphicsLayer → hardware layer no GPU; scale sem redraw
            .graphicsLayer { scaleX = scale; scaleY = scale }
            // 1. OUTER SHADOW — hardware-accelerated, não redraw por frame
            .shadow(
                elevation = if (enabled) elevation else 0.dp,
                shape = shape,
                clip = false,
                ambientColor = Color(0xFF0E223F).copy(alpha = 0.08f),
                spotColor = Color(0xFF0E223F).copy(alpha = 0.12f)
            )
            // 2 + 3. INSET SHADOWS — drawWithCache, zero allocs/frame
            .claySurface(
                cornerRadius = cornerRadius,
                fillColor = if (enabled) fillColor else fillColor.copy(alpha = 0.4f)
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (enabled) txtColor else txtColor.copy(alpha = 0.5f)
        )
    }
}
