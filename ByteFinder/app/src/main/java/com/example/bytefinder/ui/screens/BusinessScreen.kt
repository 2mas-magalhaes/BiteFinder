package com.example.bytefinder.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.data.AvaliacaoDto
import com.example.bytefinder.data.CreatePratoRequest
import com.example.bytefinder.data.CreateRespostaRequest
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.DeletePratoRequest
import com.example.bytefinder.data.MockDataProvider
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.data.UpdatePratoRequest
import com.example.bytefinder.data.displayImageUrl
import com.example.bytefinder.ui.components.*
import com.example.bytefinder.ui.theme.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/**
 * BusinessScreen — Painel de gestão do restaurante (world-class layout).
 */
@Composable
fun ClayBusinessScreen(
    repository: DataRepository,
    currentUserId: Int,
    restauranteIds: List<Int>,
    onBack: () -> Unit,
    onPratoClick: (Int) -> Unit,
    onGoHome: () -> Unit
) {
    var selectedRestauranteId by remember { mutableStateOf(restauranteIds.firstOrNull()) }
    var pratos by remember { mutableStateOf(listOf<PratoDto>()) }
    var selectedPratoId by remember { mutableStateOf<Int?>(null) }
    var avaliacoes by remember { mutableStateOf(listOf<AvaliacaoDto>()) }
    var erro by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    val drafts = remember { mutableStateMapOf<Int, String>() }
    val scope = rememberCoroutineScope()

    // Dialogs
    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<PratoDto?>(null) }

    // Tabs: 0 = Cardápio, 1 = Avaliações
    var selectedTab by remember { mutableIntStateOf(0) }

    val restauranteNome = selectedRestauranteId?.let { repository.getRestauranteNome(it) } ?: "Restaurante"

    LaunchedEffect(selectedRestauranteId, refreshKey) {
        val rid = selectedRestauranteId
        if (rid == null) { pratos = emptyList(); avaliacoes = emptyList(); loading = false; return@LaunchedEffect }
        loading = true; erro = null
        try {
            pratos = repository.listPratos(restauranteId = rid, limit = 100)
            if (pratos.isEmpty()) { selectedPratoId = null; avaliacoes = emptyList(); return@LaunchedEffect }
            if (selectedPratoId == null || pratos.none { it.id == selectedPratoId }) selectedPratoId = pratos.first().id
            selectedPratoId?.let { avaliacoes = repository.listAvaliacoes(it) }
        } catch (e: Exception) { erro = e.message ?: "Erro de rede" } finally { loading = false }
    }

    LaunchedEffect(selectedPratoId, refreshKey) {
        selectedPratoId?.let { avaliacoes = repository.listAvaliacoes(it) }
    }

    val totalAvaliacoes = pratos.sumOf { it.totalAvaliacoes }
    val mediaRating = if (pratos.isEmpty()) 0.0 else pratos.map { it.ratingMedio }.average()
    val semRespostaCount = avaliacoes.count { it.respostaTexto.isNullOrBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandHeader(onGoHome = onGoHome)
            Spacer(Modifier.weight(1f))
            ClayButton(text = "← Voltar", onClick = onBack, isSecondary = true)
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // ── Restaurant Name ─────────────────────────────────────────
            Text(
                restauranteNome,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = ClayOnDark
            )
            Text("Painel de Gestão", color = ClayOnDarkSecond, fontSize = 14.sp)

            Spacer(Modifier.height(12.dp))

            if (restauranteIds.isEmpty()) {
                EmptyState(icon = Icons.Filled.Storefront, title = "Sem restaurante associado", description = "Este utilizador não está associado a nenhum restaurante.")
                return@Column
            }

            // ── Restaurant Selector (if multiple) ───────────────────────
            if (restauranteIds.size > 1) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    restauranteIds.forEach { rid ->
                        val rName = repository.getRestauranteNome(rid)
                        ClayButton(
                            text = rName,
                            onClick = { selectedRestauranteId = rid; selectedPratoId = null; selectedTab = 0; feedback = null },
                            containerColor = if (selectedRestauranteId == rid) ClayBlue else ClayWhite,
                            contentColor = if (selectedRestauranteId == rid) ClayWhite else ClayTextDark
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // ── Stats Cards ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BusinessStatCard("Pratos", pratos.size.toString(), Icons.AutoMirrored.Filled.MenuBook, Modifier.weight(1f))
                BusinessStatCard("Rating", String.format(Locale.US, "%.1f", mediaRating), Icons.Filled.Star, Modifier.weight(1f))
                BusinessStatCard("Reviews", totalAvaliacoes.toString(), Icons.Filled.ChatBubbleOutline, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            if (loading) { SkeletonList(count = 3); return@Column }
            if (erro != null) { Text(erro!!, color = ClayRedDeep); Spacer(Modifier.height(8.dp)) }

            // ── Feedback message ─────────────────────────────────────────
            if (feedback != null) {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (feedback!!.contains("sucesso", true) || feedback!!.contains("criado", true) || feedback!!.contains("atualiz", true) || feedback!!.contains("guardad", true) || feedback!!.contains("eliminad", true))
                        Color(0xFF0D2818) else Color(0xFF2D0D0D),
                    cornerRadius = 14.dp,
                    elevation = 2.dp
                ) {
                    Text(
                        feedback!!,
                        modifier = Modifier.padding(12.dp),
                        color = if (feedback!!.contains("sucesso", true) || feedback!!.contains("criado", true) || feedback!!.contains("atualiz", true) || feedback!!.contains("guardad", true) || feedback!!.contains("eliminad", true))
                            ClayGreenDeep else ClayRedDeep,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Tab Bar ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ClayDarkNavy.copy(alpha = 0.82f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabButton(
                    label = "Cardápio",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    label = "Avaliações",
                    icon = Icons.Filled.RateReview,
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Tab Content ─────────────────────────────────────────────
            when (selectedTab) {
                0 -> CardapioTab(
                    pratos = pratos,
                    onAddClick = { showCreateDialog = true },
                    onEditClick = { prato -> selectedPratoId = prato.id; showEditDialog = true },
                    onDeleteClick = { prato -> showDeleteConfirm = prato },
                    onPratoClick = onPratoClick,
                    onToggleDestaque = { prato ->
                        scope.launch {
                            repository.toggleDestacado(prato.id)
                            feedback = if (MockDataProvider.isDestacado(prato.id))
                                "\"${prato.nome}\" está agora em destaque! (15€/mês)"
                            else "\"${prato.nome}\" removido do destaque."
                            refreshKey++
                        }
                    }
                )
                1 -> AvaliacoesTab(
                    pratos = pratos,
                    selectedPratoId = selectedPratoId,
                    onPratoSelect = { selectedPratoId = it },
                    avaliacoes = avaliacoes,
                    drafts = drafts,
                    semRespostaCount = semRespostaCount,
                    onSubmitReply = { avaliacao ->
                        scope.launch {
                            val texto = (drafts[avaliacao.idAvaliacao] ?: "").trim()
                            if (texto.isBlank()) { feedback = "Escreve a resposta antes de enviar."; return@launch }
                            try {
                                val res = repository.responderAvaliacao(CreateRespostaRequest(avaliacao.idAvaliacao, currentUserId, texto))
                                feedback = if (res.ok) res.message ?: "Resposta guardada com sucesso!" else res.error ?: "Erro"
                                if (res.ok) refreshKey++
                            } catch (e: Exception) { feedback = e.message ?: "Erro de rede" }
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        // ── Dialogs ─────────────────────────────────────────────────────

        if (showCreateDialog) {
            CreatePratoDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { nome, descricao, categoria, preco, imagemUrl ->
                    scope.launch {
                        try {
                            val res = repository.createPrato(CreatePratoRequest(
                                userId = currentUserId,
                                restauranteId = selectedRestauranteId ?: 0,
                                nome = nome, descricao = descricao,
                                categoria = categoria, preco = preco, imagemUrl = imagemUrl
                            ))
                            feedback = if (res.ok) "Prato criado com sucesso!" else res.error ?: "Erro"
                            if (res.ok) refreshKey++
                        } catch (e: Exception) { feedback = e.message ?: "Erro de rede" }
                        showCreateDialog = false
                    }
                }
            )
        }

        if (showEditDialog) {
            val prato = pratos.firstOrNull { it.id == selectedPratoId }
            if (prato != null) {
                EditPratoDialog(
                    prato = prato,
                    onDismiss = { showEditDialog = false },
                    onSave = { updated ->
                        scope.launch {
                            try {
                                val res = repository.updatePrato(UpdatePratoRequest(
                                    idPrato = updated.id, userId = currentUserId,
                                    restauranteId = updated.restauranteId,
                                    nome = updated.nome, descricao = updated.descricao,
                                    categoria = updated.categoria, preco = updated.preco,
                                    imagemUrl = updated.imagemUrl
                                ))
                                feedback = if (res.ok) "Prato atualizado com sucesso!" else res.error ?: "Erro"
                                if (res.ok) refreshKey++
                            } catch (e: Exception) { feedback = e.message ?: "Erro de rede" }
                            showEditDialog = false
                        }
                    }
                )
            }
        }

        showDeleteConfirm?.let { prato ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                containerColor = ClayWhite,
                title = { Text("Eliminar prato?", fontWeight = FontWeight.Bold, color = ClayTextDark) },
                text = {
                    Text("Tem a certeza que deseja eliminar \"${prato.nome}\"? Esta ação não pode ser revertida.",
                        color = ClayTextMedium)
                },
                confirmButton = {
                    ClayButton(text = "Eliminar", containerColor = ClayRedDeep, contentColor = ClayWhite, onClick = {
                        scope.launch {
                            try {
                                val res = repository.deletePrato(DeletePratoRequest(
                                    idPrato = prato.id,
                                    userId = currentUserId,
                                    restauranteId = selectedRestauranteId ?: 0
                                ))
                                feedback = if (res.ok) "Prato eliminado com sucesso!" else res.error ?: "Erro"
                                if (res.ok) { selectedPratoId = null; refreshKey++ }
                            } catch (e: Exception) { feedback = e.message ?: "Erro de rede" }
                            showDeleteConfirm = null
                        }
                    })
                },
                dismissButton = { ClayButton(text = "Cancelar", isSecondary = true, onClick = { showDeleteConfirm = null }) }
            )
        }
    }
}

// ─── Stats Card ─────────────────────────────────────────────────────────

@Composable
private fun BusinessStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    ClayCard(modifier = modifier, backgroundColor = ClayDarkNavy.copy(alpha = 0.86f), cornerRadius = 18.dp, elevation = 4.dp) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, tint = ClayBlue, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = ClayOnDark)
            Text(label, color = ClayOnDarkSecond, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Tab Button ─────────────────────────────────────────────────────────

@Composable
private fun TabButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) ClayBlue else Color.Transparent
    val fg = if (selected) ClayWhite else ClayOnDarkSecond
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = fg, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

// ─── Cardápio Tab ───────────────────────────────────────────────────────

@Composable
private fun CardapioTab(
    pratos: List<PratoDto>,
    onAddClick: () -> Unit,
    onEditClick: (PratoDto) -> Unit,
    onDeleteClick: (PratoDto) -> Unit,
    onPratoClick: (Int) -> Unit,
    onToggleDestaque: (PratoDto) -> Unit
) {
    // Add button
    ClayCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onAddClick),
        backgroundColor = ClayDarkNavy.copy(alpha = 0.86f),
        cornerRadius = 16.dp,
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = ClayBlue, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("Adicionar Novo Prato", color = ClayBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    Spacer(Modifier.height(12.dp))

    if (pratos.isEmpty()) {
        EmptyBusinessState()
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            pratos.forEach { prato ->
                PratoManagementCard(
                    prato = prato,
                    onEdit = { onEditClick(prato) },
                    onDelete = { onDeleteClick(prato) },
                    onClick = { onPratoClick(prato.id) },
                    onToggleDestaque = { onToggleDestaque(prato) }
                )
            }
        }
    }
}

// ─── Prato Management Card ──────────────────────────────────────────────

@Composable
private fun PratoManagementCard(
    prato: PratoDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onToggleDestaque: () -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        backgroundColor = ClayWhite,
        cornerRadius = 18.dp,
        elevation = 5.dp
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Image
            AsyncImage(
                model = prato.displayImageUrl(),
                contentDescription = prato.nome,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                // Category badge + Destaque badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!prato.categoria.isNullOrBlank()) {
                        Text(
                            prato.categoria.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayBlue,
                            letterSpacing = 0.sp,
                            modifier = Modifier
                                .background(ClayBlue.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (prato.destacado) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFFFB800), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFF1A1A00), modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(2.dp))
                            Text(
                                "DESTAQUE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A1A00),
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))

                Text(
                    prato.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ClayTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!prato.descricao.isNullOrBlank()) {
                    Text(
                        prato.descricao,
                        fontSize = 12.sp,
                        color = ClayTextLight,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price
                    Text(
                        prato.preco?.let { String.format(Locale.US, "%.2f €", it) } ?: "—",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = ClayBlue
                    )
                    // Rating
                    Text(
                        "★ ${String.format(Locale.US, "%.1f", prato.ratingMedio)}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = ClayYellowDeep
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Action buttons - two rows for proper spacing
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Destaque toggle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (prato.destacado) Color(0xFFFFB800) else Color(0xFFFFF3D0))
                            .clickable(onClick = onToggleDestaque)
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            if (prato.destacado) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Destacar",
                            tint = if (prato.destacado) Color(0xFF1A1A00) else Color(0xFF8B6914),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    // Edit
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ClayBlue.copy(alpha = 0.12f))
                            .clickable(onClick = onEdit)
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = ClayBlue, modifier = Modifier.size(18.dp))
                    }
                    // Delete
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ClayRedDeep.copy(alpha = 0.12f))
                            .clickable(onClick = onDelete)
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = ClayRedDeep, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ─── Avaliações Tab ─────────────────────────────────────────────────────

@Composable
private fun AvaliacoesTab(
    pratos: List<PratoDto>,
    selectedPratoId: Int?,
    onPratoSelect: (Int) -> Unit,
    avaliacoes: List<AvaliacaoDto>,
    drafts: MutableMap<Int, String>,
    semRespostaCount: Int,
    onSubmitReply: (AvaliacaoDto) -> Unit
) {
    if (pratos.isEmpty()) {
        EmptyBusinessState()
        return
    }

    // Prato selector
    Text("Selecionar prato:", color = ClayOnDarkSecond, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pratos.forEach { prato ->
            ClayButton(
                text = prato.nome,
                onClick = { onPratoSelect(prato.id) },
                containerColor = if (selectedPratoId == prato.id) ClayBlue else ClayWhite,
                contentColor = if (selectedPratoId == prato.id) ClayWhite else ClayTextDark
            )
        }
    }

    Spacer(Modifier.height(14.dp))

    if (semRespostaCount > 0) {
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF2D1F00),
            cornerRadius = 14.dp,
            elevation = 2.dp
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = ClayYellowDeep, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("$semRespostaCount avaliações sem resposta", color = ClayYellowDeep, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
    }

    if (avaliacoes.isEmpty()) {
        EmptyReviewsState()
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            avaliacoes.forEach { avaliacao ->
                val draft = drafts[avaliacao.idAvaliacao] ?: avaliacao.respostaTexto.orEmpty()
                BusinessReviewCard(
                    avaliacao = avaliacao,
                    draft = draft,
                    onDraftChange = { drafts[avaliacao.idAvaliacao] = it },
                    onSubmit = { onSubmitReply(avaliacao) }
                )
            }
        }
    }
}

// ─── Business Review Card ───────────────────────────────────────────────

@Composable
private fun BusinessReviewCard(
    avaliacao: AvaliacaoDto,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val sentiment = reviewSentiment(avaliacao.classificacao)
    ClayCard(modifier = Modifier.fillMaxWidth(), backgroundColor = ClayWhite, cornerRadius = 18.dp, elevation = 5.dp) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(avaliacao.autorNome ?: "Cliente", fontWeight = FontWeight.SemiBold, color = ClayTextDark)
                    if (!avaliacao.createdAt.isNullOrBlank()) {
                        Text(avaliacao.createdAt, fontSize = 11.sp, color = ClayTextLight)
                    }
                }
                Row {
                    repeat(avaliacao.classificacao.coerceIn(0, 5)) {
                        Text("★", color = ClayYellowDeep, fontSize = 16.sp)
                    }
                    repeat((5 - avaliacao.classificacao).coerceAtLeast(0)) {
                        Text("★", color = ClayTextLight.copy(alpha = 0.3f), fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(sentiment.second.copy(alpha = 0.16f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    sentiment.first,
                    color = sentiment.second,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(avaliacao.comentario ?: "Sem comentário", color = ClayTextMedium, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                label = { Text("Resposta do restaurante") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2, maxLines = 4,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ClayBlue,
                    unfocusedBorderColor = ClayBeigeDeep,
                    focusedLabelColor = ClayBlue,
                    cursorColor = ClayBlue
                )
            )

            Spacer(Modifier.height(8.dp))

            ClayButton(
                text = if (avaliacao.respostaTexto.isNullOrBlank()) "Responder" else "Atualizar resposta",
                onClick = onSubmit,
                enabled = draft.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun reviewSentiment(rating: Int): Pair<String, Color> = when {
    rating >= 5 -> "Excelente" to ClayGreenDeep
    rating >= 4 -> "Consistente" to ClayBlue
    rating >= 3 -> "Neutro" to ClayYellowDeep
    else -> "Precisa atenção" to ClayRedDeep
}

// ─── Create Prato Dialog ────────────────────────────────────────────────

@Composable
private fun CreatePratoDialog(
    onDismiss: () -> Unit,
    onCreate: (nome: String, descricao: String?, categoria: String?, preco: Double?, imagemUrl: String?) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var imagemUri by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClayWhite,
        title = {
            Column {
                Text("Novo prato", fontWeight = FontWeight.ExtraBold, color = ClayTextDark, fontSize = 22.sp)
                Text("Fotografia, detalhes e preco num fluxo simples.", color = ClayTextMedium, fontSize = 13.sp)
            }
        },
        text = {
            Column {
                DishPhotoPicker(imageUri = imagemUri, onImageSelected = { imagemUri = it })
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = nome, onValueChange = { nome = it },
                    label = { Text("Nome *") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = categoria, onValueChange = { categoria = it },
                    label = { Text("Categoria") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = preco, onValueChange = { preco = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                    label = { Text("Preço (€)") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = descricao, onValueChange = { descricao = it },
                    label = { Text("Descrição") }, maxLines = 3,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("A imagem e escolhida pela galeria ou camara.", color = ClayTextMedium, fontSize = 12.sp)
            }
        },
        confirmButton = {
            ClayButton(
                text = "Adicionar",
                enabled = nome.isNotBlank(),
                onClick = {
                    onCreate(
                        nome.trim(),
                        descricao.trim().ifBlank { null },
                        categoria.trim().ifBlank { null },
                        preco.replace(",", ".").toDoubleOrNull(),
                        imagemUri.ifBlank { null }
                    )
                }
            )
        },
        dismissButton = { ClayButton(text = "Cancelar", isSecondary = true, onClick = onDismiss) }
    )
}

// ─── Edit Prato Dialog ──────────────────────────────────────────────────

@Composable
private fun EditPratoDialog(prato: PratoDto, onDismiss: () -> Unit, onSave: (PratoDto) -> Unit) {
    var nome by remember { mutableStateOf(prato.nome) }
    var descricao by remember { mutableStateOf(prato.descricao ?: "") }
    var preco by remember { mutableStateOf(prato.preco?.toString() ?: "") }
    var imagemUrl by remember { mutableStateOf(prato.imagemUrl ?: "") }
    var categoria by remember { mutableStateOf(prato.categoria ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClayWhite,
        title = { Text("Editar prato", fontWeight = FontWeight.Bold, color = ClayTextDark) },
        text = {
            Column {
                // Image preview
                if (imagemUrl.isNotBlank()) {
                    AsyncImage(
                        model = imagemUrl,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                }

                OutlinedTextField(value = nome, onValueChange = { nome = it },
                    label = { Text("Nome") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = categoria, onValueChange = { categoria = it },
                    label = { Text("Categoria") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = preco, onValueChange = { preco = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                    label = { Text("Preço (€)") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = descricao, onValueChange = { descricao = it },
                    label = { Text("Descrição") }, maxLines = 3,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = imagemUrl, onValueChange = { imagemUrl = it },
                    label = { Text("URL da imagem") }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            ClayButton(text = "Guardar", onClick = {
                onSave(prato.copy(
                    nome = nome, descricao = descricao, categoria = categoria,
                    preco = preco.replace(",", ".").toDoubleOrNull(),
                    imagemUrl = imagemUrl
                ))
            })
        },
        dismissButton = { ClayButton(text = "Cancelar", isSecondary = true, onClick = onDismiss) }
    )
}

@Composable
private fun DishPhotoPicker(
    imageUri: String,
    onImageSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) onImageSelected(uri.toString())
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) onImageSelected(saveDishBitmap(context, bitmap))
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    ClayCard(backgroundColor = ClayWhite, cornerRadius = 22.dp, elevation = 8.dp) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ClayBlueLight)
                    .border(1.dp, ClayBluePale, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri.isNotBlank()) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Fotografia do prato",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.RestaurantMenu, contentDescription = null, tint = ClayBlue, modifier = Modifier.size(34.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("Adiciona uma foto apetecivel", color = ClayTextDark, fontWeight = FontWeight.Bold)
                        Text("Galeria ou camara do telemovel", color = ClayTextMedium, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MediaActionChip(
                    text = "Galeria",
                    icon = Icons.Filled.PhotoLibrary,
                    modifier = Modifier.weight(1f),
                    onClick = { galleryLauncher.launch("image/*") }
                )
                MediaActionChip(
                    text = "Camara",
                    icon = Icons.Filled.AddAPhoto,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        if (granted) cameraLauncher.launch(null) else permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }
    }
}

@Composable
private fun MediaActionChip(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ClayBlue.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ClayBlue, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, color = ClayBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

private fun saveDishBitmap(context: Context, bitmap: Bitmap): String {
    val file = File(context.cacheDir, "bitefinder-dish-${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { output ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
    }
    return file.toUri().toString()
}
