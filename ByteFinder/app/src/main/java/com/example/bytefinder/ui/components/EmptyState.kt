package com.example.bytefinder.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.*

@Composable
fun EmptyState(
    icon: ImageVector = Icons.Filled.Info,
    title: String = "Nada encontrado",
    description: String = "Tenta ajustar os filtros ou pesquisar algo diferente.",
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val biteEase = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.96f,
        animationSpec = tween(durationMillis = 220, easing = biteEase),
        label = "empty-scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = biteEase),
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ClayBlue.copy(alpha = 0.86f),
            modifier = Modifier.size(64.dp)
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
        icon = Icons.Filled.Search,
        title = "Sem resultados para \"$query\"",
        description = "Experimenta pesquisar por outro prato ou restaurante."
    )
}

@Composable
fun EmptyCategoryState(category: String) {
    EmptyState(
        icon = Icons.Filled.SentimentDissatisfied,
        title = "Ops, não encontrámos $category nesta zona!",
        description = "Experimenta mudar a localização ou escolher outra categoria."
    )
}

@Composable
fun EmptyReviewsState() {
    EmptyState(
        icon = Icons.Filled.RateReview,
        title = "Ainda sem avaliações",
        description = "Sê o primeiro a avaliar este prato e ajuda outros foodies!"
    )
}

@Composable
fun EmptyMyReviewsState() {
    EmptyState(
        icon = Icons.Filled.RateReview,
        title = "Ainda não avaliaste nenhum prato",
        description = "Explora os pratos e partilha a tua opinião!"
    )
}

@Composable
fun EmptyBusinessState() {
    EmptyState(
        icon = Icons.Filled.Storefront,
        title = "Sem pratos neste restaurante",
        description = "Adiciona pratos para começar a receber avaliações."
    )
}

@Composable
fun EmptyFilterState() {
    EmptyState(
        icon = Icons.Filled.FilterAlt,
        title = "Nenhum prato corresponde aos filtros",
        description = "Tenta alargar a pesquisa removendo alguns filtros."
    )
}
