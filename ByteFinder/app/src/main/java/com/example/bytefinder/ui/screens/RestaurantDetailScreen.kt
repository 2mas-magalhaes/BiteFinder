package com.example.bytefinder.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.MockDataProvider
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.ui.components.*
import com.example.bytefinder.ui.theme.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

/**
 * RestaurantDetailScreen — Página de detalhe do restaurante.
 *
 * Mostra:
 * - Nome, morada, cidade/zona
 * - Rating médio agregado de todos os pratos
 * - Mapa embebido com localização
 * - Lista de pratos do restaurante (com cards clicáveis)
 * - Estatísticas (total pratos, total avaliações, preço médio)
 */
@Composable
fun ClayRestaurantDetailScreen(
    repository: DataRepository,
    restauranteId: Int,
    onBack: () -> Unit,
    onGoHome: () -> Unit,
    onPratoClick: (Int) -> Unit
) {
    val restaurante = remember(restauranteId) {
        MockDataProvider.restaurantes.find { it.id == restauranteId }
    }

    var pratos by remember { mutableStateOf<List<PratoDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LaunchedEffect(restauranteId) {
        isLoading = true
        pratos = repository.listPratos(restauranteId = restauranteId, limit = 50)
        isLoading = false
    }

    // Estatísticas agregadas
    val avgRating = if (pratos.isNotEmpty()) pratos.map { it.ratingMedio }.average() else 0.0
    val totalAvaliacoes = pratos.sumOf { it.totalAvaliacoes }
    val avgPreco = pratos.mapNotNull { it.preco }.let { prices ->
        if (prices.isNotEmpty()) prices.average() else null
    }
    val categorias = pratos.mapNotNull { it.categoria }.distinct()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkeletonBlock(modifier = Modifier.fillMaxWidth().height(32.dp), cornerRadius = 12.dp)
                SkeletonBlock(modifier = Modifier.fillMaxWidth().height(200.dp), cornerRadius = 20.dp)
                SkeletonBlock(modifier = Modifier.fillMaxWidth().height(140.dp), cornerRadius = 20.dp)
                SkeletonBlock(modifier = Modifier.fillMaxWidth().height(200.dp), cornerRadius = 20.dp)
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
                ClayButton(
                    text = "← Voltar",
                    onClick = onBack,
                    isSecondary = true
                )
            }

            Spacer(Modifier.height(16.dp))

            if (restaurante == null) {
                EmptyState(
                    icon = Icons.Filled.Warning,
                    title = "Restaurante não encontrado",
                    description = "O restaurante que procuras não existe."
                )
                return@Column
            }

            // ─── Card principal do restaurante ──────────────────────────
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = ClayWhite,
                cornerRadius = 24.dp,
                elevation = 8.dp
            ) {
                Column(Modifier.padding(20.dp)) {
                    // Nome
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Restaurant,
                            contentDescription = null,
                            tint = ClayBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = restaurante.nome,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ClayTextDark
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Morada
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = ClayTextMedium,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${restaurante.morada}, ${restaurante.cidade}",
                            color = ClayTextMedium,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    // Zona
                    Text(
                        text = "Zona: ${restaurante.zona}",
                        color = ClayTextLight,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 24.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    // ─── Stats row ──────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge(
                            label = "Rating",
                            value = String.format(Locale.US, "%.1f", avgRating),
                            icon = "★",
                            badgeColor = ClayYellow.copy(alpha = 0.3f),
                            textColor = ClayTextDark
                        )
                        StatBadge(
                            label = "Pratos",
                            value = "${pratos.size}",
                            icon = "🍽",
                            badgeColor = ClayBluePale,
                            textColor = ClayBlue
                        )
                        StatBadge(
                            label = "Avaliações",
                            value = "$totalAvaliacoes",
                            icon = "💬",
                            badgeColor = ClayGreen.copy(alpha = 0.2f),
                            textColor = ClayGreenDeep
                        )
                        if (avgPreco != null) {
                            StatBadge(
                                label = "Preço médio",
                                value = String.format(Locale.US, "%.0f€", avgPreco),
                                icon = "€",
                                badgeColor = ClayBlueLight,
                                textColor = ClayBlueDeep
                            )
                        }
                    }

                    // Categorias do restaurante
                    if (categorias.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categorias.forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(getCategoryColor(cat).copy(alpha = 0.25f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ClayTextDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Mapa ───────────────────────────────────────────────────
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = ClayWhite,
                cornerRadius = 24.dp,
                elevation = 8.dp
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Localização",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayTextDark
                    )
                    Spacer(Modifier.height(12.dp))

                    val position = LatLng(restaurante.latitude, restaurante.longitude)
                    val cameraPositionState = rememberCameraPositionState {
                        this.position = CameraPosition.fromLatLngZoom(position, 16f)
                    }
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            scrollGesturesEnabled = false,
                            zoomGesturesEnabled = false,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        properties = MapProperties()
                    ) {
                        Marker(state = MarkerState(position = position))
                    }

                    Spacer(Modifier.height(12.dp))

                    ClayButton(
                        text = "Abrir no Maps",
                        onClick = {
                            val label = Uri.encode(restaurante.nome)
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("geo:${restaurante.latitude},${restaurante.longitude}?q=${restaurante.latitude},${restaurante.longitude}($label)")
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ─── Pratos do restaurante ──────────────────────────────────
            Text(
                "Ementa",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ClayOnDark,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${pratos.size} pratos disponíveis",
                fontSize = 13.sp,
                color = ClayOnDarkSecond,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(Modifier.height(14.dp))

            if (pratos.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Restaurant,
                    title = "Sem pratos",
                    description = "Este restaurante ainda não tem pratos registados."
                )
            } else {
                // Group by category
                val groupedByCategory = pratos.groupBy { it.categoria ?: "Outros" }

                groupedByCategory.forEach { (categoria, pratosCategoria) ->
                    // Category header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(getCategoryColor(categoria).copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = categoria,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = ClayTextDark
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${pratosCategoria.size} ${if (pratosCategoria.size == 1) "prato" else "pratos"}",
                            fontSize = 12.sp,
                            color = ClayOnDarkSecond
                        )
                    }

                    // Pratos list
                    pratosCategoria.forEach { prato ->
                        PratoListItem(
                            prato = prato,
                            onClick = { onPratoClick(prato.id) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Componentes auxiliares ─────────────────────────────────────────────────

@Composable
private fun StatBadge(
    label: String,
    value: String,
    icon: String,
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
                text = "$icon $value",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = textColor
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = ClayOnDarkSecond
        )
    }
}

@Composable
private fun PratoListItem(
    prato: PratoDto,
    onClick: () -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundColor = ClayWhite,
        cornerRadius = 18.dp,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem pequena
            if (!prato.imagemUrl.isNullOrBlank()) {
                coil.compose.AsyncImage(
                    model = prato.imagemUrl,
                    contentDescription = prato.nome,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ClayBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Restaurant,
                        contentDescription = null,
                        tint = ClayTextLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prato.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ClayTextDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = prato.descricao ?: "",
                    fontSize = 12.sp,
                    color = ClayTextMedium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rating
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ClayYellow.copy(alpha = 0.3f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "★ ${String.format(Locale.US, "%.1f", prato.ratingMedio)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ClayTextDark
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${prato.totalAvaliacoes} aval.",
                        fontSize = 11.sp,
                        color = ClayTextLight
                    )
                }
            }

            // Preço
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (prato.preco != null) String.format(Locale.US, "%.2f€", prato.preco) else "-",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = ClayBlue
                )
            }
        }
    }
}
