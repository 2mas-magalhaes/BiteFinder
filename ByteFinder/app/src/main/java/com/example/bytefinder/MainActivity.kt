package com.example.bytefinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import android.widget.Toast
import androidx.compose.material.icons.rounded.Menu
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Star
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Badge
import coil.compose.AsyncImage
import com.example.bytefinder.data.ApiClient
import com.example.bytefinder.data.ApiService
import com.example.bytefinder.data.AvaliacaoDto
import com.example.bytefinder.data.CreateRespostaRequest
import com.example.bytefinder.data.CreateAvaliacaoRequest
import com.example.bytefinder.data.DeleteAvaliacaoRequest
import com.example.bytefinder.data.LoginRequest
import com.example.bytefinder.data.MyReviewItemDto
import com.example.bytefinder.data.PratoDetailDto
import com.example.bytefinder.data.PratoDto
import com.example.bytefinder.data.UpdatePratoRequest
import com.example.bytefinder.ui.theme.ByteFinderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private enum class AppScreen {
    HOME,
    BUSINESS,
    MY_REVIEWS,
    DETAIL
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = ApiClient.retrofit.create(ApiService::class.java)

        setContent {
            ByteFinderTheme {
                AppRoot(api = api)
            }
        }
    }
}

@Composable
private fun AppRoot(api: ApiService) {
    var token by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var currentUserRole by remember { mutableStateOf<String?>(null) }
    var currentUserRestaurants by remember { mutableStateOf<List<Int>>(emptyList()) }

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var detailReturnScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedPratoId by remember { mutableStateOf<Int?>(null) }

    fun goHome() {
        selectedPratoId = null
        currentScreen = AppScreen.HOME
    }

    fun signOut() {
        token = null
        userName = null
        currentUserId = null
        currentUserRole = null
        currentUserRestaurants = emptyList()
        selectedPratoId = null
        currentScreen = AppScreen.HOME
    }

    if (token == null) {
        LoginScreen(
            api = api,
            onLoggedIn = { t, userId, nome, role, restaurantes ->
                token = t
                currentUserId = userId
                userName = nome
                currentUserRole = role
                currentUserRestaurants = restaurantes
                currentScreen = if (role == "restaurante") AppScreen.BUSINESS else AppScreen.HOME
            }
        )
    } else {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                val direction = if (targetState.ordinal >= initialState.ordinal) 1 else -1
                (slideInHorizontally(
                    animationSpec = tween(durationMillis = 330, easing = FastOutSlowInEasing),
                    initialOffsetX = { direction * (it / 6) }
                ) + fadeIn(animationSpec = tween(260))) togetherWith
                    (slideOutHorizontally(
                        animationSpec = tween(durationMillis = 230, easing = FastOutSlowInEasing),
                        targetOffsetX = { -direction * (it / 8) }
                    ) + fadeOut(animationSpec = tween(190)))
            },
            label = "screen-motion"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        api = api,
                        userName = userName ?: "Utilizador",
                        isRestaurantUser = (currentUserRole == "restaurante"),
                        onPratoClick = { pratoId ->
                            selectedPratoId = pratoId
                            detailReturnScreen = AppScreen.HOME
                            currentScreen = AppScreen.DETAIL
                        },
                        onOpenBusiness = {
                            currentScreen = AppScreen.BUSINESS
                        },
                        onOpenMyReviews = {
                            currentScreen = AppScreen.MY_REVIEWS
                        },
                        onSignOut = { signOut() },
                        onGoHome = { goHome() }
                    )
                }

                AppScreen.BUSINESS -> {
                    RestaurantBusinessScreen(
                        api = api,
                        currentUserId = currentUserId ?: 0,
                        restauranteIds = currentUserRestaurants,
                        onBack = {
                            currentScreen = AppScreen.HOME
                        },
                        onPratoClick = { pratoId ->
                            selectedPratoId = pratoId
                            detailReturnScreen = AppScreen.BUSINESS
                            currentScreen = AppScreen.DETAIL
                        },
                        onGoHome = { goHome() }
                    )
                }

                AppScreen.MY_REVIEWS -> {
                    MyReviewsScreen(
                        api = api,
                        userId = currentUserId ?: 0,
                        onBack = {
                            currentScreen = AppScreen.HOME
                        },
                        onPratoClick = { pratoId ->
                            selectedPratoId = pratoId
                            detailReturnScreen = AppScreen.MY_REVIEWS
                            currentScreen = AppScreen.DETAIL
                        },
                        onGoHome = { goHome() }
                    )
                }

                AppScreen.DETAIL -> {
                    PratoDetailScreen(
                        api = api,
                        pratoId = selectedPratoId ?: 0,
                        currentUserId = currentUserId ?: 0,
                        restaurantIds = currentUserRestaurants,
                        onBack = {
                            currentScreen = detailReturnScreen
                        },
                        onGoHome = { goHome() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThreeDButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val shape = RoundedCornerShape(18.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(120),
        label = "btn-scale"
    )
    val lift by animateDpAsState(
        targetValue = if (pressed) 1.dp else 0.dp,
        animationSpec = tween(120),
        label = "btn-lift"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.45f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier
            .shadow(16.dp, shape = shape, clip = false)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = lift.toPx()
            }
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp
) {
    val shimmer = rememberInfiniteTransition(label = "skeleton")
    val shift by shimmer.animateFloat(
        initialValue = -350f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton-shift"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        start = Offset(shift, 0f),
        end = Offset(shift + 230f, 230f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
private fun DishSkeletonCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp),
                cornerRadius = 14.dp
            )
            Spacer(Modifier.height(12.dp))
            SkeletonBlock(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp), cornerRadius = 8.dp)
            Spacer(Modifier.height(8.dp))
            SkeletonBlock(modifier = Modifier.fillMaxWidth(0.45f).height(12.dp), cornerRadius = 8.dp)
        }
    }
}

@Composable
private fun SimpleListSkeleton(count: Int = 3) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(count) {
            DishSkeletonCard()
        }
    }
}

@Composable
private fun BrandHeader(
    onGoHome: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onGoHome() }
    ) {
        AsyncImage(
            model = "https://bitefinderstorage.blob.core.windows.net/icons/logo-bf.png",
            contentDescription = "Logo BiteFinder",
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(8.dp))

        Column {
            Text(
                "BiteFinder",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                "encontra o prato certo, já",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoginScreen(
    api: ApiService,
    onLoggedIn: (token: String, userId: Int, nome: String, role: String, restaurantes: List<Int>) -> Unit
) {
    var email by remember { mutableStateOf("goncalo@teste.com") }
    var pass by remember { mutableStateOf("123456") }
    var msg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()  // 🔧 NOVO: Respeita o espaço do teclado virtual
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())  // 🔧 NOVO: Scroll se teclado comprime layout
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = "https://bitefinderstorage.blob.core.windows.net/icons/logo-bf.png",
                        contentDescription = "Logo BiteFinder",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "BiteFinder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Encontra o prato certo, já",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(26.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        placeholder = { Text("exemplo@email.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics(mergeDescendants = true) { contentDescription = "Campo de email para login" },
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { /* Foco automático */ }
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Password") },
                        placeholder = { Text("***") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics(mergeDescendants = true) { contentDescription = "Campo de palavra-passe para login" },
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                scope.launch {
                                    try {
                                        val res = api.login(LoginRequest(email, pass))
                                        if (res.ok && res.token != null && res.user != null) {
                                            msg = ""
                                            onLoggedIn(
                                                res.token,
                                                res.user.id,
                                                res.user.nome,
                                                res.user.role,
                                                res.user.restaurantes
                                            )
                                        } else {
                                            msg = res.error ?: res.message ?: "Falha no login"
                                        }
                                    } catch (e: Exception) {
                                        msg = e.message ?: "Erro de rede"
                                    }
                                }
                            }
                        )
                    )

                    Spacer(Modifier.height(18.dp))

                    ThreeDButton(
                        text = "Entrar",
                        onClick = {
                            scope.launch {
                                try {
                                    val res = api.login(LoginRequest(email, pass))
                                    if (res.ok && res.token != null && res.user != null) {
                                        msg = ""
                                        onLoggedIn(
                                            res.token,
                                            res.user.id,
                                            res.user.nome,
                                            res.user.role,
                                            res.user.restaurantes
                                        )
                                    } else {
                                        msg = res.error ?: res.message ?: "Falha no login"
                                    }
                                } catch (e: Exception) {
                                    msg = e.message ?: "Erro de rede"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .semantics(mergeDescendants = true) { contentDescription = "Botão para fazer login" }
                    )

                    AnimatedVisibility(visible = msg.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                        Column {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "Mensagem de erro: $msg" }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Category(val name: String, val imageUrl: String)

@Composable
private fun HomeScreen(
    api: ApiService,
    userName: String,
    isRestaurantUser: Boolean,
    onPratoClick: (Int) -> Unit,
    onOpenBusiness: () -> Unit,
    onOpenMyReviews: () -> Unit,
    onSignOut: () -> Unit,
    onGoHome: () -> Unit
) {
    var categorias by remember { mutableStateOf(listOf("Bifanas", "Francesinha", "Tradicional", "Bacalhau", "Petiscos", "Doces", "Leitão", "Cozido")) }
    var selectedCategory by remember { mutableStateOf("Bifanas") }
    var featuredPratos by remember { mutableStateOf(listOf<PratoDto>()) }
    var featuredMsg by remember { mutableStateOf<String?>(null) }
    var featuredLoading by remember { mutableStateOf(true) }

    val categoryImages = remember {
        mapOf(
            "Todos" to "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&q=80&w=200",
            "Bifanas" to "https://images.unsplash.com/photo-1628191010210-a59de33e5941?auto=format&fit=crop&q=80&w=200",
            "Francesinha" to "https://images.unsplash.com/photo-1544025162-836b9e28e469?auto=format&fit=crop&q=80&w=200",
            "Tradicional" to "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&q=80&w=200",
            "Bacalhau" to "https://images.unsplash.com/photo-1599458252573-56ae36120de1?auto=format&fit=crop&q=80&w=200",
            "Petiscos" to "https://images.unsplash.com/photo-1541544741938-0af808871ccd?auto=format&fit=crop&q=80&w=200",
            "Doces" to "https://images.unsplash.com/photo-1481504225026-669de4a706b8?auto=format&fit=crop&q=80&w=200",
            "Leitão" to "https://images.unsplash.com/photo-1594041680534-e8d9b2db90cf?auto=format&fit=crop&q=80&w=200",
            "Cozido" to "https://images.unsplash.com/photo-1547596009-842cdd0d84c1?auto=format&fit=crop&q=80&w=200"
        )
    }

    fun getCategoryImageUrl(name: String): String {
        return categoryImages[name] ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&q=80&w=200"
    }

    val pratos = remember { androidx.compose.runtime.mutableStateListOf<PratoDto>() }

    var searchQuery by remember { mutableStateOf("") }
    var searchSuggestions by remember { mutableStateOf(listOf<PratoDto>()) }
    var viewAllCategory by remember { mutableStateOf<String?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val res = api.listCategorias()
            if (res.ok && res.items.isNotEmpty()) {
                categorias = res.items
            }
        } catch (_: Exception) {
            // Keep default mock categories
        }
        try {
            val pratosRes = api.listPratos(limit = 100)
            if (pratosRes.ok && pratosRes.items.isNotEmpty()) {
                pratos.clear()
                pratos.addAll(pratosRes.items)
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(selectedCategory) {
        featuredLoading = true
        try {
            val res = api.listPratos(categoria = if (selectedCategory == "Todos") null else selectedCategory, limit = 10, offset = 0)
            if (res.ok && res.items.isNotEmpty()) {
                featuredPratos = res.items
                featuredMsg = null
            } else {
                featuredPratos = pratos.filter { it.categoria == selectedCategory || selectedCategory == "Todos" }
                featuredMsg = null
            }
        } catch (e: Exception) {
            featuredPratos = pratos.filter { it.categoria == selectedCategory || selectedCategory == "Todos" }
            featuredMsg = null
        } finally {
            featuredLoading = false
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.trim().length < 2) {
            searchSuggestions = emptyList()
            return@LaunchedEffect
        }
        delay(250)
        try {
            val res = api.listPratos(search = searchQuery.trim(), limit = 10, offset = 0)
            searchSuggestions = if (res.ok) {
                res.items.distinctBy { it.nome.lowercase(Locale.ROOT) }.take(3)
            } else emptyList()
        } catch (_: Exception) {
            searchSuggestions = emptyList()
        }
    }

    val blueThemeAccent = Color(0xFF2563EB)
    val textPrimary = Color(0xFF0F172A)
    val appBackground = Color.White

    var city by remember { mutableStateOf("Lisboa, Portugal") }
    var street by remember { mutableStateOf("Avenida de Berna 13A") }
    var showLocationDialog by remember { mutableStateOf(false) }
    var tempCity by remember { mutableStateOf(city) }
    var tempStreet by remember { mutableStateOf(street) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground)
            .statusBarsPadding()
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable {
                    tempCity = city
                    tempStreet = street
                    showLocationDialog = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = blueThemeAccent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
                Text(
                    text = street,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Box {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF3F7FD),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            Toast.makeText(context, "Menu em desenvolvimento", Toast.LENGTH_SHORT).show()
                            menuExpanded = true
                        }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = "Menu",
                        tint = blueThemeAccent,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text(userName) }, onClick = {})
                    if (isRestaurantUser) {
                        DropdownMenuItem(
                            text = { Text("Business") },
                            onClick = {
                                menuExpanded = false
                                onOpenBusiness()
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Avaliações") },
                        onClick = {
                            menuExpanded = false
                            onOpenMyReviews()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sign out") },
                        onClick = {
                            menuExpanded = false
                            onSignOut()
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            // SEARCH BAR
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Procurar bifana, francesinha, bacalhau...",
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = textPrimary)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Filter",
                            tint = blueThemeAccent
                        )
                    }
                }

                // Suggestions popup equivalent inline
                AnimatedVisibility(visible = searchSuggestions.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            searchSuggestions.forEachIndexed { index, item ->
                                SearchSuggestionItem(
                                    nome = item.nome,
                                    onClick = {
                                        searchQuery = item.nome
                                        searchSuggestions = emptyList()
                                        onPratoClick(item.id)
                                    }
                                )
                                if (index != searchSuggestions.lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // DYNAMIC CONTENT: Search, View All, or Normal Dashboard
            if (searchQuery.isNotBlank() || viewAllCategory != null) {
                val listToShow = pratos.filter {
                    val matchSearch = if (searchQuery.isNotBlank()) {
                        it.nome.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT)) ||
                        it.restauranteNome.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT))
                    } else true
                    
                    val matchCat = if (viewAllCategory != null) {
                        it.categoria == viewAllCategory || viewAllCategory == "Todos"
                    } else {
                        it.categoria == selectedCategory || selectedCategory == "Todos"
                    }
                    
                    matchSearch && matchCat
                }
                
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        if (searchQuery.isNotBlank()) searchQuery = "" 
                        else viewAllCategory = null 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = textPrimary)
                    }
                    Spacer(Modifier.width(8.dp))
                    val title = if (searchQuery.isNotBlank()) "Resultados da pesquisa" else "Todos - $viewAllCategory"
                    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                Spacer(Modifier.height(16.dp))
                
                if (listToShow.isEmpty()) {
                    Text(
                        text = "Não foram encontrados resultados.",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = Color.Gray
                    )
                } else {
                    val chunkedList = listToShow.chunked(2)
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (rowItems in chunkedList) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                for (prato in rowItems) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        BoltDishCard(prato = prato, onClick = { onPratoClick(prato.id) }, modifier = Modifier.fillMaxWidth())
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            } else {
                // CATEGORIES
                val displayCats = if (categorias.isEmpty()) listOf("Todos", "Bifanas", "Francesinha", "Tradicional", "Bacalhau", "Petiscos", "Doces", "Leitão", "Cozido") else listOf("Todos") + categorias
                
                val categoryObjects = displayCats.map { Category(it, getCategoryImageUrl(it)) }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categoryObjects) { catInfo ->
                        val cat = catInfo.name
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = if (selectedCategory == cat) blueThemeAccent.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .size(72.dp)
                                    .let { modifier ->
                                        if (selectedCategory == cat) {
                                            modifier.background(Color.Transparent, CircleShape)
                                                .padding(2.dp)
                                                .background(Color.Transparent, CircleShape)
                                        } else {
                                            modifier
                                        }
                                    }
                                    .clickable {
                                        selectedCategory = cat
                                    }
                            ) {
                                AsyncImage(
                                    model = catInfo.imageUrl,
                                    contentDescription = cat,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(if (selectedCategory == cat) 4.dp else 0.dp)
                                        .clip(CircleShape)
                                        .fillMaxSize()
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                color = textPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // HORIZONTAL LIST 1: Pedir de novo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sec1Title = if (selectedCategory == "Todos") "As melhores opções perto de ti" else "As melhores $selectedCategory perto de ti"
                    Text(
                        text = sec1Title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "Todos >",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = blueThemeAccent,
                        modifier = Modifier.clickable { viewAllCategory = selectedCategory }
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (featuredLoading) {
                    Column(Modifier.padding(horizontal = 24.dp)) {
                        SimpleListSkeleton(count = 1)
                    }
                } else if (featuredPratos.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(featuredPratos) { prato ->
                            BoltDishCard(prato = prato, modifier = Modifier.width(200.dp), onClick = { onPratoClick(prato.id) })
                        }
                    }
                } else {
                    Text(
                        text = featuredMsg ?: "Ainda não há pratos disponíveis.",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(32.dp))

                // HORIZONTAL LIST 2: Explora as ofertas
                val sec2Title = if (selectedCategory == "Todos") "Populares" else "Outras opções de $selectedCategory"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sec2Title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "Ver todas >",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = blueThemeAccent,
                        modifier = Modifier.clickable { viewAllCategory = selectedCategory }
                    )
                }
                Spacer(Modifier.height(16.dp))

                if (!featuredLoading && featuredPratos.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(featuredPratos.reversed()) { prato ->
                            BoltDishCard(prato = prato, modifier = Modifier.width(200.dp), onClick = { onPratoClick(prato.id) })
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
        
        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Alterar Localização") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempCity,
                            onValueChange = { tempCity = it },
                            label = { Text("Cidade") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tempStreet,
                            onValueChange = { tempStreet = it },
                            label = { Text("Rua") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        city = tempCity
                        street = tempStreet
                        showLocationDialog = false
                    }) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLocationDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun BoltDishCard(prato: PratoDto, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val blueThemeAccent = Color(0xFF2563EB)
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A0F172A), ambientColor = Color(0x080F172A))
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                if (!prato.imagemUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = prato.imagemUrl,
                        contentDescription = prato.nome,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(prato.nome, color = Color.Gray, fontSize = 12.sp)
                    }
                }

                // Add Button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp),
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add",
                        tint = blueThemeAccent,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = prato.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prato.restauranteNome,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = if (prato.preco != null) String.format(Locale.US, "%.2f €", prato.preco) else "-",
                        fontWeight = FontWeight.SemiBold,
                        color = blueThemeAccent,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun SearchSuggestionItem(
    nome: String,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.992f else 1f,
        animationSpec = tween(100),
        label = "suggestion-scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nome,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun DishCard(
    prato: PratoDto,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(120),
        label = "card-scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column {
            if (!prato.imagemUrl.isNullOrBlank()) {
                AsyncImage(
                    model = prato.imagemUrl,
                    contentDescription = prato.nome,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(prato.nome)
                }
            }

            Column(Modifier.padding(12.dp)) {
                Text(prato.nome, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        prato.restauranteNome,
                        style = MaterialTheme.typography.labelSmall
                    )

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                if (prato.preco == null) {
                                    "€"
                                } else {
                                    String.format(Locale.US, "%.2f €", prato.preco)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditPratoDialog(prato: PratoDto, onDismiss: () -> Unit, onSave: (PratoDto) -> Unit) {
    var nome by remember { mutableStateOf(prato.nome) }
    var descricao by remember { mutableStateOf(prato.descricao ?: "") }
    var preco by remember { mutableStateOf(prato.preco?.toString() ?: "") }
    var imagemUrl by remember { mutableStateOf(prato.imagemUrl ?: "") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar prato") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    maxLines = 3
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = preco,
                    onValueChange = { preco = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                    label = { Text("Preço (€)") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = imagemUrl,
                    onValueChange = { imagemUrl = it },
                    label = { Text("URL da imagem") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            ThreeDButton(
                text = "Guardar",
                onClick = {
                    onSave(
                        prato.copy(
                            nome = nome,
                            descricao = descricao,
                            preco = preco.toDoubleOrNull(),
                            imagemUrl = imagemUrl
                        )
                    )
                }
            )
        },
        dismissButton = {
            ThreeDButton(
                text = "Cancelar",
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}

@Composable
private fun RestaurantBusinessScreen(
    api: ApiService,
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

    LaunchedEffect(selectedRestauranteId, refreshKey) {
        val restauranteId = selectedRestauranteId

        if (restauranteId == null) {
            pratos = emptyList()
            avaliacoes = emptyList()
            loading = false
            return@LaunchedEffect
        }

        loading = true
        erro = null

        try {
            val pratosRes = api.listPratos(restauranteId = restauranteId, limit = 100, offset = 0)

            if (!pratosRes.ok) {
                pratos = emptyList()
                avaliacoes = emptyList()
                erro = pratosRes.error ?: pratosRes.message ?: "Erro ao carregar pratos do restaurante"
                return@LaunchedEffect
            }

            pratos = pratosRes.items

            if (pratos.isEmpty()) {
                selectedPratoId = null
                avaliacoes = emptyList()
                return@LaunchedEffect
            }

            if (selectedPratoId == null || pratos.none { it.id == selectedPratoId }) {
                selectedPratoId = pratos.first().id
            }

            val pratoId = selectedPratoId
            if (pratoId != null) {
                val avRes = api.listAvaliacoes(pratoId)
                if (avRes.ok) {
                    avaliacoes = avRes.items
                } else {
                    avaliacoes = emptyList()
                    erro = avRes.error ?: avRes.message ?: "Erro ao carregar comentários"
                }
            }
        } catch (e: Exception) {
            erro = e.message ?: "Erro de rede"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(selectedPratoId, refreshKey) {
        val pratoId = selectedPratoId ?: return@LaunchedEffect
        if (selectedRestauranteId == null) return@LaunchedEffect

        try {
            val avRes = api.listAvaliacoes(pratoId)
            if (avRes.ok) {
                avaliacoes = avRes.items
            }
        } catch (_: Exception) {
        }
    }

    val selectedPrato = pratos.firstOrNull { it.id == selectedPratoId }
    val semRespostaCount = avaliacoes.count { it.respostaTexto.isNullOrBlank() }
    val mediaRating = if (avaliacoes.isEmpty()) 0.0 else avaliacoes.map { it.classificacao }.average()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandHeader(onGoHome = onGoHome)
            Spacer(Modifier.weight(1f))
            AssistChip(onClick = onBack, label = { Text("Voltar") })
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Painel Business",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(
            text = "Gestão profissional de reputação e feedback do restaurante",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(14.dp))

        if (restauranteIds.isEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Este utilizador não está associado a nenhum restaurante.")
                }
            }
            return@Column
        }

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            restauranteIds.forEach { rid ->
                FilterChip(
                    selected = selectedRestauranteId == rid,
                    onClick = {
                        selectedRestauranteId = rid
                        selectedPratoId = null
                        feedback = null
                    },
                    label = { Text("Restaurante #$rid") }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Pratos", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    Text(pratos.size.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Comentários", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    Text(avaliacoes.size.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Sem resposta", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    Text(semRespostaCount.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (loading) {
            SimpleListSkeleton(count = 3)
            return@Column
        }

        if (erro != null) {
            Text(erro!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        if (pratos.isEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Ainda não existem pratos disponíveis neste restaurante.")
                }
            }
            return@Column
        }

        Text(
            text = "Pratos do restaurante",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pratos.forEach { prato ->
                FilterChip(
                    selected = selectedPratoId == prato.id,
                    onClick = {
                        selectedPratoId = prato.id
                        feedback = null
                    },
                    label = { Text(prato.nome) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        var showEditDialog by remember { mutableStateOf(false) }
        if (selectedPrato != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(selectedPrato.nome, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(selectedPrato.restauranteNome, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Text("Rating médio: ${String.format(Locale.US, "%.1f", mediaRating)} ★")
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { onPratoClick(selectedPrato.id) },
                            label = { Text("Ver página do prato") }
                        )
                        AssistChip(
                            onClick = { showEditDialog = true },
                            label = { Text("Editar prato") }
                        )
                    }
                }
            }
        }
        if (showEditDialog && selectedPrato != null) {
            EditPratoDialog(
                prato = selectedPrato,
                onDismiss = { showEditDialog = false },
                onSave = { updatedPrato ->
                    scope.launch {
                        try {
                            val res = api.updatePrato(
                                UpdatePratoRequest(
                                    idPrato = updatedPrato.id,
                                    userId = currentUserId,
                                    restauranteId = updatedPrato.restauranteId,
                                    nome = updatedPrato.nome,
                                    descricao = updatedPrato.descricao,
                                    categoria = updatedPrato.categoria,
                                    preco = updatedPrato.preco,
                                    imagemUrl = updatedPrato.imagemUrl
                                )
                            )

                            if (res.ok) {
                                feedback = res.message ?: "Prato atualizado com sucesso"
                                refreshKey++
                            } else {
                                feedback = res.error ?: res.message ?: "Erro ao atualizar prato"
                            }
                        } catch (e: Exception) {
                            feedback = e.message ?: "Erro de rede"
                        } finally {
                            showEditDialog = false
                        }
                    }
                }
            )
        }


        Spacer(Modifier.height(12.dp))

        Text(
            text = "Comentários e respostas",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(8.dp))

        if (feedback != null) {
            Text(
                text = feedback!!,
                color = if (feedback!!.contains("sucesso", true) || feedback!!.contains("criada", true) || feedback!!.contains("atualizada", true)) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            Spacer(Modifier.height(8.dp))
        }

        if (avaliacoes.isEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Este prato ainda não recebeu comentários.")
                }
            }
            return
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            avaliacoes.forEach { avaliacao ->
                val draft = drafts[avaliacao.idAvaliacao] ?: avaliacao.respostaTexto.orEmpty()
                BusinessReviewItem(
                    avaliacao = avaliacao,
                    draft = draft,
                    onDraftChange = { drafts[avaliacao.idAvaliacao] = it },
                    onSubmit = {
                        scope.launch {
                            try {
                                val texto = (drafts[avaliacao.idAvaliacao] ?: "").trim()
                                if (texto.isBlank()) {
                                    feedback = "Escreve a resposta antes de enviar."
                                    return@launch
                                }

                                val res = api.responderAvaliacao(
                                    CreateRespostaRequest(
                                        idAvaliacao = avaliacao.idAvaliacao,
                                        userId = currentUserId,
                                        texto = texto
                                    )
                                )

                                if (res.ok) {
                                    feedback = res.message ?: "Resposta guardada com sucesso"
                                    refreshKey++
                                } else {
                                    feedback = res.error ?: res.message ?: "Erro ao guardar resposta"
                                }
                            } catch (e: Exception) {
                                feedback = e.message ?: "Erro de rede"
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun BusinessReviewItem(
    avaliacao: AvaliacaoDto,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = avaliacao.autorNome ?: "Cliente",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "★".repeat(avaliacao.classificacao.coerceIn(0, 5)),
                    color = Color(0xFFF59E0B)
                )
            }

            if (!avaliacao.createdAt.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = avaliacao.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(avaliacao.comentario ?: "Sem comentário")

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                label = { Text("Resposta do restaurante") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(Modifier.height(8.dp))

            ThreeDButton(
                text = if (avaliacao.respostaTexto.isNullOrBlank()) "Responder" else "Atualizar resposta",
                onClick = onSubmit,
                enabled = draft.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MyReviewsScreen(
    api: ApiService,
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
            val res = api.listMyAvaliacoes(userId)
            if (res.ok) {
                items = res.items
                erro = null
            } else {
                erro = res.error ?: res.message ?: "Erro ao carregar avaliações"
            }
        } catch (e: Exception) {
            erro = e.message ?: "Erro de rede"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandHeader(onGoHome = onGoHome)
            Spacer(Modifier.weight(1f))
            AssistChip(
                onClick = onBack,
                label = { Text("Voltar") }
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "As minhas avaliações",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(12.dp))

        if (loading) {
            SimpleListSkeleton(count = 3)
            return@Column
        }

        if (erro != null) {
            Text(
                text = erro!!,
                color = MaterialTheme.colorScheme.error
            )
            return@Column
        }

        if (items.isEmpty()) {
            Text("Ainda não comentaste nem avaliaste pratos.")
            return@Column
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPratoClick(item.pratoId) },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
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
                            )
                        }

                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = item.pratoNome,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = item.restauranteNome,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "★".repeat(item.classificacao.coerceIn(0, 5)),
                                color = Color(0xFFF59E0B)
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(item.comentario ?: "")

                            if (!item.createdAt.isNullOrBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = item.createdAt,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PratoDetailScreen(
    api: ApiService,
    pratoId: Int,
    currentUserId: Int,
    restaurantIds: List<Int>,
    onBack: () -> Unit,
    onGoHome: () -> Unit
) {
    var prato by remember { mutableStateOf<PratoDetailDto?>(null) }
    var avaliacoes by remember { mutableStateOf(listOf<AvaliacaoDto>()) }
    var erro by remember { mutableStateOf<String?>(null) }
    var aCarregar by remember { mutableStateOf(true) }

    var ratingSelecionado by remember { mutableIntStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    var feedbackEnvio by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var editingMyReview by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(pratoId, refreshKey) {
        aCarregar = true
        feedbackEnvio = null

        try {
            val detailRes = api.getPratoDetail(pratoId)
            val avaliacoesRes = api.listAvaliacoes(pratoId)

            if (detailRes.ok) {
                prato = detailRes.item
                erro = null
            } else {
                erro = detailRes.error ?: detailRes.message ?: "Erro ao carregar prato"
            }

            avaliacoes = if (avaliacoesRes.ok) avaliacoesRes.items else emptyList()

            if (!avaliacoesRes.ok && erro == null) {
                erro = avaliacoesRes.error ?: avaliacoesRes.message
            }

            val minhaAvaliacao = avaliacoes.firstOrNull { it.userId == currentUserId }

            if (minhaAvaliacao == null) {
                ratingSelecionado = 0
                comentario = ""
                editingMyReview = true
            } else if (!editingMyReview) {
                ratingSelecionado = 0
                comentario = ""
            }
        } catch (e: Exception) {
            erro = e.message ?: "Erro de rede"
        } finally {
            aCarregar = false
        }
    }

    val minhaAvaliacao = avaliacoes.firstOrNull { it.userId == currentUserId }
    val isMyRestaurant = prato?.restauranteId?.let { restaurantIds.contains(it) } == true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (aCarregar) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(12.dp),
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
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandHeader(onGoHome = onGoHome)
                Spacer(Modifier.weight(1f))
                AssistChip(
                    onClick = onBack,
                    label = { Text("Voltar") }
                )
            }

            Spacer(Modifier.height(16.dp))

            if (erro != null && prato == null) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = erro!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                return@Column
            }

            if (prato == null) return@Column

            if (isMyRestaurant) {
                ThreeDButton(
                    text = "Editar prato",
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))
            }

            if (showEditDialog && prato != null) {
                val pratoValue = prato!!
                val basePrato = PratoDto(
                    id = pratoValue.id,
                    nome = pratoValue.nome,
                    categoria = pratoValue.categoria,
                    descricao = pratoValue.descricao,
                    preco = pratoValue.preco,
                    imagemUrl = pratoValue.imagemUrl,
                    restauranteId = pratoValue.restauranteId,
                    restauranteNome = pratoValue.restauranteNome,
                    ratingMedio = pratoValue.ratingMedio,
                    totalAvaliacoes = pratoValue.totalAvaliacoes
                )

                EditPratoDialog(
                    prato = basePrato,
                    onDismiss = { showEditDialog = false },
                    onSave = { updatedPrato ->
                        scope.launch {
                            try {
                                val res = api.updatePrato(
                                    UpdatePratoRequest(
                                        idPrato = updatedPrato.id,
                                        userId = currentUserId,
                                        restauranteId = updatedPrato.restauranteId,
                                        nome = updatedPrato.nome,
                                        descricao = updatedPrato.descricao,
                                        categoria = updatedPrato.categoria,
                                        preco = updatedPrato.preco,
                                        imagemUrl = updatedPrato.imagemUrl
                                    )
                                )
                                if (res.ok) {
                                    feedbackEnvio = res.message ?: "Prato atualizado com sucesso"
                                    prato = pratoValue.copy(
                                        nome = updatedPrato.nome,
                                        descricao = updatedPrato.descricao,
                                        preco = updatedPrato.preco,
                                        imagemUrl = updatedPrato.imagemUrl,
                                        categoria = updatedPrato.categoria
                                    )
                                } else {
                                    feedbackEnvio = res.error ?: res.message ?: "Erro ao atualizar prato"
                                }
                            } catch (e: Exception) {
                                feedbackEnvio = e.message ?: "Erro de rede"
                            } finally {
                                showEditDialog = false
                            }
                        }
                    }
                )

                Spacer(Modifier.height(14.dp))
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
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
                        )
                    }

                    Column(Modifier.padding(20.dp)) {
                        Text(
                            text = prato!!.nome,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "${prato!!.categoria ?: "Prato"} · ${prato!!.restauranteNome}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "★★★★★  ${String.format(Locale.US, "%.1f", prato!!.ratingMedio)} / 5 (${prato!!.totalAvaliacoes} avaliações)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Preço base: ${
                                    if (prato!!.preco == null) "-" else
                                        String.format(Locale.US, "%.2f €", prato!!.preco)
                                }",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(22.dp))

                        Text(
                            text = "Descrição",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = prato!!.descricao ?: "Sem descrição",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(26.dp))

                        if (minhaAvaliacao == null || editingMyReview) {
                            Text(
                                text = if (minhaAvaliacao == null) "Dá a tua opinião" else "Editar a tua opinião",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Spacer(Modifier.height(10.dp))

                            RatingSelector(
                                ratingSelecionado = ratingSelecionado,
                                onRatingSelected = { ratingSelecionado = it }
                            )

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = comentario,
                                onValueChange = { comentario = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Escreve a tua opinião") },
                                minLines = 3,
                                maxLines = 5
                            )

                            Spacer(Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ThreeDButton(
                                    text = if (minhaAvaliacao == null) "Enviar avaliação" else "Atualizar opinião",
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val res = api.createAvaliacao(
                                                    CreateAvaliacaoRequest(
                                                        pratoId = prato!!.id,
                                                        userId = currentUserId,
                                                        classificacao = ratingSelecionado,
                                                        comentario = comentario.trim()
                                                    )
                                                )

                                                if (res.ok) {
                                                    feedbackEnvio = res.message ?: "Avaliação guardada com sucesso."
                                                    editingMyReview = false
                                                    refreshKey++
                                                } else {
                                                    feedbackEnvio = res.error ?: res.message ?: "Erro ao enviar avaliação."
                                                }
                                            } catch (e: Exception) {
                                                feedbackEnvio = e.message ?: "Erro de rede."
                                            }
                                        }
                                    },
                                    enabled = ratingSelecionado in 1..5 && comentario.isNotBlank()
                                )

                                if (minhaAvaliacao != null) {
                                    ThreeDButton(
                                        text = "Cancelar",
                                        onClick = {
                                            editingMyReview = false
                                            ratingSelecionado = 0
                                            comentario = ""
                                        },
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (feedbackEnvio != null) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = feedbackEnvio!!,
                                    color = if (
                                        feedbackEnvio!!.contains("sucesso", true) ||
                                        feedbackEnvio!!.contains("guardada", true) ||
                                        feedbackEnvio!!.contains("atualizada", true)
                                    ) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                                )
                            }

                            Spacer(Modifier.height(26.dp))
                        }

                        Text(
                            text = "Comentários recentes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))

                        if (avaliacoes.isEmpty()) {
                            Text(
                                text = "Ainda não existem comentários.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            avaliacoes.forEachIndexed { index, a ->
                                AvaliacaoItem(
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
                                                val res = api.deleteAvaliacao(
                                                    DeleteAvaliacaoRequest(
                                                        idAvaliacao = a.idAvaliacao,
                                                        userId = currentUserId
                                                    )
                                                )

                                                if (res.ok) {
                                                    feedbackEnvio = res.message ?: "Comentário eliminado com sucesso."
                                                    editingMyReview = false
                                                    ratingSelecionado = 0
                                                    comentario = ""
                                                    refreshKey++
                                                } else {
                                                    feedbackEnvio = res.error ?: res.message ?: "Erro ao eliminar comentário."
                                                }
                                            } catch (e: Exception) {
                                                feedbackEnvio = e.message ?: "Erro de rede."
                                            }
                                        }
                                    }
                                )

                                if (index != avaliacoes.lastIndex) {
                                    Spacer(Modifier.height(12.dp))
                                    HorizontalDivider()
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = prato!!.restauranteNome,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = prato!!.restauranteMorada ?: "Morada indisponível",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    if (prato!!.restauranteLatitude != null && prato!!.restauranteLongitude != null) {
                        EmbeddedMap(
                            lat = prato!!.restauranteLatitude!!,
                            lng = prato!!.restauranteLongitude!!
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Coordenadas indisponíveis")
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (prato!!.restauranteLatitude != null && prato!!.restauranteLongitude != null) {
                        ThreeDButton(
                            text = "Abrir no Maps",
                            onClick = {
                                val lat = prato!!.restauranteLatitude
                                val lng = prato!!.restauranteLongitude
                                val label = Uri.encode(prato!!.restauranteNome)
                                val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun RatingSelector(
    ratingSelecionado: Int,
    onRatingSelected: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (1..5).forEach { estrela ->
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (estrela <= ratingSelecionado) Color(0xFFF59E0B)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onRatingSelected(estrela) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "★",
                    fontSize = 22.sp,
                    color = if (estrela <= ratingSelecionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AvaliacaoItem(
    a: AvaliacaoDto,
    isMine: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = a.autorNome ?: "Utilizador",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "★".repeat(a.classificacao.coerceIn(0, 5)),
                    color = Color(0xFFF59E0B)
                )

                Spacer(Modifier.height(4.dp))

                Text(a.comentario ?: "")

                if (!a.createdAt.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = a.createdAt,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            if (isMine) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Editar",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable { onEdit() }
                    )
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { onDelete() }
                    )
                }
            }
        }

        if (!a.respostaTexto.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = "Resposta do restaurante",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(a.respostaTexto)
                }
            }
        }
    }
}

@Composable
private fun EmbeddedMap(
    lat: Double,
    lng: Double
) {
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    overflow: hidden;
                    background: #f3f4f6;
                }
                #map {
                    width: 100%;
                    height: 100%;
                    border-radius: 18px;
                }
                .leaflet-container {
                    font-family: sans-serif;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([$lat, $lng], 16);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '&copy; OpenStreetMap'
                }).addTo(map);
                L.marker([$lat, $lng]).addTo(map);
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                overScrollMode = WebView.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {}
                loadDataWithBaseURL(
                    "https://localhost/",
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "https://localhost/",
                html,
                "text/html",
                "UTF-8",
                null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(18.dp))
    )
}