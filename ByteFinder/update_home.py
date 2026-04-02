import re
import os

filepath = r'e:\bitefinder\BiteFinder\ByteFinder\app\src\main\java\com\example\bytefinder\MainActivity.kt'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

home_screen_start = content.find('private fun HomeScreen(')
search_suggestion_start = content.find('@Composable\nprivate fun SearchSuggestionItem(')

if search_suggestion_start == -1:
    search_suggestion_start = content.find('private fun SearchSuggestionItem(')

print(home_screen_start, search_suggestion_start)

if home_screen_start != -1 and search_suggestion_start != -1:
    before = content[:home_screen_start]
    after = content[search_suggestion_start:]
    
    new_home = '''@Composable
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
    var featuredLoading by remember { mutableStateOf(true) }

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
        featuredLoading = true
        try {
            val res = api.listPratos(categoria = categoriaSelecionada, limit = 10, offset = 0)
            if (res.ok) {
                featuredPratos = res.items
                featuredMsg = null
            } else {
                featuredPratos = emptyList()
                featuredMsg = res.error ?: res.message ?: "Erro a carregar pratos."
            }
        } catch (e: Exception) {
            featuredPratos = emptyList()
            featuredMsg = e.message ?: "Erro de rede"
        } finally {
            featuredLoading = false
        }
    }

    LaunchedEffect(search) {
        if (search.trim().length < 2) {
            searchSuggestions = emptyList()
            return@LaunchedEffect
        }
        delay(250)
        try {
            val res = api.listPratos(search = search.trim(), limit = 10, offset = 0)
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
                .padding(horizontal = 24.dp, vertical = 12.dp),
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
                    text = "Avenida de Berna 13A",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
                Text(
                    text = "Lisboa, 1050-053",
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
                        .clickable { menuExpanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "User",
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
            // HERO SECTION
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(160.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = blueThemeAccent)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Descobre,\\nsaboreia",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 28.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { }
                        ) {
                            Text(
                                text = "Explorar ofertas ➔",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

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
                            if (search.isEmpty()) {
                                Text(
                                    "Comida, restaurantes, lojas...",
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = search,
                                onValueChange = { search = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = textPrimary)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
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
            }

            Spacer(Modifier.height(24.dp))

            // CATEGORIES
            val displayCats = if (categorias.isEmpty()) listOf("Pizza", "Burger", "Sushi", "Saudável", "Doces") else listOf("Todos") + categorias
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayCats) { cat ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = if (categoriaSelecionada == cat || (cat == "Todos" && categoriaSelecionada == null)) blueThemeAccent.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .size(64.dp)
                                .clickable {
                                    categoriaSelecionada = if (cat == "Todos") null else cat
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = cat,
                                tint = if (categoriaSelecionada == cat || (cat == "Todos" && categoriaSelecionada == null)) blueThemeAccent else Color.Gray,
                                modifier = Modifier
                                    .padding(20.dp)
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
                Text(
                    text = "Pedir de novo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "Todos >",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = blueThemeAccent,
                    modifier = Modifier.clickable {  }
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
                        BoltDishCard(prato = prato, onClick = { onPratoClick(prato.id) })
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explora as ofertas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "Ver todas >",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = blueThemeAccent,
                    modifier = Modifier.clickable {  }
                )
            }
            Spacer(Modifier.height(16.dp))

            if (!featuredLoading && featuredPratos.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(featuredPratos.reversed()) { prato ->
                        BoltDishCard(prato = prato, onClick = { onPratoClick(prato.id) })
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun BoltDishCard(prato: PratoDto, onClick: () -> Unit) {
    val blueThemeAccent = Color(0xFF2563EB)
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .width(200.dp)
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
                
                // Offer Badge
                Surface(
                    shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "-30%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
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
'''
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(before + new_home + '\n\n' + after)
    print("DONE")
else:
    print("FAILED TO FIND POS")
