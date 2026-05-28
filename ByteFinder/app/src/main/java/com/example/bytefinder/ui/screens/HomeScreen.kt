package com.example.bytefinder.ui.screens


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
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
import com.example.bytefinder.data.displayImageUrl
import com.example.bytefinder.ui.components.*
import com.example.bytefinder.ui.components.claySceneBackground
import com.example.bytefinder.ui.theme.*
import com.example.bytefinder.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

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

private fun mapMarkerSizeForRadius(radiusKm: Double) =
    (64.0 - radiusKm.coerceIn(1.0, 20.0) * 7.0).coerceIn(26.0, 58.0).dp

private fun mapMarkerRatingSizeForRadius(radiusKm: Double) =
    (10.5 - radiusKm.coerceIn(1.0, 20.0) * 0.45).coerceIn(7.0, 10.0).sp

private fun mapMarkerImageUrl(prato: com.example.bytefinder.data.PratoDto): String? {
    val imageUrl = prato.displayImageUrl()
    if (imageUrl != null && !imageUrl.contains("wikimedia", ignoreCase = true)) {
        return imageUrl
    }

    return when (prato.categoria?.lowercase().orEmpty()) {
        "sushi" -> "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?auto=format&fit=crop&q=80&w=240"
        "pizza" -> "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&q=80&w=240"
        "marisco" -> "https://images.unsplash.com/photo-1559737558-2f5a35f4523b?auto=format&fit=crop&q=80&w=240"
        "bacalhau" -> "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&q=80&w=240"
        "pratos tradicionais" -> "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&q=80&w=240"
        "francesinha" -> "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&q=80&w=240"
        else -> imageUrl ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&q=80&w=240"
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
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    // For dummy purposes, we simulate matched city logic
                    viewModel.onLocationDetected(
                        matchedCity = "Todas",
                        displayCity = "Localização Atual",
                        displayStreet = "Minha Rua",
                        lat = it.latitude,
                        lng = it.longitude
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (false) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine && !hasCoarse) {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.onLocationDetected(
                        matchedCity = "Todas",
                        displayCity = "Localização Atual",
                        displayStreet = "Minha Rua",
                        lat = it.latitude,
                        lng = it.longitude
                    )
                }
            }
        }
        }
    }

    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(context) }
    var liveLocationPermission by remember { mutableStateOf(locationService.hasLocationPermission()) }

    var locationRequested by remember { mutableStateOf(false) }

    fun applyLiveLocation(loc: com.example.bytefinder.data.UserLocation) {
        val matched = locationService.matchCity(loc.city)
        viewModel.onLocationDetected(
            matchedCity = matched,
            displayCity = "${loc.city}, Portugal",
            displayStreet = loc.street,
            lat = loc.latitude,
            lng = loc.longitude
        )
    }

    LaunchedEffect(liveLocationPermission) {
        if (liveLocationPermission) {
            locationService.getCurrentLocation()?.let(::applyLiveLocation)
            locationService.locationUpdates().collect(::applyLiveLocation)
        }
    }

    // City/street displayed in the top bar (from GPS or manual)
    val displayCity by remember {
        androidx.compose.runtime.derivedStateOf { if (state.locationLoaded) state.detectedCity else "A detectar..." }
    }
    val displayStreet by remember {
        androidx.compose.runtime.derivedStateOf { if (state.locationLoaded) state.detectedStreet else "" }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            liveLocationPermission = true
            scope.launch {
                val loc = locationService.getCurrentLocation()
                if (loc != null) {
                    val matched = locationService.matchCity(loc.city)
                    viewModel.onLocationDetected(matched, "${loc.city}, Portugal", loc.street, loc.latitude, loc.longitude)
                } else {
                    viewModel.onLocationDetected("Todas", "Portugal", "Localização indisponível", null, null)
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
                        viewModel.onLocationDetected(matched, "${loc.city}, Portugal", loc.street, loc.latitude, loc.longitude)
                    } else {
                        viewModel.onLocationDetected("Todas", "Portugal", "Localização indisponível", null, null)
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
                        liveLocationPermission = true
                        scope.launch {
                            val loc = locationService.getCurrentLocation()
                            if (loc != null) {
                                val matched = locationService.matchCity(loc.city)
                                viewModel.onLocationDetected(matched, "${loc.city}, Portugal", loc.street, loc.latitude, loc.longitude)
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
                    .crossfade(true)
                    .build(),
                contentDescription = "BiteFinder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
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

                // ─── SECÇÃO DESTAQUE: Pratos em destaque (patrocinados) ─
                if (state.featuredPratos.isNotEmpty() && !state.isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 18.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Em Destaque",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayOnDark
                            )
                        }
                        Text(
                            "Patrocinado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClayOnDarkSecond
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.featuredPratos) { prato ->
                            FeaturedDishCard(
                                prato = prato,
                                modifier = Modifier.width(260.dp),
                                onClick = { onPratoClick(prato.id) }
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))
                }

                // ─── NA ZONA & PRATOS TRADICIONAIS ──────────────────────
                val lat = state.userLat
                val lng = state.userLng
                if (nearModeActive && state.locationLoaded && lat != null && lng != null) {
                    SectionHeader(
                        title = "Na Zona",
                        onViewAll = { viewModel.onViewAll("Todas", "Na Zona") }
                    )
                    Spacer(Modifier.height(14.dp))
                    
                    // Controlo do Raio
                    Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
                        Text(
                            text = "Raio de pesquisa: ${state.radiusKm.toInt()} km",
                            style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ClayTextDark)
                        )
                        Slider(
                            value = state.radiusKm.toFloat(),
                            onValueChange = { viewModel.onRadiusChanged(it.toDouble()) },
                            onValueChangeFinished = { viewModel.refreshNearbyPratos() },
                            valueRange = 1f..20f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = ClayOrangeBolt,
                                activeTrackColor = ClayOrangeBolt
                            )
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    val userLocation = LatLng(lat, lng)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(userLocation, 12f)
                    }

                    // Map UI
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = false), // simulado via custom marker
                            uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = false)
                        ) {
                            // User Location
                            MarkerComposable(
                                state = MarkerState(position = userLocation),
                                title = "A tua localização"
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.Blue)
                                        .border(2.dp, Color.White, CircleShape)
                                )
                            }
                            
                            // Radius Circle
                            Circle(
                                center = userLocation,
                                radius = state.radiusKm * 1000,
                                fillColor = ClayOrangeBolt.copy(alpha = 0.15f),
                                strokeColor = ClayOrangeBolt,
                                strokeWidth = 2f
                            )

                            // Restaurantes & pratos
                            val pratosByRestaurante = state.nearbyPratos
                                .filter { it.restauranteLatitude != null && it.restauranteLongitude != null }
                                .groupBy { it.restauranteId }
                            pratosByRestaurante.forEach { (restId, pratos) ->
                                val topPrato = pratos.minWithOrNull(
                                    compareBy<com.example.bytefinder.data.PratoDto> { it.distanciaKm ?: Double.MAX_VALUE }
                                        .thenByDescending { it.ratingMedio }
                                        .thenByDescending { it.totalAvaliacoes }
                                ) ?: pratos.first()
                                val restLat = topPrato.restauranteLatitude
                                val restLng = topPrato.restauranteLongitude
                                if (restLat != null && restLng != null) {
                                    MarkerComposable(
                                        keys = arrayOf<Any>(restId, topPrato.id, state.radiusKm),
                                        state = MarkerState(position = LatLng(restLat, restLng)),
                                        onClick = { 
                                            onPratoClick(topPrato.id)
                                            true
                                        }
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            val markerSize = mapMarkerSizeForRadius(state.radiusKm)
                                            val ratingFontSize = mapMarkerRatingSizeForRadius(state.radiusKm)
                                            val ratingText = String.format(
                                                java.util.Locale.US,
                                                "%.1f",
                                                topPrato.ratingMedio
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .size(markerSize),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape)
                                                        .background(Color.White)
                                                        .border(2.dp, ClayOrangeBolt, CircleShape)
                                                        .padding(3.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(LocalContext.current)
                                                            .data(mapMarkerImageUrl(topPrato))
                                                            .allowHardware(false)
                                                            .crossfade(true)
                                                            .build(),
                                                        contentDescription = topPrato.nome,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape)
                                                    )
                                                }
                                                if (topPrato.ratingMedio > 0.0) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.BottomEnd)
                                                            .offset(x = 8.dp, y = 6.dp)
                                                            .background(ClayOrangeBolt, RoundedCornerShape(999.dp))
                                                            .border(1.dp, Color.White, RoundedCornerShape(999.dp))
                                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "\u2605 $ratingText",
                                                            color = Color.White,
                                                            fontSize = ratingFontSize,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = topPrato.restauranteNome,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ClayTextDark,
                                                modifier = Modifier
                                                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    when {
                        state.isNearbyLoading -> {
                            Column(Modifier.padding(horizontal = 24.dp)) {
                                SkeletonRow()
                            }
                        }
                        state.nearbyError != null -> {
                            Text(
                                text = state.nearbyError ?: "",
                                color = ClayOnDarkSecond,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        state.nearbyPratos.isEmpty() -> {
                            Text(
                                text = "Ainda nao encontramos pratos num raio de ${state.radiusKm.toInt()} km.",
                                color = ClayOnDarkSecond,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        else -> {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.nearbyPratos.take(10)) { prato ->
                                    NearbyDishCard(
                                        prato = prato,
                                        modifier = Modifier.width(220.dp),
                                        onClick = { onPratoClick(prato.id) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(28.dp))

                    // --- Pratos Tradicionais Section ---
                    if (state.tradicionaisPratos.isNotEmpty()) {
                        SectionHeader(
                            title = "Pratos Tradicionais na Zona",
                            onViewAll = { viewModel.onViewAll("Pratos Tradicionais", "Pratos Tradicionais na Zona") }
                        )
                        Spacer(Modifier.height(14.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.tradicionaisPratos) { prato ->
                                ClayDishCard(
                                    prato = prato,
                                    modifier = Modifier.width(200.dp),
                                    onClick = { onPratoClick(prato.id) }
                                )
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                    }
                }

                // ─── SECÇÃO 1: Melhores da categoria ────────────────────
                if (state.isLoading) {
                    Column(Modifier.padding(horizontal = 24.dp)) {
                        SkeletonRow()
                    }
                } else {
                    val sectionTitle by remember {
                        androidx.compose.runtime.derivedStateOf {
                            if (state.selectedCategory == "Todos")
                                "As melhores opções perto de ti" else "As melhores ${state.selectedCategory} perto de ti"
                        }
                    }

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

                // AdMob Banner No Fundo
                AdMobBanner()
                Spacer(Modifier.height(16.dp))
            }
        }
        }
    }
}

// ─── AdMob Banner Dummy ──────────────────────────────────────────────────────

@Composable
fun AdMobBanner() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    // Google's test ad unit ID for Banners
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
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

// ─── Featured Dish Card (destaque patrocinado) ──────────────────────────────

@Composable
private fun NearbyDishCard(
    prato: com.example.bytefinder.data.PratoDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ClayCard(
        modifier = modifier.clickable(onClick = onClick),
        backgroundColor = ClayWhite,
        cornerRadius = 18.dp,
        elevation = 6.dp
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(prato.displayImageUrl())
                    .crossfade(true)
                    .build(),
                contentDescription = prato.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prato.nome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ClayTextDark,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    prato.distanciaKm?.let { distance ->
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f km", distance),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClayOrangeBolt
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = prato.restauranteNome,
                    fontSize = 12.sp,
                    color = ClayTextMedium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prato.preco?.let { String.format(java.util.Locale.US, "%.2f EUR", it) } ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ClayTextDark
                    )
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", prato.ratingMedio),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = ClayTextMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedDishCard(
    prato: com.example.bytefinder.data.PratoDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ClayCard(
        modifier = modifier.clickable(onClick = onClick),
        backgroundColor = ClayWhite,
        cornerRadius = 20.dp,
        elevation = 10.dp
    ) {
        Column {
            Box {
                AsyncImage(
                    model = prato.displayImageUrl(),
                    contentDescription = prato.nome,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )
                // Featured badge
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            Color(0xFFFFB800),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        "⭐ DESTAQUE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A00),
                        letterSpacing = 0.sp
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                if (!prato.categoria.isNullOrBlank()) {
                    Text(
                        prato.categoria.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB800),
                        letterSpacing = 0.sp
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Text(
                    prato.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ClayOnDark,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    prato.restauranteNome,
                    fontSize = 12.sp,
                    color = ClayOnDarkSecond,
                    maxLines = 1
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        prato.preco?.let { String.format(java.util.Locale.US, "%.2f €", it) } ?: "—",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFFFFB800)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("★", color = ClayYellowDeep, fontSize = 14.sp)
                        Spacer(Modifier.width(2.dp))
                        Text(
                            String.format(java.util.Locale.US, "%.1f", prato.ratingMedio),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = ClayOnDarkSecond
                        )
                    }
                }
            }
        }
    }
}
