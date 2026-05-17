package com.example.bytefinder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.ui.components.claySceneBackground
import com.example.bytefinder.ui.theme.*
import com.example.bytefinder.ui.viewmodel.HomeViewModel

// ─── Dados de inspiração ──────────────────────────────────────────────────────

private data class InspirationItem(val label: String, val icon: ImageVector)

private val inspirationCategories = listOf(
    InspirationItem("Pizza", Icons.Filled.LocalPizza),
    InspirationItem("Hambúrgueres", Icons.Filled.DinnerDining),
    InspirationItem("Sushi", Icons.Filled.RiceBowl),
    InspirationItem("Pasta", Icons.Filled.Restaurant),
    InspirationItem("Marisco", Icons.Filled.SetMeal),
    InspirationItem("Sobremesas", Icons.Filled.Cake),
    InspirationItem("Francesinha", Icons.Filled.Storefront),
)

// ─── Ecrã de pesquisa ────────────────────────────────────────────────────────

@Composable
fun ClaySearchScreen(
    viewModel: HomeViewModel,
    onPratoClick: (Int) -> Unit,
    onCategorySelected: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Tabs de tipo
    val typeTabs = listOf("Todos", "Pratos", "Restaurantes")
    var selectedTypeTab by remember { mutableStateOf("Todos") }

    // Chips de filtro
    val filterChips = listOf(
        FilterChip("Ordenar", Icons.Filled.SortByAlpha),
        FilterChip("Ofertas", Icons.Filled.Star),
        FilterChip("Filtros", Icons.Filled.FilterList),
    )
    var activeChip by remember { mutableStateOf<String?>(null) }
    var resultMode by remember { mutableStateOf("Lista") }

    // Histórico (mock local — sem persistência por enquanto)
    val searchHistory = remember { mutableStateOf(listOf("pizza", "sushi", "burger")) }

    val hasQuery = state.searchQuery.isNotBlank()
    val hasSuggestions = state.searchSuggestions.isNotEmpty()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
            .statusBarsPadding()
    ) {
        // ─── SEARCH BAR ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Campo de pesquisa
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ClayDarkNavy.copy(alpha = 0.76f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = ClayOnDarkSecond,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (state.searchQuery.isEmpty()) {
                        Text(
                            "Comida, restaurantes, lojas...",
                            color = ClayOnDarkSecond,
                            fontSize = 15.sp
                        )
                    }
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(fontSize = 15.sp, color = ClayOnDark),
                        cursorBrush = SolidColor(ClayBlue),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                    )
                }
                AnimatedVisibility(visible = hasQuery) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Limpar",
                        tint = ClayOnDarkSecond,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { viewModel.clearSearch() }
                    )
                }
            }
        }

        // ─── TABS: Todos / Pratos / Restaurantes ───────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            typeTabs.forEach { tab ->
                val isSelected = tab == selectedTypeTab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTypeTab = tab }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) ClayOnDark else ClayOnDarkSecond
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(if (isSelected) 28.dp else 0.dp)
                            .background(ClayBlue)
                    )
                }
            }
        }

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.07f),
            thickness = 1.dp
        )

        // ─── FILTER CHIPS ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterChips.forEach { chip ->
                val isActive = chip.label == activeChip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isActive) ClayBlue.copy(alpha = 0.25f)
                            else ClayDarkNavy.copy(alpha = 0.76f)
                        )
                        .clickable { activeChip = if (isActive) null else chip.label }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = chip.icon,
                        contentDescription = null,
                        tint = if (isActive) ClayBlue else ClayOnDarkSecond,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = chip.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) ClayOnDark else ClayOnDarkSecond
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ClayDarkNavy.copy(alpha = 0.72f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Lista", "Mapa").forEach { mode ->
                val selected = resultMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) ClayBlue else Color.Transparent)
                        .clickable { resultMode = mode }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        mode,
                        color = if (selected) ClayOnDark else ClayOnDarkSecond,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // ─── CONTEÚDO PRINCIPAL ─────────────────────────────────────────
        if (hasQuery && hasSuggestions) {
            if (resultMode == "Mapa") {
                DiscoveryMapPreview(
                    pratos = state.searchSuggestions,
                    onPratoClick = {
                        viewModel.onSearchQueryChange(it.nome)
                        focusManager.clearFocus()
                        onPratoClick(it.id)
                    }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(state.searchSuggestions) { prato ->
                        SearchResultRow(
                            prato = prato,
                            onClick = {
                                viewModel.onSearchQueryChange(prato.nome)
                                focusManager.clearFocus()
                                onPratoClick(prato.id)
                            }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Histórico de pesquisas
                if (searchHistory.value.isNotEmpty() && !hasQuery) {
                    Spacer(Modifier.height(8.dp))
                    searchHistory.value.forEach { term ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onSearchQueryChange(term)
                                }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                tint = ClayOnDarkSecond,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = term,
                                fontSize = 15.sp,
                                color = ClayOnDark,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remover",
                                tint = ClayOnDarkSecond,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        searchHistory.value = searchHistory.value - term
                                    }
                            )
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }

                // Inspiração
                Spacer(Modifier.height(20.dp))
                Text(
                    "Procura inspiração",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ClayOnDark,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(4.dp))

                inspirationCategories.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onSearchQueryChange(item.label)
                                searchHistory.value = (listOf(item.label) + searchHistory.value).distinct().take(5)
                                onCategorySelected(item.label)
                            }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = ClayOnDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = item.label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ClayOnDark
                        )
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DiscoveryMapPreview(
    pratos: List<PratoDto>,
    onPratoClick: (PratoDto) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(ClayBlueLight.copy(alpha = 0.18f))
        ) {
            Text(
                "Mapa de descoberta",
                color = ClayOnDark,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(18.dp)
            )
            pratos.take(5).forEachIndexed { index, prato ->
                Box(
                    modifier = Modifier
                        .align(
                            when (index % 5) {
                                0 -> Alignment.Center
                                1 -> Alignment.TopEnd
                                2 -> Alignment.BottomStart
                                3 -> Alignment.CenterEnd
                                else -> Alignment.BottomEnd
                            }
                        )
                        .padding(24.dp)
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ClayBlue)
                        .clickable { onPratoClick(prato) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("★", color = ClayOnDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            "Pratos próximos no mapa",
            color = ClayOnDark,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            pratos.take(3).forEach { prato ->
                SearchResultRow(prato = prato, onClick = { onPratoClick(prato) })
            }
        }
    }
}

// ─── Componentes auxiliares ───────────────────────────────────────────────────

private data class FilterChip(val label: String, val icon: ImageVector)

@Composable
private fun SearchResultRow(prato: PratoDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Miniatura do prato
        if (!prato.imagemUrl.isNullOrBlank()) {
            AsyncImage(
                model = prato.imagemUrl,
                contentDescription = prato.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ClayDarkNavy.copy(alpha = 0.76f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Restaurant,
                    contentDescription = null,
                    tint = ClayOnDarkSecond,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prato.nome,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = ClayOnDark
            )
            Text(
                text = prato.restauranteNome,
                fontSize = 12.sp,
                color = ClayOnDarkSecond
            )
        }

        if (prato.ratingMedio > 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = ClayYellow,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = String.format("%.1f", prato.ratingMedio),
                    fontSize = 12.sp,
                    color = ClayOnDarkSecond,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
