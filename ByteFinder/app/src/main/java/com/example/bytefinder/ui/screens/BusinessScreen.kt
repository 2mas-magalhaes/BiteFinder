package com.example.bytefinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.data.AvaliacaoDto
import com.example.bytefinder.data.CreateRespostaRequest
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.data.UpdatePratoRequest
import com.example.bytefinder.ui.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import com.example.bytefinder.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * BusinessScreen — Painel de gestão do restaurante com estilo Claymorphism.
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
    var showEditDialog by remember { mutableStateOf(false) }

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

    val selectedPrato = pratos.firstOrNull { it.id == selectedPratoId }
    val semRespostaCount = avaliacoes.count { it.respostaTexto.isNullOrBlank() }
    val mediaRating = if (avaliacoes.isEmpty()) 0.0 else avaliacoes.map { it.classificacao }.average()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ClayCream)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BrandHeader(onGoHome = onGoHome)
            Spacer(Modifier.weight(1f))
            ClayButton(text = "← Voltar", onClick = onBack, isSecondary = true)
        }

        Spacer(Modifier.height(14.dp))

        Text("Painel Business 🏪", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = ClayTextDark)
        Text("Gestão de reputação e feedback", color = ClayTextMedium, fontSize = 14.sp)

        Spacer(Modifier.height(14.dp))

        if (restauranteIds.isEmpty()) {
            EmptyState(icon = Icons.Filled.Storefront, title = "Sem restaurante associado", description = "Este utilizador não está associado a nenhum restaurante.")
            return@Column
        }

        // Seletor de restaurante
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            restauranteIds.forEach { rid ->
                ClayButton(
                    text = "Restaurante #$rid",
                    onClick = { selectedRestauranteId = rid; selectedPratoId = null; feedback = null },
                    containerColor = if (selectedRestauranteId == rid) ClayOrange else ClayBeige,
                    contentColor = if (selectedRestauranteId == rid) ClayWhite else ClayTextDark
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Stats cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Pratos", pratos.size.toString(), Modifier.weight(1f))
            StatCard("Comentários", avaliacoes.size.toString(), Modifier.weight(1f))
            StatCard("Sem resposta", semRespostaCount.toString(), Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))

        if (loading) { SkeletonList(count = 3); return@Column }
        if (erro != null) { Text(erro!!, color = ClayRedDeep); Spacer(Modifier.height(8.dp)) }

        if (pratos.isEmpty()) {
            EmptyBusinessState()
            return@Column
        }

        Text("Pratos do restaurante", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = ClayTextDark)
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pratos.forEach { prato ->
                ClayButton(
                    text = prato.nome,
                    onClick = { selectedPratoId = prato.id; feedback = null },
                    containerColor = if (selectedPratoId == prato.id) ClayOrange else ClayBeige,
                    contentColor = if (selectedPratoId == prato.id) ClayWhite else ClayTextDark
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (selectedPrato != null) {
            ClayCard(modifier = Modifier.fillMaxWidth(), backgroundColor = ClayWhite, cornerRadius = 20.dp, elevation = 6.dp) {
                Column(Modifier.padding(16.dp)) {
                    Text(selectedPrato.nome, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ClayTextDark)
                    Spacer(Modifier.height(4.dp))
                    Text(selectedPrato.restauranteNome, color = ClayTextMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("★ ${String.format(Locale.US, "%.1f", mediaRating)} média", color = ClayYellowDeep, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ClayButton(text = "Ver prato", onClick = { onPratoClick(selectedPrato.id) })
                        ClayButton(text = "Editar", onClick = { showEditDialog = true }, isSecondary = true)
                    }
                }
            }
        }

        if (showEditDialog && selectedPrato != null) {
            ClayEditBusinessDialog(
                prato = selectedPrato,
                onDismiss = { showEditDialog = false },
                onSave = { updated ->
                    scope.launch {
                        try {
                            val res = repository.updatePrato(UpdatePratoRequest(
                                idPrato = updated.id, userId = currentUserId, restauranteId = updated.restauranteId,
                                nome = updated.nome, descricao = updated.descricao, categoria = updated.categoria,
                                preco = updated.preco, imagemUrl = updated.imagemUrl
                            ))
                            feedback = if (res.ok) res.message ?: "Prato atualizado!" else res.error ?: "Erro"
                            if (res.ok) refreshKey++
                        } catch (e: Exception) { feedback = e.message ?: "Erro de rede" } finally { showEditDialog = false }
                    }
                }
            )
        }

        Spacer(Modifier.height(14.dp))

        Text("Comentários e respostas", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = ClayTextDark)
        Spacer(Modifier.height(8.dp))

        if (feedback != null) {
            Text(
                feedback!!,
                color = if (feedback!!.contains("sucesso", true) || feedback!!.contains("atualiz", true) || feedback!!.contains("guardad", true)) ClayGreenDeep else ClayRedDeep,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(8.dp))
        }

        if (avaliacoes.isEmpty()) {
            EmptyReviewsState()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                avaliacoes.forEach { avaliacao ->
                    val draft = drafts[avaliacao.idAvaliacao] ?: avaliacao.respostaTexto.orEmpty()
                    ClayBusinessReviewItem(
                        avaliacao = avaliacao,
                        draft = draft,
                        onDraftChange = { drafts[avaliacao.idAvaliacao] = it },
                        onSubmit = {
                            scope.launch {
                                val texto = (drafts[avaliacao.idAvaliacao] ?: "").trim()
                                if (texto.isBlank()) { feedback = "Escreve a resposta antes de enviar."; return@launch }
                                try {
                                    val res = repository.responderAvaliacao(CreateRespostaRequest(avaliacao.idAvaliacao, currentUserId, texto))
                                    feedback = if (res.ok) res.message ?: "Resposta guardada!" else res.error ?: "Erro"
                                    if (res.ok) refreshKey++
                                } catch (e: Exception) { feedback = e.message ?: "Erro de rede" }
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    ClayCard(modifier = modifier, backgroundColor = ClayWhite, cornerRadius = 18.dp, elevation = 4.dp) {
        Column(Modifier.padding(12.dp)) {
            Text(label, color = ClayTextLight, fontSize = 11.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = ClayTextDark)
        }
    }
}

@Composable
private fun ClayBusinessReviewItem(
    avaliacao: AvaliacaoDto,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    ClayCard(modifier = Modifier.fillMaxWidth(), backgroundColor = ClayWhite, cornerRadius = 18.dp, elevation = 5.dp) {
        Column(Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(avaliacao.autorNome ?: "Cliente", fontWeight = FontWeight.SemiBold, color = ClayTextDark)
                Text("★".repeat(avaliacao.classificacao.coerceIn(0, 5)), color = ClayYellow)
            }
            if (!avaliacao.createdAt.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(avaliacao.createdAt, fontSize = 11.sp, color = ClayTextLight)
            }
            Spacer(Modifier.height(8.dp))
            Text(avaliacao.comentario ?: "Sem comentário", color = ClayTextMedium)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                label = { Text("Resposta do restaurante") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2, maxLines = 4,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ClayOrange, unfocusedBorderColor = ClayBeigeDeep, focusedLabelColor = ClayOrange, cursorColor = ClayOrange)
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

@Composable
private fun ClayEditBusinessDialog(prato: PratoDto, onDismiss: () -> Unit, onSave: (PratoDto) -> Unit) {
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
        confirmButton = { ClayButton(text = "Guardar", onClick = { onSave(prato.copy(nome = nome, descricao = descricao, preco = preco.toDoubleOrNull(), imagemUrl = imagemUrl)) }) },
        dismissButton = { ClayButton(text = "Cancelar", onClick = onDismiss, isSecondary = true) }
    )
}
