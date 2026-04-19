package com.example.bytefinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.MockDataProvider
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.ui.components.*
import com.example.bytefinder.ui.theme.*
import java.util.Locale

/**
 * PratoCompareScreen — Ecrã de comparação de restaurantes para o mesmo prato.
 *
 * Mostra:
 * - Header do prato (imagem, nome, categoria)
 * - Estatísticas agregadas
 * - Lista de restaurantes ordenados por rating com preço, avaliações e morada
 */
@Composable
fun ClayPratoCompareScreen(
    repository: DataRepository,
    pratoId: Int,
    selectedCity: String = "Todas",
    onBack: () -> Unit,
    onGoHome: () -> Unit,
    onPratoDetailClick: (Int) -> Unit,
    onRestauranteClick: (Int) -> Unit
) {
    val tipo = remember(pratoId) { repository.getPratoTipo(pratoId) }
    val allPratos = remember(tipo, selectedCity) { repository.getPratosByTipo(tipo, selectedCity) }
    val bestPrato = allPratos.firstOrNull()

    // Stats
    val avgRating = if (allPratos.isNotEmpty()) Math.round(allPratos.map { it.ratingMedio }.average() * 10) / 10.0 else 0.0
    val totalAvaliacoes = allPratos.sumOf { it.totalAvaliacoes }
    val priceMin = allPratos.mapNotNull { it.preco }.minOrNull()
    val priceMax = allPratos.mapNotNull { it.preco }.maxOrNull()
    val restaurantCount = allPratos.map { it.restauranteId }.distinct().size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClayCream)
    ) {
        if (bestPrato == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrandHeader(onGoHome = onGoHome)
                    Spacer(Modifier.weight(1f))
                    ClayButton(text = "← Voltar", onClick = onBack, isSecondary = true)
                }
                Spacer(Modifier.height(32.dp))
                EmptyState(
                    icon = Icons.Filled.Warning,
                    title = "Prato não encontrado",
                    description = "O prato que procuras não existe."
                )
            }
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandHeader(onGoHome = onGoHome)
                Spacer(Modifier.weight(1f))
                ClayButton(text = "← Voltar", onClick = onBack, isSecondary = true)
            }

            Spacer(Modifier.height(16.dp))

            // ─── Card do prato ──────────────────────────────────────────
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = ClayWhite,
                cornerRadius = 24.dp,
                elevation = 8.dp
            ) {
                Column {
                    // Imagem
                    if (!bestPrato.imagemUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = bestPrato.imagemUrl,
                            contentDescription = tipo,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        )
                    }

                    Column(Modifier.padding(20.dp)) {
                        Text(
                            text = tipo,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ClayTextDark
                        )

                        Spacer(Modifier.height(8.dp))

                        // Categoria chip
                        val categoria = bestPrato.categoria
                        if (categoria != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(getCategoryColor(categoria).copy(alpha = 0.25f))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = categoria,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ClayTextDark
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CompareStatBadge(
                                label = "Rating",
                                value = "★ ${String.format(Locale.US, "%.1f", avgRating)}",
                                badgeColor = ClayYellow.copy(alpha = 0.3f),
                                textColor = ClayTextDark
                            )
                            CompareStatBadge(
                                label = "Restaurantes",
                                value = "$restaurantCount",
                                badgeColor = ClayBluePale,
                                textColor = ClayBlue
                            )
                            CompareStatBadge(
                                label = "Avaliações",
                                value = "$totalAvaliacoes",
                                badgeColor = ClayGreen.copy(alpha = 0.2f),
                                textColor = ClayGreenDeep
                            )
                            if (priceMin != null && priceMax != null) {
                                CompareStatBadge(
                                    label = "Preço",
                                    value = if (priceMin == priceMax)
                                        String.format(Locale.US, "%.0f€", priceMin)
                                    else
                                        String.format(Locale.US, "%.0f–%.0f€", priceMin, priceMax),
                                    badgeColor = ClayBlueLight,
                                    textColor = ClayBlueDeep
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Descrição
                        Text(
                            text = bestPrato.descricao ?: "",
                            color = ClayTextMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ─── Lista de restaurantes ──────────────────────────────────
            Text(
                text = "Disponível em $restaurantCount ${if (restaurantCount == 1) "restaurante" else "restaurantes"}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ClayTextDark
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ordenado por rating · Toca para ver avaliações",
                fontSize = 13.sp,
                color = ClayTextLight
            )

            Spacer(Modifier.height(14.dp))

            allPratos.forEachIndexed { index, prato ->
                val rest = MockDataProvider.restaurantes.find { it.id == prato.restauranteId }

                RestaurantCompareCard(
                    prato = prato,
                    restaurante = rest,
                    rank = index + 1,
                    isFirst = index == 0,
                    onClick = { onPratoDetailClick(prato.id) },
                    onRestauranteClick = { onRestauranteClick(prato.restauranteId) }
                )

                if (index < allPratos.lastIndex) {
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Componentes auxiliares ─────────────────────────────────────────────────

@Composable
private fun CompareStatBadge(
    label: String,
    value: String,
    badgeColor: Color,
    textColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(badgeColor)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textColor
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = ClayTextLight)
    }
}

@Composable
private fun RestaurantCompareCard(
    prato: PratoDto,
    restaurante: MockDataProvider.MockRestaurante?,
    rank: Int,
    isFirst: Boolean,
    onClick: () -> Unit,
    onRestauranteClick: () -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundColor = if (isFirst) ClayWhite else ClayWhite,
        cornerRadius = 20.dp,
        elevation = if (isFirst) 8.dp else 5.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Rank badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (rank) {
                                1 -> ClayYellow.copy(alpha = 0.35f)
                                2 -> ClayBluePale
                                3 -> ClayGreen.copy(alpha = 0.15f)
                                else -> ClayBlueLight
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (rank == 1) ClayTextDark else ClayTextMedium
                    )
                }

                Spacer(Modifier.width(14.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prato.restauranteNome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ClayTextDark
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = prato.nome,
                        fontSize = 13.sp,
                        color = ClayTextMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (restaurante != null) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = ClayTextLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${restaurante.morada}, ${restaurante.cidade}",
                                fontSize = 12.sp,
                                color = ClayTextLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Preço
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (prato.preco != null)
                            String.format(Locale.US, "%.2f€", prato.preco)
                        else "-",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = ClayBlue
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Rating bar + reviews + link
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ClayYellow.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "★ ${String.format(Locale.US, "%.1f", prato.ratingMedio)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ClayTextDark
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${prato.totalAvaliacoes} avaliações",
                    fontSize = 12.sp,
                    color = ClayTextLight
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Ver restaurante →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClayBlue,
                    modifier = Modifier.clickable { onRestauranteClick() }
                )
            }
        }
    }
}
