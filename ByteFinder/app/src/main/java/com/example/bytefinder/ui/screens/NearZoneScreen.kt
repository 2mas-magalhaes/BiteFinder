package com.example.bytefinder.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.bytefinder.data.LocationService
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.data.displayImageUrl
import com.example.bytefinder.ui.components.ClayCard
import com.example.bytefinder.ui.components.SkeletonRow
import com.example.bytefinder.ui.components.claySceneBackground
import com.example.bytefinder.ui.theme.ClayBluePale
import com.example.bytefinder.ui.theme.ClayOnDark
import com.example.bytefinder.ui.theme.ClayOnDarkSecond
import com.example.bytefinder.ui.theme.ClayOrangeBolt
import com.example.bytefinder.ui.theme.ClayTextDark
import com.example.bytefinder.ui.theme.ClayTextMedium
import com.example.bytefinder.ui.theme.ClayWhite
import com.example.bytefinder.ui.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.ln

@Composable
fun NearZoneScreen(
    viewModel: HomeViewModel,
    onPratoClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(context) }
    var hasLocationPermission by remember { mutableStateOf(locationService.hasLocationPermission()) }

    fun applyLocation(loc: com.example.bytefinder.data.UserLocation) {
        val matched = locationService.matchCity(loc.city)
        viewModel.onLocationDetected(
            matchedCity = matched,
            displayCity = "${loc.city}, Portugal",
            displayStreet = loc.street,
            lat = loc.latitude,
            lng = loc.longitude
        )
    }

    fun refreshLocationOnce() {
        scope.launch {
            locationService.getCurrentLocation()?.let(::applyLocation)
                ?: viewModel.onLocationDetected("Todas", "Portugal", "Localizacao indisponivel")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            hasLocationPermission = true
            refreshLocationOnce()
        } else {
            viewModel.onLocationDetected("Todas", "Portugal", "Permissao de localizacao negada")
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            refreshLocationOnce()
            locationService.locationUpdates().collect(::applyLocation)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            NearHeader(
                city = if (state.locationLoaded) state.detectedCity else "A detectar...",
                street = state.detectedStreet,
                onRefreshLocation = {
                    if (locationService.hasLocationPermission()) {
                        hasLocationPermission = true
                        refreshLocationOnce()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            )
        }

        item {
            RadiusControl(
                radiusKm = state.radiusKm,
                onRadiusChange = viewModel::onRadiusChanged,
                onRadiusFinished = viewModel::refreshNearbyPratos
            )
        }

        item {
            val lat = state.userLat
            val lng = state.userLng
            when {
                lat != null && lng != null -> NearMapCard(
                    userLocation = LatLng(lat, lng),
                    radiusKm = state.radiusKm,
                    pratos = state.nearbyPratos,
                    onPratoClick = onPratoClick
                )
                state.locationLoaded -> LocationMessage(
                    title = "Sem localizacao",
                    message = state.detectedStreet.ifBlank { "Ativa a localizacao para veres restaurantes perto de ti." }
                )
                else -> LocationMessage(
                    title = "A obter localizacao",
                    message = "Estamos a preparar a tua zona."
                )
            }
        }

        item {
            Spacer(Modifier.height(22.dp))
            SectionTitle("Pratos perto de ti")
            Spacer(Modifier.height(12.dp))
        }

        item {
            NearbyContent(
                isLoading = state.isNearbyLoading,
                error = state.nearbyError,
                emptyText = "Ainda nao encontramos pratos num raio de ${state.radiusKm.toInt()} km.",
                pratos = state.nearbyPratos,
                onPratoClick = onPratoClick
            )
        }

        if (state.tradicionaisPratos.isNotEmpty()) {
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("Tradicionais na tua zona")
                Spacer(Modifier.height(12.dp))
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.tradicionaisPratos.take(10)) { prato ->
                        NearDishCard(
                            prato = prato,
                            modifier = Modifier.width(220.dp),
                            onClick = { onPratoClick(prato.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NearHeader(
    city: String,
    street: String,
    onRefreshLocation: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = ClayOrangeBolt,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Na Zona", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ClayOnDark)
            Text(city, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ClayOnDarkSecond)
            if (street.isNotBlank()) {
                Text(street, fontSize = 12.sp, color = ClayOnDarkSecond, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        IconButton(onClick = onRefreshLocation) {
            Icon(Icons.Default.MyLocation, contentDescription = "Atualizar localizacao", tint = ClayOrangeBolt)
        }
    }
}

@Composable
private fun RadiusControl(
    radiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    onRadiusFinished: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Raio de pesquisa: ${radiusKm.toInt()} km",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClayOnDarkSecond
        )
        Slider(
            value = radiusKm.toFloat(),
            onValueChange = { onRadiusChange(it.toDouble()) },
            onValueChangeFinished = onRadiusFinished,
            valueRange = 1f..20f,
            steps = 18,
            colors = SliderDefaults.colors(
                thumbColor = ClayOrangeBolt,
                activeTrackColor = ClayOrangeBolt
            )
        )
    }
}

@Composable
private fun NearMapCard(
    userLocation: LatLng,
    radiusKm: Double,
    pratos: List<PratoDto>,
    onPratoClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
    ) {
        val targetZoom = zoomForRadius(radiusKm)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(userLocation, targetZoom)
        }

        LaunchedEffect(userLocation, targetZoom) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, targetZoom)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true,
                compassEnabled = true
            )
        ) {
            MarkerComposable(
                state = androidx.compose.runtime.remember(userLocation) { MarkerState(position = userLocation) },
                title = "A tua localizacao"
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            pratos
                .filter { it.restauranteLatitude != null && it.restauranteLongitude != null }
                .groupBy { it.restauranteId }
                .forEach { (restId, pratosRestaurante) ->
                    val topPrato = pratosRestaurante.minWithOrNull(
                        compareBy<PratoDto> { it.distanciaKm ?: Double.MAX_VALUE }
                            .thenByDescending { it.ratingMedio }
                            .thenByDescending { it.totalAvaliacoes }
                    ) ?: pratosRestaurante.first()
                    val restLat = topPrato.restauranteLatitude
                    val restLng = topPrato.restauranteLongitude
                    if (restLat != null && restLng != null) {
                        DishMapMarker(
                            restId = restId,
                            prato = topPrato,
                            radiusKm = radiusKm,
                            position = LatLng(restLat, restLng),
                            onPratoClick = onPratoClick
                        )
                    }
                }
        }
    }
}

@Composable
private fun DishMapMarker(
    restId: Int,
    prato: PratoDto,
    radiusKm: Double,
    position: LatLng,
    onPratoClick: (Int) -> Unit
) {
    val imageUrl = mapMarkerImageUrl(prato)
    val context = LocalContext.current
    var imageBitmap by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        imageBitmap = null
        if (imageUrl != null) {
            val result = context.imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .allowHardware(false)
                    .build()
            )
            imageBitmap = (result as? SuccessResult)
                ?.drawable
                ?.toBitmap()
                ?.asImageBitmap()
        }
    }

    MarkerComposable(
        keys = arrayOf<Any>(restId, prato.id, radiusKm, imageUrl.orEmpty(), imageBitmap != null),
        state = androidx.compose.runtime.remember(position) { MarkerState(position = position) },
        onClick = {
            onPratoClick(prato.id)
            true
        }
    ) {
        MapDishMarker(prato, radiusKm, imageBitmap)
    }
}

@Composable
private fun MapDishMarker(
    prato: PratoDto,
    radiusKm: Double,
    imageBitmap: ImageBitmap?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val markerSize = mapMarkerSizeForRadius(radiusKm)
        val ratingFontSize = mapMarkerRatingSizeForRadius(radiusKm)
        val ratingText = String.format(Locale.US, "%.1f", prato.ratingMedio)

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
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = prato.nome,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(ClayBluePale.copy(alpha = 0.75f))
                    )
                }
            }
            if (prato.ratingMedio > 0.0) {
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
        Spacer(Modifier.height(3.dp))
        Text(
            text = prato.restauranteNome,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ClayTextDark,
            maxLines = 1,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.96f))
                .border(1.dp, ClayBluePale, RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun MapMarkerImageFallback(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClayBluePale.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.take(1).uppercase(Locale.ROOT),
            color = ClayTextDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun NearbyContent(
    isLoading: Boolean,
    error: String?,
    emptyText: String,
    pratos: List<PratoDto>,
    onPratoClick: (Int) -> Unit
) {
    when {
        isLoading -> Column(Modifier.padding(horizontal = 24.dp)) { SkeletonRow() }
        error != null -> LocationMessage("Erro", error)
        pratos.isEmpty() -> LocationMessage("Sem resultados", emptyText)
        else -> LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(pratos.take(20)) { prato ->
                NearDishCard(
                    prato = prato,
                    modifier = Modifier.width(220.dp),
                    onClick = { onPratoClick(prato.id) }
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = ClayOnDark,
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}

@Composable
private fun LocationMessage(title: String, message: String) {
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        backgroundColor = ClayWhite,
        cornerRadius = 16.dp,
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ClayTextDark)
            Spacer(Modifier.height(4.dp))
            Text(message, fontSize = 13.sp, color = ClayTextMedium)
        }
    }
}

@Composable
private fun NearDishCard(
    prato: PratoDto,
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
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(prato.displayImageUrl())
                    .crossfade(true)
                    .build(),
                contentDescription = prato.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                loading = { NearDishImageFallback(prato.nome) },
                error = { NearDishImageFallback(prato.nome) }
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = prato.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ClayTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = prato.restauranteNome,
                    fontSize = 12.sp,
                    color = ClayTextMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prato.distanciaKm?.let { String.format(Locale.US, "%.1f km", it) } ?: "",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = ClayOrangeBolt
                    )
                    Text(
                        text = prato.preco?.let { String.format(Locale.US, "%.2f EUR", it) } ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ClayTextDark
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", color = Color(0xFFFFC107), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = String.format(Locale.US, "%.1f", prato.ratingMedio),
                        color = ClayTextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${prato.totalAvaliacoes} aval.",
                        color = ClayTextMedium,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NearDishImageFallback(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClayBluePale.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Restaurant,
                contentDescription = null,
                tint = ClayOrangeBolt,
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label.take(18),
                color = ClayTextMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun zoomForRadius(radiusKm: Double): Float {
    val clampedRadius = radiusKm.coerceIn(1.0, 20.0)
    return (15.4 - ln(clampedRadius) / ln(2.0)).toFloat().coerceIn(11.0f, 15.5f)
}

private fun mapMarkerSizeForRadius(radiusKm: Double) =
    (64.0 - radiusKm.coerceIn(1.0, 20.0) * 7.0).coerceIn(26.0, 58.0).dp

private fun mapMarkerRatingSizeForRadius(radiusKm: Double) =
    (10.5 - radiusKm.coerceIn(1.0, 20.0) * 0.45).coerceIn(7.0, 10.0).sp

private fun mapMarkerImageUrl(prato: PratoDto): String? {
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
