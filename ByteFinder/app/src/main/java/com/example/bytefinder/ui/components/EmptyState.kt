package com.example.bytefinder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.*

/**
 * EmptyState — Composable para estados vazios com animação de entrada.
 *
 * Mostra um emoji/ícone grande, um título e uma descrição amigável.
 * Aparece com animação spring bounce para criar impacto visual.
 */
@Composable
fun EmptyState(
    emoji: String = "🍽",
    title: String = "Nada encontrado",
    description: String = "Tenta ajustar os filtros ou pesquisar algo diferente.",
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
        label = "empty-scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "empty-alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji grande como "ilustração"
        Text(
            text = emoji,
            fontSize = 64.sp,
            modifier = Modifier.size(80.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = ClayTextDark,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = ClayTextMedium,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

// ─── Empty States pré-definidos para cada contexto ──────────────────────────

@Composable
fun EmptySearchState(query: String) {
    EmptyState(
        emoji = "🔍",
        title = "Sem resultados para \"$query\"",
        description = "Experimenta pesquisar por outro prato ou restaurante."
    )
}

@Composable
fun EmptyCategoryState(category: String) {
    EmptyState(
        emoji = "😔",
        title = "Ops, não encontrámos $category nesta zona!",
        description = "Experimenta mudar a localização ou escolher outra categoria."
    )
}

@Composable
fun EmptyReviewsState() {
    EmptyState(
        emoji = "✍️",
        title = "Ainda sem avaliações",
        description = "Sê o primeiro a avaliar este prato e ajuda outros foodies!"
    )
}

@Composable
fun EmptyMyReviewsState() {
    EmptyState(
        emoji = "📝",
        title = "Ainda não avaliaste nenhum prato",
        description = "Explora os pratos e partilha a tua opinião!"
    )
}

@Composable
fun EmptyBusinessState() {
    EmptyState(
        emoji = "🏪",
        title = "Sem pratos neste restaurante",
        description = "Adiciona pratos para começar a receber avaliações."
    )
}

@Composable
fun EmptyFilterState() {
    EmptyState(
        emoji = "🎯",
        title = "Nenhum prato corresponde aos filtros",
        description = "Tenta alargar a pesquisa removendo alguns filtros."
    )
}
