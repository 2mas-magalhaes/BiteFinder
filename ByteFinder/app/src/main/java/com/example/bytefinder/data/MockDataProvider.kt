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
        "Bifanas", "Francesinha", "Tradicional", "Bacalhau",
        "Petiscos", "Doces", "Leitão", "Cozido", "Marisco", "Vegetariano"
    )

    // ─── Imagens por Categoria (URLs Unsplash otimizadas) ───────────────
    val categoryImages = mapOf(
        "Todos" to "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&q=80&w=200",
        "Bifanas" to "https://images.unsplash.com/photo-1628191010210-a59de33e5941?auto=format&fit=crop&q=80&w=200",
        "Francesinha" to "https://images.unsplash.com/photo-1544025162-836b9e28e469?auto=format&fit=crop&q=80&w=200",
        "Tradicional" to "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&q=80&w=200",
        "Bacalhau" to "https://images.unsplash.com/photo-1599458252573-56ae36120de1?auto=format&fit=crop&q=80&w=200",
        "Petiscos" to "https://images.unsplash.com/photo-1541544741938-0af808871ccd?auto=format&fit=crop&q=80&w=200",
        "Doces" to "https://images.unsplash.com/photo-1481504225026-669de4a706b8?auto=format&fit=crop&q=80&w=200",
        "Leitão" to "https://images.unsplash.com/photo-1594041680534-e8d9b2db90cf?auto=format&fit=crop&q=80&w=200",
        "Cozido" to "https://images.unsplash.com/photo-1547596009-842cdd0d84c1?auto=format&fit=crop&q=80&w=200",
        "Marisco" to "https://images.unsplash.com/photo-1565680018434-b513d5e1051e?auto=format&fit=crop&q=80&w=200",
        "Vegetariano" to "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&q=80&w=200"
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
        MockRestaurante(1, "Casa das Bifanas", "Rua Augusta 42", "Lisboa", "Baixa", 38.7108, -9.1368),
        MockRestaurante(2, "O Velho Eurico", "Largo São Cristóvão 3", "Lisboa", "Alfama", 38.7139, -9.1310),
        MockRestaurante(3, "Cervejaria Ramiro", "Av. Almirante Reis 1", "Lisboa", "Intendente", 38.7213, -9.1353),
        MockRestaurante(4, "Taberna da Rua das Flores", "Rua das Flores 340", "Porto", "Ribeira", 41.1450, -8.6150),
        MockRestaurante(5, "Café Santiago", "Rua Passos Manuel 226", "Porto", "Bolhão", 41.1496, -8.6050),
        MockRestaurante(6, "Solar dos Presuntos", "Rua das Portas de Sto Antão 150", "Lisboa", "Restauradores", 38.7162, -9.1420),
        MockRestaurante(7, "Pastéis de Belém", "Rua de Belém 84", "Lisboa", "Belém", 38.6975, -9.2031),
        MockRestaurante(8, "A Cozinha do Martinho", "Rua Larga 18", "Coimbra", "Alta", 40.2085, -8.4265),
        MockRestaurante(9, "Tasca do Chico", "Rua dos Remédios 83", "Lisboa", "Alfama", 38.7125, -9.1275),
        MockRestaurante(10, "Cantinho do Avillez", "Rua dos Duques de Bragança 7", "Lisboa", "Chiado", 38.7095, -9.1440),
        MockRestaurante(11, "Marisqueira Nunes", "Rua Bartolomeu Dias 112", "Lisboa", "Belém", 38.6960, -9.2060),
        MockRestaurante(12, "O Botanista", "Rua da Mãe d'Água 49", "Lisboa", "Príncipe Real", 38.7180, -9.1510)
    )

    // ─── Pratos Mock ────────────────────────────────────────────────────

    val pratos: List<PratoDto> = listOf(
        // Bifanas — Espalhadas por Lisboa e Porto
        PratoDto(1, "Bifana Clássica", "Bifanas", "Bifana de porco marinada em massa de pimentão, servida em pão crocante", 3.50, "https://images.unsplash.com/photo-1628191010210-a59de33e5941?auto=format&fit=crop&q=80&w=400", 1, "Casa das Bifanas", 4.7, 128),
        PratoDto(2, "Bifana Especial com Queijo", "Bifanas", "Bifana com queijo da Serra derretido e molho picante da casa", 4.50, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=400", 2, "O Velho Eurico", 4.3, 85),
        PratoDto(3, "Bifana do Porto", "Bifanas", "Receita tradicional portuense com molho de cerveja preta", 3.80, "https://images.unsplash.com/photo-1551782450-a2132b4ba21d?auto=format&fit=crop&q=80&w=400", 4, "Taberna da Rua das Flores", 4.9, 201),
        PratoDto(4, "Mini Bifanas (6 un.)", "Bifanas", "Mini bifanas perfeitas para petiscar, com molho à parte", 6.00, "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&q=80&w=400", 9, "Tasca do Chico", 4.1, 67),

        // Francesinhas
        PratoDto(5, "Francesinha Original", "Francesinha", "A verdadeira francesinha do Porto com molho secreto", 12.50, "https://images.unsplash.com/photo-1544025162-836b9e28e469?auto=format&fit=crop&q=80&w=400", 5, "Café Santiago", 4.8, 312),
        PratoDto(6, "Francesinha Vegetariana", "Francesinha", "Versão vegetariana com seitan e cogumelos, molho de tomate", 11.00, "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&q=80&w=400", 4, "Taberna da Rua das Flores", 4.2, 54),
        PratoDto(7, "Francesinha Especial Santiago", "Francesinha", "Com ovo estrelado, batata frita caseira e molho extra", 14.00, "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&q=80&w=400", 5, "Café Santiago", 4.9, 445),

        // Tradicional
        PratoDto(8, "Cozido à Portuguesa", "Tradicional", "Cozido completo com carnes variadas, enchidos, legumes e arroz", 15.00, "https://images.unsplash.com/photo-1547596009-842cdd0d84c1?auto=format&fit=crop&q=80&w=400", 6, "Solar dos Presuntos", 4.6, 178),
        PratoDto(9, "Arroz de Pato", "Tradicional", "Arroz de pato no forno com chouriço e bacon crocante", 13.50, "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&q=80&w=400", 10, "Cantinho do Avillez", 4.5, 134),
        PratoDto(10, "Feijoada Transmontana", "Tradicional", "Feijoada rica com orelha, pé e enchidos de Trás-os-Montes", 12.00, "https://images.unsplash.com/photo-1574484284002-952d92456975?auto=format&fit=crop&q=80&w=400", 8, "A Cozinha do Martinho", 4.4, 89),

        // Bacalhau
        PratoDto(11, "Bacalhau à Brás", "Bacalhau", "Bacalhau desfiado com batata palha, ovo e azeitonas", 14.00, "https://images.unsplash.com/photo-1599458252573-56ae36120de1?auto=format&fit=crop&q=80&w=400", 6, "Solar dos Presuntos", 4.7, 215),
        PratoDto(12, "Bacalhau com Natas", "Bacalhau", "Gratinado de bacalhau com natas dourado no forno", 13.00, "https://images.unsplash.com/photo-1467003909585-2f8a72700288?auto=format&fit=crop&q=80&w=400", 2, "O Velho Eurico", 4.5, 99),
        PratoDto(13, "Pastéis de Bacalhau (6 un.)", "Bacalhau", "Pastéis crocantes de bacalhau da avó, fritos na hora", 7.50, "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&q=80&w=400", 9, "Tasca do Chico", 4.6, 156),

        // Petiscos
        PratoDto(14, "Prego no Prato", "Petiscos", "Bife de vaca com ovo a cavalo e batatas fritas", 9.50, "https://images.unsplash.com/photo-1558030006-450675393462?auto=format&fit=crop&q=80&w=400", 1, "Casa das Bifanas", 4.3, 78),
        PratoDto(15, "Tábua de Queijos e Enchidos", "Petiscos", "Seleção de queijos regionais com presunto e chouriço", 12.00, "https://images.unsplash.com/photo-1541544741938-0af808871ccd?auto=format&fit=crop&q=80&w=400", 4, "Taberna da Rua das Flores", 4.4, 91),
        PratoDto(16, "Pataniscas de Bacalhau", "Petiscos", "Pataniscas douradas servidas com arroz de feijão", 8.50, "https://images.unsplash.com/photo-1562967916-eb82221dfb92?auto=format&fit=crop&q=80&w=400", 3, "Cervejaria Ramiro", 4.2, 67),

        // Doces
        PratoDto(17, "Pastel de Nata", "Doces", "O autêntico pastel de Belém com canela e açúcar em pó", 1.50, "https://images.unsplash.com/photo-1481504225026-669de4a706b8?auto=format&fit=crop&q=80&w=400", 7, "Pastéis de Belém", 4.9, 523),
        PratoDto(18, "Bola de Berlim", "Doces", "Bola de Berlim recheada com creme pasteleiro artesanal", 2.00, "https://images.unsplash.com/photo-1558326567-98ae2405596b?auto=format&fit=crop&q=80&w=400", 7, "Pastéis de Belém", 4.5, 198),
        PratoDto(19, "Pudim Abade de Priscos", "Doces", "Pudim tradicional minhoto com calda de caramelo", 4.50, "https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&q=80&w=400", 8, "A Cozinha do Martinho", 4.3, 45),

        // Leitão
        PratoDto(20, "Leitão da Bairrada", "Leitão", "Leitão assado com pele estaladiça ao estilo da Bairrada", 16.00, "https://images.unsplash.com/photo-1594041680534-e8d9b2db90cf?auto=format&fit=crop&q=80&w=400", 8, "A Cozinha do Martinho", 4.8, 167),
        PratoDto(21, "Sandes de Leitão", "Leitão", "Sandes generosa de leitão com mostarda e rúcula", 7.00, "https://images.unsplash.com/photo-1553909489-cd47e0907980?auto=format&fit=crop&q=80&w=400", 6, "Solar dos Presuntos", 4.4, 73),

        // Cozido
        PratoDto(22, "Cozido à Portuguesa Completo", "Cozido", "Cozido tradicional completo para 2 pessoas", 28.00, "https://images.unsplash.com/photo-1547596009-842cdd0d84c1?auto=format&fit=crop&q=80&w=400", 6, "Solar dos Presuntos", 4.7, 89),

        // Marisco
        PratoDto(23, "Amêijoas à Bulhão Pato", "Marisco", "Amêijoas frescas com alho, coentros e azeite", 14.00, "https://images.unsplash.com/photo-1565680018434-b513d5e1051e?auto=format&fit=crop&q=80&w=400", 3, "Cervejaria Ramiro", 4.8, 234),
        PratoDto(24, "Camarão Tigre Grelhado", "Marisco", "Camarão tigre grelhado com manteiga de alho", 22.00, "https://images.unsplash.com/photo-1565680018434-b513d5e1051e?auto=format&fit=crop&q=80&w=400", 3, "Cervejaria Ramiro", 4.6, 187),
        PratoDto(25, "Arroz de Marisco", "Marisco", "Arroz cremoso com camarão, amêijoas e lagostim", 18.00, "https://images.unsplash.com/photo-1534080564583-6be75777b70a?auto=format&fit=crop&q=80&w=400", 11, "Marisqueira Nunes", 4.7, 145),

        // Vegetariano
        PratoDto(26, "Bowl Buddha Português", "Vegetariano", "Bowl com grão, abacate, tomate seco e queijo de cabra", 10.50, "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&q=80&w=400", 12, "O Botanista", 4.5, 88),
        PratoDto(27, "Açorda de Espargos", "Vegetariano", "Açorda cremosa de espargos verdes com ovo escalfado", 9.00, "https://images.unsplash.com/photo-1543362906-acfc16c67564?auto=format&fit=crop&q=80&w=400", 12, "O Botanista", 4.3, 56),
        PratoDto(28, "Migas de Couve com Feijão", "Vegetariano", "Migas alentejanas de couve com feijão branco", 8.50, "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&q=80&w=400", 10, "Cantinho do Avillez", 4.2, 42)
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
