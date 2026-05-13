package com.example.bytefinder.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bytefinder.BuildConfig
import com.example.bytefinder.data.LocationService
import com.example.bytefinder.data.PratoDto
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
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun NearZoneScreen(
    viewModel: HomeViewModel,
    onPratoClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(context) }
    var locationRequested by remember { mutableStateOf(false) }

    fun loadLocation() {
        scope.launch {
            val loc = locationService.getCurrentLocation()
            if (loc != null) {
                val matched = locationService.matchCity(loc.city)
                viewModel.onLocationDetected(
                    matchedCity = matched,
                    displayCity = "${loc.city}, Portugal",
                    displayStreet = loc.street,
                    lat = loc.latitude,
                    lng = loc.longitude
                )
            } else {
                viewModel.onLocationDetected("Todas", "Portugal", "Localizacao indisponivel")
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            loadLocation()
        } else {
            viewModel.onLocationDetected("Todas", "Portugal", "Permissao de localizacao negada")
        }
    }

    LaunchedEffect(Unit) {
        if (!locationRequested) {
            locationRequested = true
            if (locationService.hasLocationPermission()) {
                loadLocation()
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
                        loadLocation()
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
    val hasMapsKey = BuildConfig.MAPS_API_KEY.isNotBlank() &&
        BuildConfig.MAPS_API_KEY != "YOUR_GOOGLE_MAPS_ANDROID_KEY"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
    ) {
        if (!hasMapsKey) {
            LocationMessage(
                title = "Google Maps por configurar",
                message = "Define MAPS_API_KEY no local.properties e ativa Maps SDK for Android."
            )
            return@Box
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(userLocation, 12f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = false)
        ) {
            MarkerComposable(
                state = MarkerState(position = userLocation),
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

            Circle(
                center = userLocation,
                radius = radiusKm * 1000,
                fillColor = ClayOrangeBolt.copy(alpha = 0.15f),
                strokeColor = ClayOrangeBolt,
                strokeWidth = 2f
            )

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
                        MarkerComposable(
                            keys = arrayOf(restId, topPrato.id),
                            state = MarkerState(position = LatLng(restLat, restLng)),
                            onClick = {
                                onPratoClick(topPrato.id)
                                true
                            }
                        ) {
                            MapDishMarker(topPrato)
                        }
                    }
                }
        }
    }
}

@Composable
private fun MapDishMarker(prato: PratoDto) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, ClayOrangeBolt, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(prato.imagemUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = prato.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
        Text(
            text = prato.restauranteNome,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ClayTextDark,
            maxLines = 1,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(5.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(prato.imagemUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = prato.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
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
            }
        }
    }
}
