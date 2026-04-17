package com.example.bytefinder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.*

/**
 * ClayButton — Botão flat pill matte.
 *
 * Sem inset shadows — cor sólida saturada sobre fundo escuro
 * já cria o contraste visual do Claymorphism.
 * Squish via scale Spring (1 animação, hardware layer).
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

    val fillColor = when {
        !enabled        -> (if (isSecondary) ClayBlueLight else containerColor).copy(alpha = 0.4f)
        isSecondary     -> Color.White.copy(alpha = 0.12f)   // ghost pill sobre fundo escuro
        else            -> containerColor
    }
    val txtColor = when {
        !enabled    -> contentColor.copy(alpha = 0.4f)
        isSecondary -> Color.White
        else        -> contentColor
    }
    val shape = RoundedCornerShape(cornerRadius)

    // 1 animação: scale com spring bounce
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.42f, stiffness = 420f),
        label = "btn-scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(fillColor)
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
            color = txtColor
        )
    }
}
