package com.example.bytefinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.MyReviewItemDto
import com.example.bytefinder.ui.components.*
import com.example.bytefinder.ui.theme.*

/**
 * MyReviewsScreen — Ecrã com histórico de avaliações do utilizador.
 * Estilo Claymorphism com cards de review e empty state bonito.
 */
@Composable
fun ClayMyReviewsScreen(
    repository: DataRepository,
    userId: Int,
    onBack: () -> Unit,
    onPratoClick: (Int) -> Unit,
    onGoHome: () -> Unit
) {
    var items by remember { mutableStateOf(listOf<MyReviewItemDto>()) }
    var erro by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        loading = true
        try {
            items = repository.listMyAvaliacoes(userId)
            erro = null
        } catch (e: Exception) {
            erro = e.message ?: "Erro de rede"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ClayCream)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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

        Text(
            text = "As minhas avaliações ⭐",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = ClayTextDark
        )

        Spacer(Modifier.height(14.dp))

        if (loading) {
            SkeletonList(count = 3)
            return@Column
        }

        if (erro != null) {
            EmptyState(
                emoji = "😕",
                title = "Algo correu mal",
                description = erro ?: "Erro desconhecido"
            )
            return@Column
        }

        if (items.isEmpty()) {
            EmptyMyReviewsState()
            return@Column
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items.forEach { item ->
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onPratoClick(item.pratoId) },
                    backgroundColor = ClayWhite,
                    cornerRadius = 22.dp,
                    elevation = 6.dp
                ) {
                    Column {
                        if (!item.pratoImagemUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.pratoImagemUrl,
                                contentDescription = item.pratoNome,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                            )
                        }

                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = item.pratoNome,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ClayTextDark
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = item.restauranteNome,
                                color = ClayTextMedium,
                                fontSize = 14.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "★".repeat(item.classificacao.coerceIn(0, 5)),
                                color = ClayYellow,
                                fontSize = 18.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = item.comentario ?: "",
                                color = ClayTextMedium,
                                lineHeight = 20.sp
                            )

                            if (!item.createdAt.isNullOrBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = item.createdAt,
                                    color = ClayTextLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
