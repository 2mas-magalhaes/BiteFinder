package com.example.bytefinder.data

/**
 * MockDataProvider — Fornece dados mock robustos para demonstração offline.
 *
 * Contém restaurantes, pratos, categorias, avaliações e localizações reais
 * de Lisboa, Porto e Coimbra com relações lógicas (IDs, categorias, ratings).
 *
 * Utilizado pelo DataRepository como fallback quando a API não está disponível.
 */
object MockDataProvider {

    // ─── Categorias ─────────────────────────────────────────────────────
    val categorias = listOf(
        "Pizza", "Marisco", "Francesinha", "Hambúrguer",
        "Sushi", "Pasta", "Sobremesas"
    )

    // ─── Imagens por Categoria (URLs Unsplash otimizadas) ───────────────
    val categoryImages = mapOf(
        "Todos" to "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&q=80&w=200",
        "Pizza" to "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&q=80&w=200",
        "Marisco" to "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?auto=format&fit=crop&q=80&w=200",
        "Francesinha" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/330px-Francesinha_Sandwich_%28cropped%29.jpg",
        "Hambúrguer" to "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=200",
        "Sushi" to "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?auto=format&fit=crop&q=80&w=200",
        "Pasta" to "https://images.unsplash.com/photo-1612874742237-6526221588e3?auto=format&fit=crop&q=80&w=200",
        "Sobremesas" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Pastel_de_nata_%2818616473070%29.jpg/330px-Pastel_de_nata_%2818616473070%29.jpg"
    )

    fun getCategoryImageUrl(name: String): String =
        categoryImages[name]
            ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&q=80&w=200"

    // ─── Dados dos Restaurantes ─────────────────────────────────────────

    data class MockRestaurante(
        val id: Int,
        val nome: String,
        val morada: String,
        val cidade: String,
        val zona: String,
        val latitude: Double,
        val longitude: Double
    )

    val restaurantes = listOf(
        MockRestaurante(2, "Pizzaria Luzzo Amadora", "Av. Cruzeiro Seixas 5 e 7", "Amadora", "Centro", 38.7763, -9.2197),
        MockRestaurante(3, "Cervejaria Ramiro", "Av. Almirante Reis 1H", "Lisboa", "Intendente", 38.7223, -9.1353),
        MockRestaurante(5, "Café Santiago", "Rua de Passos Manuel 226", "Porto", "Bolhão", 41.1496, -8.6100),
        MockRestaurante(7, "Pastéis de Belém", "Rua de Belém 84-92", "Lisboa", "Belém", 38.6975, -9.2030),
        MockRestaurante(9, "Hamburgueria do Bairro", "Rua da Madalena 200", "Lisboa", "Baixa", 38.7100, -9.1330),
        MockRestaurante(10, "Sushicafé Avenida", "Rua Barata Salgueiro 28", "Lisboa", "Avenida", 38.7260, -9.1470),
        MockRestaurante(11, "Pasta Non Basta", "Rua do Alecrim 21", "Lisboa", "Chiado", 38.7100, -9.1450)
    )

    // ─── Pratos Mock ────────────────────────────────────────────────────

    val pratos: List<PratoDto> = listOf(
        // Pizza — Pizzaria Luzzo Amadora
        PratoDto(1, "Margherita", "Pizza", "Pizza clássica com molho de tomate, mozzarella e manjericão", 13.20, "https://bitefinderstorage.blob.core.windows.net/pratos/margherita1.png", 2, "Pizzaria Luzzo Amadora", 4.8, 312),
        PratoDto(2, "Pepperoni", "Pizza", "Pizza com pepperoni picante e mozzarella", 15.30, "https://bitefinderstorage.blob.core.windows.net/pratos/pepperoni1.png", 2, "Pizzaria Luzzo Amadora", 4.7, 256),
        PratoDto(3, "Funghi", "Pizza", "Pizza com cogumelos frescos e mozzarella", 15.30, "https://bitefinderstorage.blob.core.windows.net/pratos/funghi.png", 2, "Pizzaria Luzzo Amadora", 4.5, 178),

        // Marisco — Cervejaria Ramiro
        PratoDto(4, "Gambas al Ajillo", "Marisco", "Gambas frescas salteadas em azeite com alho e malagueta", 18.50, "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?auto=format&fit=crop&q=80&w=400", 3, "Cervejaria Ramiro", 4.8, 234),
        PratoDto(5, "Amêijoas à Bulhão Pato", "Marisco", "Amêijoas frescas com azeite, alho, coentros e limão", 22.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg/960px-Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg", 3, "Cervejaria Ramiro", 4.7, 187),
        PratoDto(6, "Lavagante Grelhado", "Marisco", "Lavagante fresco grelhado com manteiga de ervas", 45.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Grilled_Lobster_%288558909573%29.jpg/960px-Grilled_Lobster_%288558909573%29.jpg", 3, "Cervejaria Ramiro", 4.9, 145),

        // Francesinha — Café Santiago
        PratoDto(7, "Francesinha Especial", "Francesinha", "Francesinha com fiambre, linguiça, salsicha fresca, bife e ovo, coberta com queijo e molho especial", 14.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/960px-Francesinha_Sandwich_%28cropped%29.jpg", 5, "Café Santiago", 4.9, 445),
        PratoDto(8, "Mini Francesinha", "Francesinha", "Versão mini da clássica francesinha do Porto", 9.50, "https://images.unsplash.com/photo-1513185158878-8d8c2a2a3da3?auto=format&fit=crop&q=80&w=400", 5, "Café Santiago", 4.6, 178),

        // Hambúrguer — Hamburgueria do Bairro
        PratoDto(9, "Classic Smash Burger", "Hambúrguer", "Dois smash patties de 90g, queijo cheddar, pickles, cebola caramelizada e molho especial", 12.50, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=400", 9, "Hamburgueria do Bairro", 4.7, 201),
        PratoDto(10, "Bacon Cheese Burger", "Hambúrguer", "Burger 180g com bacon crocante, queijo cheddar derretido, alface e tomate", 14.00, "https://images.unsplash.com/photo-1553979459-d2229ba7433b?auto=format&fit=crop&q=80&w=400", 9, "Hamburgueria do Bairro", 4.5, 156),
        PratoDto(11, "Truffle Burger", "Hambúrguer", "Burger 200g com queijo brie, rúcula, cogumelos e maionese de trufa", 16.00, "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&q=80&w=400", 9, "Hamburgueria do Bairro", 4.8, 89),

        // Sushi — Sushicafé Avenida
        PratoDto(12, "Nigiri de Salmão (6 pcs)", "Sushi", "Seis peças de nigiri com salmão fresco do Atlântico", 14.00, "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?auto=format&fit=crop&q=80&w=400", 10, "Sushicafé Avenida", 4.6, 134),
        PratoDto(13, "Dragon Roll (8 pcs)", "Sushi", "Roll especial com camarão tempura, abacate, enguia e tobiko", 16.50, "https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?auto=format&fit=crop&q=80&w=400", 10, "Sushicafé Avenida", 4.7, 98),
        PratoDto(14, "Sashimi Misto", "Sushi", "Seleção de 15 fatias de sashimi: salmão, atum e robalo", 22.00, "https://images.unsplash.com/photo-1534482421-64566f976cfa?auto=format&fit=crop&q=80&w=400", 10, "Sushicafé Avenida", 4.8, 167),

        // Pasta — Pasta Non Basta
        PratoDto(15, "Carbonara", "Pasta", "Spaghetti com guanciale, ovo, pecorino romano e pimenta preta", 13.50, "https://images.unsplash.com/photo-1612874742237-6526221588e3?auto=format&fit=crop&q=80&w=400", 11, "Pasta Non Basta", 4.5, 99),
        PratoDto(16, "Lasagna della Casa", "Pasta", "Lasanha tradicional com ragù de carne, béchamel e parmigiano", 13.00, "https://images.unsplash.com/photo-1574894709920-11b28e7367e3?auto=format&fit=crop&q=80&w=400", 11, "Pasta Non Basta", 4.6, 112),
        PratoDto(17, "Penne all'Arrabbiata", "Pasta", "Penne com molho de tomate picante, alho e salsa", 11.50, "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?auto=format&fit=crop&q=80&w=400", 11, "Pasta Non Basta", 4.3, 78),

        // Sobremesas — Pastéis de Belém
        PratoDto(18, "Pastel de Belém", "Sobremesas", "O original pastel de nata de Belém com canela e açúcar em pó", 1.30, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Pastel_de_nata_%2818616473070%29.jpg/960px-Pastel_de_nata_%2818616473070%29.jpg", 7, "Pastéis de Belém", 4.9, 523),
        PratoDto(19, "Tarte de Amêndoa", "Sobremesas", "Tarte crocante de amêndoa do Algarve com gelado de baunilha", 4.50, "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?auto=format&fit=crop&q=80&w=400", 7, "Pastéis de Belém", 4.5, 198),
        PratoDto(20, "Mousse de Chocolate", "Sobremesas", "Mousse de chocolate negro belga com raspas de chocolate", 5.00, "https://images.unsplash.com/photo-1541783245831-57d6fb0926d3?auto=format&fit=crop&q=80&w=400", 7, "Pastéis de Belém", 4.7, 145)
    )

    // ─── Detalhes dos Pratos ────────────────────────────────────────────

    fun getPratoDetail(id: Int): PratoDetailDto? {
        val prato = pratos.find { it.id == id } ?: return null
        val rest = restaurantes.find { it.id == prato.restauranteId } ?: return null
        return PratoDetailDto(
            id = prato.id,
            nome = prato.nome,
            categoria = prato.categoria,
            descricao = prato.descricao,
            preco = prato.preco,
            imagemUrl = prato.imagemUrl,
            disponivel = true,
            restauranteId = rest.id,
            restauranteNome = rest.nome,
            restauranteMorada = "${rest.morada}, ${rest.cidade}",
            restauranteLatitude = rest.latitude,
            restauranteLongitude = rest.longitude,
            ratingMedio = prato.ratingMedio,
            totalAvaliacoes = prato.totalAvaliacoes
        )
    }

    // ─── Avaliações Mock ────────────────────────────────────────────────

    val avaliacoes: Map<Int, List<AvaliacaoDto>> = mapOf(
        1 to listOf(
            AvaliacaoDto(101, 10, "Maria S.", 5, "A melhor bifana que já comi! Carne tenra e pão quentinho.", "2025-03-15", null, null, null, null),
            AvaliacaoDto(102, 11, "João P.", 4, "Muito boa, mas achei o pão um pouco seco.", "2025-03-10", null, 201, "Obrigado João! Vamos melhorar o pão.", "2025-03-11"),
            AvaliacaoDto(103, 12, "Ana R.", 5, "Venho aqui todas as semanas. Nunca desilude!", "2025-02-28", null, null, null, null)
        ),
        3 to listOf(
            AvaliacaoDto(104, 10, "Carlos M.", 5, "Molho de cerveja preta é genial! Nota 10.", "2025-04-01", null, null, null, null),
            AvaliacaoDto(105, 13, "Sofia L.", 5, "Vale a viagem ao Porto só por esta bifana.", "2025-03-22", null, null, null, null)
        ),
        5 to listOf(
            AvaliacaoDto(106, 10, "Pedro G.", 5, "A MELHOR francesinha do mundo. Ponto final.", "2025-04-05", null, null, null, null),
            AvaliacaoDto(107, 14, "Rita C.", 4, "Muito boa, mas a fila de espera é enorme.", "2025-03-28", null, 202, "Obrigado Rita! Recomendamos reservar mesa.", "2025-03-29"),
            AvaliacaoDto(108, 15, "Miguel A.", 5, "Molho é absurdamente bom. Voltarei sempre.", "2025-03-20", null, null, null, null),
            AvaliacaoDto(109, 11, "Beatriz F.", 4, "Muito saborosa. Porções generosas!", "2025-03-15", null, null, null, null)
        ),
        7 to listOf(
            AvaliacaoDto(110, 12, "Tiago R.", 5, "É a francesinha que toda a gente procura.", "2025-04-08", null, null, null, null)
        ),
        11 to listOf(
            AvaliacaoDto(111, 13, "Luísa M.", 5, "O melhor Bacalhau à Brás de Lisboa!", "2025-03-18", null, null, null, null),
            AvaliacaoDto(112, 10, "André S.", 4, "Muito bom. Batata palha sempre crocante.", "2025-03-12", null, null, null, null)
        ),
        17 to listOf(
            AvaliacaoDto(113, 10, "Emma W.", 5, "Best pastéis de nata in the world!", "2025-04-10", null, null, null, null),
            AvaliacaoDto(114, 14, "François D.", 5, "Magnifique! A must-visit in Lisbon.", "2025-04-07", null, null, null, null),
            AvaliacaoDto(115, 11, "Clara V.", 5, "Quentinhos e crocantes. Perfeição.", "2025-04-02", null, null, null, null),
            AvaliacaoDto(116, 15, "Hugo T.", 4, "Excelentes, mas a fila é insuportável.", "2025-03-30", null, 203, "Sugerimos vir de manhã cedo! Obrigado.", "2025-03-31"),
            AvaliacaoDto(117, 12, "Sara N.", 5, "O melhor pastel de nata do universo.", "2025-03-25", null, null, null, null)
        ),
        20 to listOf(
            AvaliacaoDto(118, 13, "Rui B.", 5, "Pele estaladiça como deve ser! Magnífico.", "2025-04-06", null, null, null, null),
            AvaliacaoDto(119, 10, "Inês P.", 5, "Vale cada cêntimo. Carne suculenta.", "2025-03-29", null, null, null, null)
        ),
        23 to listOf(
            AvaliacaoDto(120, 14, "Diogo L.", 5, "Amêijoas perfeitas! Coentros na dose certa.", "2025-04-09", null, null, null, null),
            AvaliacaoDto(121, 15, "Catarina F.", 4, "Muito frescas. Preço justo para a qualidade.", "2025-04-03", null, null, null, null),
            AvaliacaoDto(122, 11, "Nuno G.", 5, "A Ramiro nunca falha. Sempre TOP.", "2025-03-26", null, null, null, null)
        ),
        26 to listOf(
            AvaliacaoDto(123, 12, "Mariana C.", 4, "Boa opção vegetariana em Lisboa.", "2025-04-04", null, null, null, null),
            AvaliacaoDto(124, 10, "Tomás H.", 5, "Surpreendente! Sabores muito bem combinados.", "2025-03-31", null, null, null, null)
        )
    )

    fun getAvaliacoes(pratoId: Int): List<AvaliacaoDto> = avaliacoes[pratoId] ?: emptyList()

    // ─── Zonas disponíveis para filtros ─────────────────────────────────

    val cidades = listOf("Todas", "Lisboa", "Porto", "Coimbra")

    val zonasPorCidade = mapOf(
        "Lisboa" to listOf("Todas", "Alfama", "Baixa", "Belém", "Chiado", "Intendente", "Príncipe Real", "Restauradores"),
        "Porto" to listOf("Todas", "Bolhão", "Ribeira"),
        "Coimbra" to listOf("Todas", "Alta")
    )

    // ─── Faixas de preço para filtros ───────────────────────────────────

    data class PriceRange(val label: String, val min: Double, val max: Double)

    val priceRanges = listOf(
        PriceRange("Todos", 0.0, 999.0),
        PriceRange("< 5€", 0.0, 5.0),
        PriceRange("5€ - 10€", 5.0, 10.0),
        PriceRange("10€ - 15€", 10.0, 15.0),
        PriceRange("> 15€", 15.0, 999.0)
    )

    // ─── Algoritmo de filtragem multi-critério ──────────────────────────

    /**
     * Filtra pratos com base em múltiplos critérios simultâneos.
     * Aplica interseção lógica: TODOS os filtros ativos devem ser satisfeitos.
     *
     * @param categoria Categoria selecionada (null ou "Todos" = sem filtro)
     * @param cidade Cidade selecionada (null ou "Todas" = sem filtro)
     * @param zona Zona dentro da cidade (null ou "Todas" = sem filtro)
     * @param priceRange Faixa de preço (null = sem filtro)
     * @param searchQuery Texto de pesquisa livre (nome do prato ou restaurante)
     * @param minRating Rating mínimo (0 = sem filtro)
     * @return Lista filtrada e ordenada por rating descendente
     */
    fun filterPratos(
        categoria: String? = null,
        cidade: String? = null,
        zona: String? = null,
        priceRange: PriceRange? = null,
        searchQuery: String? = null,
        minRating: Double = 0.0
    ): List<PratoDto> {
        return pratos.filter { prato ->
            val rest = restaurantes.find { it.id == prato.restauranteId }

            // Filtro de categoria
            val matchCategoria = categoria.isNullOrBlank() || categoria == "Todos" ||
                    prato.categoria.equals(categoria, ignoreCase = true)

            // Filtro de cidade
            val matchCidade = cidade.isNullOrBlank() || cidade == "Todas" ||
                    rest?.cidade.equals(cidade, ignoreCase = true)

            // Filtro de zona
            val matchZona = zona.isNullOrBlank() || zona == "Todas" ||
                    rest?.zona.equals(zona, ignoreCase = true)

            // Filtro de preço
            val matchPreco = priceRange == null || priceRange.label == "Todos" ||
                    (prato.preco != null && prato.preco >= priceRange.min && prato.preco < priceRange.max)

            // Filtro de pesquisa textual (nome do prato OU restaurante)
            val query = searchQuery?.trim()?.lowercase()
            val matchSearch = query.isNullOrBlank() ||
                    prato.nome.lowercase().contains(query) ||
                    prato.restauranteNome.lowercase().contains(query) ||
                    (prato.descricao?.lowercase()?.contains(query) == true)

            // Filtro de rating mínimo
            val matchRating = prato.ratingMedio >= minRating

            // Interseção: TODOS os filtros devem passar
            matchCategoria && matchCidade && matchZona && matchPreco && matchSearch && matchRating
        }.sortedByDescending { it.ratingMedio }
    }

    // ─── Sugestões de pesquisa ──────────────────────────────────────────

    /**
     * Retorna sugestões de pesquisa baseadas no input do utilizador.
     * Deduplicadas por nome, limitadas a 5 resultados.
     */
    fun getSearchSuggestions(query: String): List<PratoDto> {
        if (query.trim().length < 2) return emptyList()
        val q = query.trim().lowercase()
        return pratos
            .filter { it.nome.lowercase().contains(q) || it.restauranteNome.lowercase().contains(q) }
            .distinctBy { it.nome.lowercase() }
            .take(5)
    }

    // ─── Mock de login ──────────────────────────────────────────────────

    fun mockLogin(email: String, password: String): LoginResponse {
        return when {
            email == "goncalo@teste.com" && password == "123456" -> LoginResponse(
                ok = true,
                token = "mock_jwt_token_goncalo_2025",
                user = UserDto(1, "Gonçalo", "cliente", emptyList()),
                error = null,
                csrf_token = "mock_csrf_token"
            )
            email == "restaurante@teste.com" && password == "123456" -> LoginResponse(
                ok = true,
                token = "mock_jwt_token_rest_2025",
                user = UserDto(2, "Chef Manuel", "restaurante", listOf(1, 6)),
                error = null,
                csrf_token = "mock_csrf_token"
            )
            email.isBlank() || password.isBlank() -> LoginResponse(
                ok = false, token = null, user = null,
                error = "Email e password são obrigatórios"
            )
            else -> LoginResponse(
                ok = false, token = null, user = null,
                error = "Credenciais inválidas"
            )
        }
    }

    // ─── Mock de avaliações do utilizador ────────────────────────────────

    fun getMyReviews(userId: Int): List<MyReviewItemDto> {
        val result = mutableListOf<MyReviewItemDto>()
        avaliacoes.forEach { (pratoId, reviews) ->
            reviews.filter { it.userId == userId }.forEach { review ->
                val prato = pratos.find { it.id == pratoId }
                if (prato != null) {
                    result.add(
                        MyReviewItemDto(
                            idAvaliacao = review.idAvaliacao,
                            pratoId = pratoId,
                            pratoNome = prato.nome,
                            pratoImagemUrl = prato.imagemUrl,
                            restauranteNome = prato.restauranteNome,
                            classificacao = review.classificacao,
                            comentario = review.comentario,
                            createdAt = review.createdAt
                        )
                    )
                }
            }
        }
        return result.sortedByDescending { it.createdAt }
    }
}
