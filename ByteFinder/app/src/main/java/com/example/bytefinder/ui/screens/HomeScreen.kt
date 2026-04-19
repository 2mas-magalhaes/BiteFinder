package com.example.bytefinder.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.Transformation
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.LocationService
import com.example.bytefinder.ui.components.*
import com.example.bytefinder.ui.components.claySceneBackground
import com.example.bytefinder.ui.theme.*
import com.example.bytefinder.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/** Coil transformation that smoothly removes white/light background pixels. */
private class RemoveWhiteTransformation : Transformation {
    override val cacheKey = "remove_white_bg_smooth"
    override suspend fun transform(input: android.graphics.Bitmap, size: coil.size.Size): android.graphics.Bitmap {
        val w = input.width
        val h = input.height
        val pixels = IntArray(w * h)
        input.getPixels(pixels, 0, w, 0, 0, w, h)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = android.graphics.Color.red(pixel)
            val g = android.graphics.Color.green(pixel)
            val b = android.graphics.Color.blue(pixel)
            val a = android.graphics.Color.alpha(pixel)

            // Luminance in 0..255
            val lum = (0.299 * r + 0.587 * g + 0.114 * b)

            // Smooth fade: fully transparent above 245, fully opaque below 200
            // Gradual transition in between
            if (lum > 200) {
                val factor = ((245.0 - lum) / 45.0).coerceIn(0.0, 1.0)
                val newAlpha = (a * factor).toInt()
                pixels[i] = android.graphics.Color.argb(newAlpha, r, g, b)
            }
        }

        val output = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
        output.setPixels(pixels, 0, w, 0, 0, w, h)
        return output
    }
}

/**
 * HomeScreen — Ecrã principal da app com layout Bolt Food + Claymorphism.
 *
 * Secções:
 * 1. Barra de localização + menu
 * 2. Barra de pesquisa com sugestões
 * 3. Filtros avançados (expansíveis)
 * 4. Carrossel de categorias
 * 5. Grelha/lista de pratos filtrados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    repository: DataRepository,
    userName: String,
    isRestaurantUser: Boolean,
    nearModeActive: Boolean = false,
    onPratoClick: (Int) -> Unit,
    onGoHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(context) }

    var locationRequested by remember { mutableStateOf(false) }

    // City/street displayed in the top bar (from GPS or manual)
    val displayCity = if (state.locationLoaded) state.detectedCity else "A detectar..."
    val displayStreet = if (state.locationLoaded) state.detectedStreet else ""

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            scope.launch {
                val loc = locationService.getCurrentLocation()
                if (loc != null) {
                    val matched = locationService.matchCity(loc.city)
                    viewModel.onLocationDetected(matched, "${loc.city}, Portugal", loc.street)
                } else {
                    viewModel.onLocationDetected("Todas", "Portugal", "Localização indisponível")
                }
            }
        } else {
            viewModel.onLocationDetected("Todas", "Portugal", "Permissão negada")
        }
    }

    // Request location on first launch
    LaunchedEffect(Unit) {
        if (!locationRequested) {
            locationRequested = true
            if (locationService.hasLocationPermission()) {
                scope.launch {
                    val loc = locationService.getCurrentLocation()
                    if (loc != null) {
                        val matched = locationService.matchCity(loc.city)
                        viewModel.onLocationDetected(matched, "${loc.city}, Portugal", loc.street)
                    } else {
                        viewModel.onLocationDetected("Todas", "Portugal", "Localização indisponível")
                    }
                }
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
            .statusBarsPadding()
    ) {
        // ─── TOP BAR: Localização + Menu ────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Localização",
                tint = ClayOrangeBolt,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayCity,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ClayOnDark
                )
                if (displayStreet.isNotBlank()) {
                    Text(
                        text = displayStreet,
                        color = ClayOnDarkSecond,
                        fontSize = 12.sp
                    )
                }
            }

            // Refresh location button
            IconButton(
                onClick = {
                    if (locationService.hasLocationPermission()) {
                        scope.launch {
                            val loc = locationService.getCurrentLocation()
                            if (loc != null) {
                                val matched = locationService.matchCity(loc.city)
                                viewModel.onLocationDetected(matched, "${loc.city}, Portugal", loc.street)
                            }
                        }
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Atualizar localização",
                    tint = ClayOrangeBolt,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://bitefinderstorage.blob.core.windows.net/icons/logo-bf.png")
                    .transformations(RemoveWhiteTransformation())
                    .build(),
                contentDescription = "BiteFinder",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onGoHome() }
            )

        }

        // ─── CONTEÚDO SCROLLÁVEL ────────────────────────────────────────
        val pullRefreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.refresh() },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
            Spacer(Modifier.height(8.dp))

            // ─── PESQUISA ───────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                ClaySearchBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )

                // Sugestões de pesquisa
                AnimatedVisibility(
                    visible = state.searchSuggestions.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ClayCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        backgroundColor = ClayWhite,
                        cornerRadius = 18.dp,
                        elevation = 6.dp
                    ) {
                        Column {
                            state.searchSuggestions.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onSearchQueryChange(item.nome)
                                            onPratoClick(item.id)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Restaurant,
                                        contentDescription = null,
                                        tint = ClayTextMedium,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(item.nome, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = ClayTextDark)
                                        Text(item.restauranteNome, fontSize = 12.sp, color = ClayTextLight)
                                    }
                                }
                                if (index != state.searchSuggestions.lastIndex) {
                                    HorizontalDivider(color = ClayBeigeDeep)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── FILTROS AVANÇADOS ──────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                FilterBar(
                    expanded = state.isFiltersExpanded,
                    onToggle = viewModel::onToggleFilters,
                    selectedCity = state.selectedCity,
                    onCityChange = viewModel::onCityChanged,
                    selectedZone = state.selectedZone,
                    onZoneChange = viewModel::onZoneChanged,
                    selectedPriceRange = state.selectedPriceRange,
                    onPriceRangeChange = viewModel::onPriceRangeChanged,
                    availableZones = state.availableZones
                )
            }

            Spacer(Modifier.height(20.dp))

            // ─── CONTEÚDO: Pesquisa vs View All vs Dashboard ────────────
            if (state.searchQuery.isNotBlank() || state.viewAllCategory != null) {
                // Modo pesquisa/view all
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (state.searchQuery.isNotBlank()) viewModel.clearSearch()
                        else viewModel.onBackFromViewAll()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ClayOnDark)
                    }
                    Spacer(Modifier.width(8.dp))
                    val title = if (state.searchQuery.isNotBlank()) "Resultados da pesquisa"
                        else state.viewAllTitle.ifBlank { state.viewAllCategory ?: "" }
                    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ClayOnDark)
                }

                Spacer(Modifier.height(16.dp))

                if (state.filteredPratos.isEmpty()) {
                    if (state.searchQuery.isNotBlank()) {
                        EmptySearchState(query = state.searchQuery)
                    } else {
                        EmptyFilterState()
                    }
                } else {
                    PratosGrid(
                        pratos = state.filteredPratos,
                        onPratoClick = onPratoClick
                    )
                }
            } else {
                // ─── CATEGORIAS (Carrossel horizontal) ──────────────────
                val displayCats = listOf("Todos") + state.categorias.ifEmpty {
                    listOf("Pizza", "Marisco", "Francesinha", "Hambúrguer", "Sushi", "Pasta", "Sobremesas")
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(displayCats) { cat ->
                        val isSelected = state.selectedCategory == cat
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.onCategorySelected(cat) }
                        ) {
                            ClayCard(
                                backgroundColor = if (isSelected) ClayWhite.copy(alpha = 0.18f)
                                    else getCategoryColor(cat).copy(alpha = 0.4f),
                                cornerRadius = 50.dp,
                                elevation = if (isSelected) 6.dp else 3.dp,
                                modifier = Modifier.size(70.dp)
                            ) {
                                AsyncImage(
                                    model = repository.getCategoryImageUrl(cat),
                                    contentDescription = cat,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(if (isSelected) 3.dp else 0.dp)
                                        .clip(CircleShape)
                                        .fillMaxSize()
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                color = if (isSelected) ClayOnDark else ClayOnDarkSecond,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ─── SECÇÃO 1: Melhores da categoria ────────────────────
                if (state.isLoading) {
                    Column(Modifier.padding(horizontal = 24.dp)) {
                        SkeletonRow()
                    }
                } else {
                    val sectionTitle = if (state.selectedCategory == "Todos")
                        "As melhores opções perto de ti" else "As melhores ${state.selectedCategory} perto de ti"

                    SectionHeader(
                        title = sectionTitle,
                        onViewAll = { viewModel.onViewAll(state.selectedCategory, sectionTitle) }
                    )

                    Spacer(Modifier.height(14.dp))

                    if (state.filteredPratos.isEmpty()) {
                        EmptyCategoryState(category = state.selectedCategory)
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.filteredPratos.take(10)) { prato ->
                                ClayDishCard(
                                    prato = prato,
                                    modifier = Modifier.width(200.dp),
                                    onClick = { onPratoClick(prato.id) }
                                )
                            }
                        }

                        Spacer(Modifier.height(28.dp))

                        // ─── SECÇÃO 2: Populares ────────────────────────
                        val sec2Title = if (state.selectedCategory == "Todos") "Populares"
                            else "Outras opções de ${state.selectedCategory}"

                        SectionHeader(
                            title = sec2Title,
                            onViewAll = { viewModel.onViewAll(state.selectedCategory, sec2Title) }
                        )

                        Spacer(Modifier.height(14.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.filteredPratos.take(10).reversed()) { prato ->
                                ClayDishCard(
                                    prato = prato,
                                    modifier = Modifier.width(200.dp),
                                    onClick = { onPratoClick(prato.id) }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
        }
    }
}

// ─── Componentes auxiliares do HomeScreen ────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    onViewAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ClayOnDark,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Ver tudo →",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClayOnDarkSecond,
            modifier = Modifier.clickable { onViewAll() }
        )
    }
}

@Composable
private fun PratosGrid(
    pratos: List<com.example.bytefinder.data.PratoDto>,
    onPratoClick: (Int) -> Unit
) {
    val chunked = pratos.chunked(2)
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (rowItems in chunked) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (prato in rowItems) {
                    Box(modifier = Modifier.weight(1f)) {
                        ClayDishCard(
                            prato = prato,
                            onClick = { onPratoClick(prato.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
    Spacer(Modifier.height(32.dp))
}
