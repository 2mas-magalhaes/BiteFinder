package com.example.bytefinder.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.bytefinder.data.AvaliacaoDto
import com.example.bytefinder.data.CreateAvaliacaoRequest
import com.example.bytefinder.data.CreateRespostaRequest
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.DeleteAvaliacaoRequest
import com.example.bytefinder.data.PratoDetailDto
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.data.UpdatePratoRequest
import com.example.bytefinder.ui.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import com.example.bytefinder.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * PratoDetailScreen — Ecrã de detalhe do prato com estilo Claymorphism.
 *
 * Funcionalidades:
 * - Imagem, preço, rating, descrição
 * - Formulário de avaliação (estrelas + comentário)
 * - Lista de avaliações com respostas
 * - Mapa embebido com localização do restaurante
 * - Edição do prato (se é o dono)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClayPratoDetailScreen(
    repository: DataRepository,
    pratoId: Int,
    currentUserId: Int,
    restaurantIds: List<Int>,
    onBack: () -> Unit,
    onGoHome: () -> Unit,
    onRestauranteClick: (Int) -> Unit = {}
) {
    var prato by remember { mutableStateOf<PratoDetailDto?>(null) }
    var avaliacoes by remember { mutableStateOf(listOf<AvaliacaoDto>()) }
    var erro by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var ratingSelecionado by remember { mutableIntStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    var feedbackEnvio by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var editingMyReview by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(pratoId, refreshKey) {
        isLoading = true
        feedbackEnvio = null
        try {
            prato = repository.getPratoDetail(pratoId)
            avaliacoes = repository.listAvaliacoes(pratoId)
            erro = if (prato == null) "Prato não encontrado" else null

            val minhaAvaliacao = avaliacoes.firstOrNull { it.userId == currentUserId }
            if (minhaAvaliacao == null) {
                ratingSelecionado = 0
                comentario = ""
                editingMyReview = true
            }
        } catch (e: Exception) {
            erro = e.message ?: "Erro ao carregar"
        } finally {
            isLoading = false
        }
    }

    val minhaAvaliacao = avaliacoes.firstOrNull { it.userId == currentUserId }
    val isMyRestaurant = prato?.restauranteId?.let { restaurantIds.contains(it) } == true

    val pullRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { refreshKey++ },
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
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
                SkeletonBlock(modifier = Modifier.fillMaxWidth().height(240.dp), cornerRadius = 20.dp)
                SkeletonBlock(modifier = Modifier.fillMaxWidth().height(180.dp), cornerRadius = 20.dp)
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

            // Erro sem dados
            if (erro != null && prato == null) {
                EmptyState(
                    icon = Icons.Filled.Warning,
                    title = "Algo correu mal",
                    description = erro ?: "Erro desconhecido"
                )
                return@Column
            }

            if (prato == null) return@Column

            // Editar prato (owner)
            if (isMyRestaurant) {
                ClayButton(
                    text = "Editar prato",
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
            }

            if (showEditDialog && prato != null) {
                ClayEditPratoDialog(
                    prato = PratoDto(
                        id = prato!!.id, nome = prato!!.nome, categoria = prato!!.categoria,
                        descricao = prato!!.descricao, preco = prato!!.preco, imagemUrl = prato!!.imagemUrl,
                        restauranteId = prato!!.restauranteId, restauranteNome = prato!!.restauranteNome,
                        ratingMedio = prato!!.ratingMedio, totalAvaliacoes = prato!!.totalAvaliacoes
                    ),
                    onDismiss = { showEditDialog = false },
                    onSave = { updatedPrato ->
                        scope.launch {
                            try {
                                val res = repository.updatePrato(
                                    UpdatePratoRequest(
                                        idPrato = updatedPrato.id, userId = currentUserId,
                                        restauranteId = updatedPrato.restauranteId, nome = updatedPrato.nome,
                                        descricao = updatedPrato.descricao, categoria = updatedPrato.categoria,
                                        preco = updatedPrato.preco, imagemUrl = updatedPrato.imagemUrl
                                    )
                                )
                                feedbackEnvio = if (res.ok) res.message ?: "Prato atualizado!" else res.error ?: "Erro"
                                if (res.ok) {
                                    prato = prato!!.copy(
                                        nome = updatedPrato.nome, descricao = updatedPrato.descricao,
                                        preco = updatedPrato.preco, imagemUrl = updatedPrato.imagemUrl,
                                        categoria = updatedPrato.categoria
                                    )
                                }
                            } catch (e: Exception) {
                                feedbackEnvio = e.message ?: "Erro de rede"
                            } finally {
                                showEditDialog = false
                            }
                        }
                    }
                )
            }

            // Card principal do prato
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = ClayWhite,
                cornerRadius = 24.dp,
                elevation = 8.dp
            ) {
                Column {
                    if (!prato!!.imagemUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = prato!!.imagemUrl,
                            contentDescription = prato!!.nome,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        )
                    }

                    Column(Modifier.padding(20.dp)) {
                        Text(
                            text = prato!!.nome,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ClayTextDark
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${prato!!.categoria ?: "Prato"} · ${prato!!.restauranteNome}",
                            color = ClayTextMedium
                        )
                        Spacer(Modifier.height(12.dp))

                        // Rating badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ClayYellow.copy(alpha = 0.3f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "★ ${String.format(Locale.US, "%.1f", prato!!.ratingMedio)} / 5",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ClayTextDark
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${prato!!.totalAvaliacoes} avaliações",
                                color = ClayTextLight,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Preço
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(ClayPeach.copy(alpha = 0.3f))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Preço: ${if (prato!!.preco == null) "-" else String.format(Locale.US, "%.2f €", prato!!.preco)}",
                                fontWeight = FontWeight.Bold,
                                color = ClayOrangeDeep,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text("Descrição", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ClayTextDark)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = prato!!.descricao ?: "Sem descrição disponível.",
                            color = ClayTextMedium,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(24.dp))

                        // Formulário de avaliação
                        if (minhaAvaliacao == null || editingMyReview) {
                            Text(
                                text = if (minhaAvaliacao == null) "Dá a tua opinião" else "Editar a tua opinião",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ClayTextDark
                            )
                            Spacer(Modifier.height(10.dp))

                            ClayRatingSelector(
                                rating = ratingSelecionado,
                                onRatingSelected = { ratingSelecionado = it }
                            )

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = comentario,
                                onValueChange = { comentario = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Escreve a tua opinião") },
                                minLines = 3,
                                maxLines = 5,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ClayOrange,
                                    unfocusedBorderColor = ClayBeigeDeep,
                                    focusedLabelColor = ClayOrange,
                                    cursorColor = ClayOrange
                                )
                            )

                            Spacer(Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ClayButton(
                                    text = if (minhaAvaliacao == null) "Enviar" else "Atualizar",
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val res = repository.createAvaliacao(
                                                    CreateAvaliacaoRequest(pratoId = prato!!.id, userId = currentUserId, classificacao = ratingSelecionado, comentario = comentario.trim())
                                                )
                                                feedbackEnvio = if (res.ok) res.message ?: "Avaliação guardada!" else res.error ?: "Erro"
                                                if (res.ok) { editingMyReview = false; refreshKey++ }
                                            } catch (e: Exception) {
                                                feedbackEnvio = e.message ?: "Erro de rede"
                                            }
                                        }
                                    },
                                    enabled = ratingSelecionado in 1..5 && comentario.isNotBlank()
                                )
                                if (minhaAvaliacao != null) {
                                    ClayButton(
                                        text = "Cancelar",
                                        onClick = { editingMyReview = false; ratingSelecionado = 0; comentario = "" },
                                        isSecondary = true
                                    )
                                }
                            }

                            AnimatedVisibility(visible = feedbackEnvio != null, enter = fadeIn(), exit = fadeOut()) {
                                Column {
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = feedbackEnvio ?: "",
                                        color = if (feedbackEnvio?.contains("sucesso", true) == true ||
                                            feedbackEnvio?.contains("guardad", true) == true ||
                                            feedbackEnvio?.contains("atualiza", true) == true) ClayGreenDeep else ClayRedDeep,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                        }

                        // Lista de avaliações
                        Text("Comentários recentes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ClayTextDark)
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = ClayBeigeDeep)
                        Spacer(Modifier.height(12.dp))

                        if (avaliacoes.isEmpty()) {
                            EmptyReviewsState()
                        } else {
                            avaliacoes.forEachIndexed { index, a ->
                                ClayAvaliacaoItem(
                                    a = a,
                                    isMine = a.userId == currentUserId,
                                    onEdit = {
                                        ratingSelecionado = a.classificacao
                                        comentario = a.comentario ?: ""
                                        editingMyReview = true
                                    },
                                    onDelete = {
                                        scope.launch {
                                            try {
                                                val res = repository.deleteAvaliacao(DeleteAvaliacaoRequest(a.idAvaliacao, currentUserId))
                                                feedbackEnvio = if (res.ok) res.message ?: "Eliminado!" else res.error ?: "Erro"
                                                if (res.ok) { editingMyReview = false; ratingSelecionado = 0; comentario = ""; refreshKey++ }
                                            } catch (e: Exception) {
                                                feedbackEnvio = e.message ?: "Erro de rede"
                                            }
                                        }
                                    }
                                )
                                if (index != avaliacoes.lastIndex) {
                                    Spacer(Modifier.height(12.dp))
                                    HorizontalDivider(color = ClayBeigeDeep)
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Card do restaurante + mapa
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onRestauranteClick(prato!!.restauranteId) },
                backgroundColor = ClayWhite,
                cornerRadius = 24.dp,
                elevation = 8.dp
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(prato!!.restauranteNome, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ClayTextDark, modifier = Modifier.weight(1f))
                        Text("Ver página →", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ClayBlue)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(prato!!.restauranteMorada ?: "Morada indisponível", color = ClayTextMedium, lineHeight = 22.sp)
                    Spacer(Modifier.height(16.dp))

                    if (prato!!.restauranteLatitude != null && prato!!.restauranteLongitude != null) {
                        EmbeddedMap(lat = prato!!.restauranteLatitude!!, lng = prato!!.restauranteLongitude!!)
                        Spacer(Modifier.height(12.dp))
                        ClayButton(
                            text = "Abrir no Maps",
                            onClick = {
                                val lat = prato!!.restauranteLatitude; val lng = prato!!.restauranteLongitude
                                val label = Uri.encode(prato!!.restauranteNome)
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp)).background(ClayBeige),
                            contentAlignment = Alignment.Center
                        ) { Text("Coordenadas indisponíveis", color = ClayTextLight) }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
    }
}

// ─── Componentes auxiliares ─────────────────────────────────────────────────

@Composable
private fun ClayAvaliacaoItem(
    a: AvaliacaoDto,
    isMine: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(a.autorNome ?: "Utilizador", fontWeight = FontWeight.SemiBold, color = ClayTextDark)
                Spacer(Modifier.height(4.dp))
                Text("★".repeat(a.classificacao.coerceIn(0, 5)), color = ClayYellow, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(a.comentario ?: "", color = ClayTextMedium)
                if (!a.createdAt.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(a.createdAt, color = ClayTextLight, fontSize = 11.sp)
                }
            }
            if (isMine) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Editar", color = ClayOrange, modifier = Modifier.clickable { onEdit() }, fontWeight = FontWeight.Medium)
                    Text("Eliminar", color = ClayRedDeep, modifier = Modifier.clickable { onDelete() }, fontWeight = FontWeight.Medium)
                }
            }
        }
        if (!a.respostaTexto.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            ClayCard(modifier = Modifier.fillMaxWidth(), backgroundColor = ClayBeige, cornerRadius = 16.dp, elevation = 3.dp) {
                Column(Modifier.padding(12.dp)) {
                    Text("Resposta do restaurante", fontWeight = FontWeight.SemiBold, color = ClayTextDark, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(a.respostaTexto, color = ClayTextMedium, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ClayEditPratoDialog(
    prato: PratoDto,
    onDismiss: () -> Unit,
    onSave: (PratoDto) -> Unit
) {
    var nome by remember { mutableStateOf(prato.nome) }
    var descricao by remember { mutableStateOf(prato.descricao ?: "") }
    var preco by remember { mutableStateOf(prato.preco?.toString() ?: "") }
    var imagemUrl by remember { mutableStateOf(prato.imagemUrl ?: "") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClayWhite,
        title = { Text("Editar prato", fontWeight = FontWeight.Bold, color = ClayTextDark) },
        text = {
            Column {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, singleLine = true, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, maxLines = 3, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = preco, onValueChange = { preco = it.filter { c -> c.isDigit() || c == '.' || c == ',' } }, label = { Text("Preço (€)") }, singleLine = true, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = imagemUrl, onValueChange = { imagemUrl = it }, label = { Text("URL da imagem") }, singleLine = true, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            ClayButton(text = "Guardar", onClick = {
                onSave(prato.copy(nome = nome, descricao = descricao, preco = preco.toDoubleOrNull(), imagemUrl = imagemUrl))
            })
        },
        dismissButton = {
            ClayButton(text = "Cancelar", onClick = onDismiss, isSecondary = true)
        }
    )
}

@Composable
private fun EmbeddedMap(lat: Double, lng: Double) {
    val position = LatLng(lat, lng)
    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(position, 16f)
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
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
        Marker(
            state = MarkerState(position = position)
        )
    }
}
