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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

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
            MaterialTheme {
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
        when (currentScreen) {
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
                color = Color(0xFF005E8A)
            )
            Text(
                "encontra o prato certo, já",
                style = MaterialTheme.typography.labelSmall
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
            .background(Color(0xFFF3F4F6))
            .statusBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
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
                    color = Color(0xFF005E8A)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Encontra o prato certo, já",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(26.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(18.dp))

                Button(
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
                        .height(52.dp),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "Entrar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (msg.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

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
    var categorias by remember { mutableStateOf(listOf<String>()) }
    var categoriaSelecionada by remember { mutableStateOf<String?>(null) }

    var featuredPratos by remember { mutableStateOf(listOf<PratoDto>()) }
    var featuredMsg by remember { mutableStateOf<String?>(null) }

    var search by remember { mutableStateOf("") }
    var searchSuggestions by remember { mutableStateOf(listOf<PratoDto>()) }

    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val res = api.listCategorias()
            categorias = if (res.ok) res.items else emptyList()
        } catch (_: Exception) {
            categorias = emptyList()
        }
    }

    LaunchedEffect(categoriaSelecionada) {
        try {
            val res = api.listPratos(
                categoria = categoriaSelecionada,
                limit = 10,
                offset = 0
            )
            if (res.ok) {
                featuredPratos = res.items
                featuredMsg = null
            } else {
                featuredPratos = emptyList()
                featuredMsg = res.error ?: res.message ?: "Erro a carregar pratos em destaque"
            }
        } catch (e: Exception) {
            featuredPratos = emptyList()
            featuredMsg = e.message ?: "Erro de rede"
        }
    }

    LaunchedEffect(search) {
        if (search.trim().length < 2) {
            searchSuggestions = emptyList()
            return@LaunchedEffect
        }

        delay(250)

        try {
            val res = api.listPratos(
                search = search.trim(),
                limit = 10,
                offset = 0
            )

            searchSuggestions = if (res.ok) {
                res.items
                    .distinctBy { it.nome.lowercase(Locale.ROOT) }
                    .take(3)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            searchSuggestions = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .statusBarsPadding()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandHeader(onGoHome = onGoHome)

            Spacer(Modifier.weight(1f))

            Box {
                AssistChip(
                    onClick = { menuExpanded = true },
                    label = { Text(userName) }
                )

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
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

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Escolha o seu prato") },
            leadingIcon = { Text("🔍") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        if (searchSuggestions.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    searchSuggestions.forEachIndexed { index, item ->
                        SearchSuggestionItem(
                            nome = item.nome,
                            onClick = {
                                search = item.nome
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

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = categoriaSelecionada == null,
                onClick = { categoriaSelecionada = null },
                label = { Text("Todos") }
            )

            categorias.forEach { cat ->
                FilterChip(
                    selected = categoriaSelecionada == cat,
                    onClick = { categoriaSelecionada = cat },
                    label = { Text(cat) }
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Pratos em destaque",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        if (featuredMsg != null) {
            Text(
                text = featuredMsg!!,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(8.dp))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            featuredPratos.forEach { p ->
                DishCard(
                    prato = p,
                    onClick = { onPratoClick(p.id) }
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SearchSuggestionItem(
    nome: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp)
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
                        .background(Color(0xFFD9D2DC)),
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
            Button(onClick = {
                onSave(prato.copy(
                    nome = nome,
                    descricao = descricao,
                    preco = preco.toDoubleOrNull(),
                    imagemUrl = imagemUrl
                ))
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancelar") }
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
            .background(Color(0xFFF3F4F6))
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
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.height(14.dp))

        if (restauranteIds.isEmpty()) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
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
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Pratos", color = Color(0xFF6B7280), style = MaterialTheme.typography.labelSmall)
                    Text(pratos.size.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Comentários", color = Color(0xFF6B7280), style = MaterialTheme.typography.labelSmall)
                    Text(avaliacoes.size.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Sem resposta", color = Color(0xFF6B7280), style = MaterialTheme.typography.labelSmall)
                    Text(semRespostaCount.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (loading) {
            CircularProgressIndicator()
            return@Column
        }

        if (erro != null) {
            Text(erro!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        if (pratos.isEmpty()) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
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
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(selectedPrato.nome, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(selectedPrato.restauranteNome, color = Color(0xFF6B7280))
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
                    Color(0xFF0F9D58)
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            Spacer(Modifier.height(8.dp))
        }

        if (avaliacoes.isEmpty()) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
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
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
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
                    color = Color(0xFFFF9800)
                )
            }

            if (!avaliacao.createdAt.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = avaliacao.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF)
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

            Button(
                onClick = onSubmit,
                enabled = draft.isNotBlank(),
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(if (avaliacao.respostaTexto.isNullOrBlank()) "Responder" else "Atualizar resposta")
            }
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
            .background(Color(0xFFF3F4F6))
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
            CircularProgressIndicator()
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
                    shape = RoundedCornerShape(20.dp)
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
                                color = Color(0xFF6B7280)
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "★".repeat(item.classificacao.coerceIn(0, 5)),
                                color = Color(0xFFFF9800)
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(item.comentario ?: "")

                            if (!item.createdAt.isNullOrBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = item.createdAt,
                                    color = Color(0xFF9CA3AF),
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
            .background(Color(0xFFF3F4F6))
    ) {
        if (aCarregar) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
                    modifier = Modifier.fillMaxWidth()
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
                Button(
                    onClick = { showEditDialog = true },
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar prato")
                }

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
                modifier = Modifier.fillMaxWidth()
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
                            color = Color(0xFF5F6368)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "★★★★★  ${String.format(Locale.US, "%.1f", prato!!.ratingMedio)} / 5 (${prato!!.totalAvaliacoes} avaliações)",
                            color = Color(0xFF4B5563)
                        )

                        Spacer(Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFD7EEF8))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Preço base: ${
                                    if (prato!!.preco == null) "-" else
                                        String.format(Locale.US, "%.2f €", prato!!.preco)
                                }",
                                color = Color(0xFF005E8A),
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
                            color = Color(0xFF374151),
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
                                Button(
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
                                    enabled = ratingSelecionado in 1..5 && comentario.isNotBlank(),
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        if (minhaAvaliacao == null) "Enviar avaliação" else "Atualizar opinião"
                                    )
                                }

                                if (minhaAvaliacao != null) {
                                    Button(
                                        onClick = {
                                            editingMyReview = false
                                            ratingSelecionado = 0
                                            comentario = ""
                                        },
                                        shape = RoundedCornerShape(999.dp)
                                    ) {
                                        Text("Cancelar")
                                    }
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
                                        Color(0xFF0F9D58)
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
                                color = Color(0xFF6B7280)
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
                modifier = Modifier.fillMaxWidth()
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
                        color = Color(0xFF374151),
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
                                .background(Color(0xFFE5E7EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Coordenadas indisponíveis")
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (prato!!.restauranteLatitude != null && prato!!.restauranteLongitude != null) {
                        Button(
                            onClick = {
                                val lat = prato!!.restauranteLatitude
                                val lng = prato!!.restauranteLongitude
                                val label = Uri.encode(prato!!.restauranteNome)
                                val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text("Abrir no Maps")
                        }
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
                        if (estrela <= ratingSelecionado) Color(0xFFFFC107)
                        else Color(0xFFE5E7EB)
                    )
                    .clickable { onRatingSelected(estrela) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "★",
                    fontSize = 22.sp,
                    color = if (estrela <= ratingSelecionado) Color.White else Color(0xFF6B7280)
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
                    color = Color(0xFFFF9800)
                )

                Spacer(Modifier.height(4.dp))

                Text(a.comentario ?: "")

                if (!a.createdAt.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = a.createdAt,
                        color = Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            if (isMine) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Editar",
                        color = Color(0xFF005E8A),
                        modifier = Modifier.clickable { onEdit() }
                    )
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFD93025),
                        modifier = Modifier.clickable { onDelete() }
                    )
                }
            }
        }

        if (!a.respostaTexto.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
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