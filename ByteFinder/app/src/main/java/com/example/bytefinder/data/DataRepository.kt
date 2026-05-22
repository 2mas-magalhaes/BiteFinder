package com.example.bytefinder.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * DataRepository — Camada de abstração entre a UI e a fonte de dados.
 *
 * Tenta sempre a API real primeiro. Se falhar (rede indisponível, servidor
 * offline), faz fallback transparente para MockDataProvider.
 *
 * Padrão Repository (SOLID - Dependency Inversion Principle):
 * Os ViewModels dependem desta interface, não da implementação concreta.
 */
class DataRepository(private val api: ApiService) {

    // ─── Flag: se a API já falhou, usa mock diretamente ─────────────────
    private var useOnlyMock = false

    private suspend fun <T> fastRead(timeoutMs: Long = 4_500, block: suspend () -> T): T? =
        try {
            withTimeoutOrNull(timeoutMs) { block() }
        } catch (_: Exception) {
            null
        }

    // ─── Login ──────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): LoginResponse =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext MockDataProvider.mockLogin(email, password)
            try {
                api.login(LoginRequest(email, password))
            } catch (_: Exception) {
                useOnlyMock = true
                MockDataProvider.mockLogin(email, password)
            }
        }

    // ─── Categorias ─────────────────────────────────────────────────────

    suspend fun listCategorias(): List<String> =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext MockDataProvider.categorias
            try {
                val res = fastRead { api.listCategorias() }
                if (res?.ok == true && res.items.isNotEmpty()) res.items else MockDataProvider.categorias
            } catch (_: Exception) {
                useOnlyMock = true
                MockDataProvider.categorias
            }
        }

    // ─── Na Zona ────────────────────────────────────────────────────────

    suspend fun getNearbyPratos(lat: Double, lng: Double, radius: Double = 5.0, categoria: String? = null): List<PratoDto> =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext MockDataProvider.getNearbyPratos(lat, lng, radius, categoria)
            try {
                val res = fastRead { api.getNearbyPratos(lat, lng, radius, categoria) }
                if (res?.ok == true && res.items.isNotEmpty()) {
                    res.items.map { MockDataProvider.enrichWithRestaurantLocation(it, lat, lng) }
                } else {
                    MockDataProvider.getNearbyPratos(lat, lng, radius, categoria)
                }
            } catch (_: Exception) {
                MockDataProvider.getNearbyPratos(lat, lng, radius, categoria)
            }
        }

    // ─── Listagem de Pratos ─────────────────────────────────────────────

    suspend fun listPratos(
        search: String? = null,
        categoria: String? = null,
        restauranteId: Int? = null,
        limit: Int = 30,
        offset: Int = 0
    ): List<PratoDto> = withContext(Dispatchers.IO) {
        if (useOnlyMock) {
            return@withContext MockDataProvider.filterPratos(
                categoria = categoria,
                searchQuery = search
            ).let { list ->
                if (restauranteId != null) list.filter { it.restauranteId == restauranteId }
                else list
            }.drop(offset).take(limit)
        }
        try {
            val res = fastRead { api.listPratos(search, categoria, restauranteId, limit, offset) }
            if (res?.ok == true && res.items.isNotEmpty()) res.items
            else MockDataProvider.filterPratos(categoria = categoria, searchQuery = search)
                .let { list ->
                    if (restauranteId != null) list.filter { it.restauranteId == restauranteId }
                    else list
                }.drop(offset).take(limit)
        } catch (_: Exception) {
            MockDataProvider.filterPratos(categoria = categoria, searchQuery = search)
                .let { list ->
                    if (restauranteId != null) list.filter { it.restauranteId == restauranteId }
                    else list
                }.drop(offset).take(limit)
        }
    }

    // ─── Filtragem avançada (multi-critério, usa sempre mock local) ─────

    fun filterPratosLocal(
        categoria: String? = null,
        cidade: String? = null,
        zona: String? = null,
        priceRange: MockDataProvider.PriceRange? = null,
        searchQuery: String? = null,
        minRating: Double = 0.0,
        source: List<PratoDto>? = null
    ): List<PratoDto> = MockDataProvider.filterPratos(
        categoria = categoria,
        cidade = cidade,
        zona = zona,
        priceRange = priceRange,
        searchQuery = searchQuery,
        minRating = minRating,
        source = source
    )

    // ─── Sugestões de pesquisa ──────────────────────────────────────────

    fun getSearchSuggestions(query: String, cidade: String = "Todas"): List<PratoDto> =
        MockDataProvider.getSearchSuggestions(query, cidade)

    // ─── Detalhe do Prato ───────────────────────────────────────────────

    suspend fun getPratoDetail(id: Int): PratoDetailDto? =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext MockDataProvider.getPratoDetail(id)
            try {
                val res = api.getPratoDetail(id)
                if (res.ok) res.item else MockDataProvider.getPratoDetail(id)
            } catch (_: Exception) {
                useOnlyMock = true
                MockDataProvider.getPratoDetail(id)
            }
        }

    // ─── Avaliações ─────────────────────────────────────────────────────

    suspend fun listAvaliacoes(pratoId: Int): List<AvaliacaoDto> =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext MockDataProvider.getAvaliacoes(pratoId)
            try {
                val res = api.listAvaliacoes(pratoId)
                if (res.ok) res.items else MockDataProvider.getAvaliacoes(pratoId)
            } catch (_: Exception) {
                useOnlyMock = true
                MockDataProvider.getAvaliacoes(pratoId)
            }
        }

    suspend fun createAvaliacao(req: CreateAvaliacaoRequest): BasicOkResponse =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) {
                MockDataProvider.addAvaliacao(req.pratoId, req.userId, req.classificacao, req.comentario)
                return@withContext BasicOkResponse(ok = true, message = "Avaliação guardada com sucesso (modo offline)")
            }
            try {
                api.createAvaliacao(req)
            } catch (_: Exception) {
                MockDataProvider.addAvaliacao(req.pratoId, req.userId, req.classificacao, req.comentario)
                BasicOkResponse(ok = true, message = "Avaliação guardada com sucesso (modo offline)")
            }
        }

    suspend fun deleteAvaliacao(req: DeleteAvaliacaoRequest): BasicOkResponse =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) {
                MockDataProvider.deleteAvaliacao(req.idAvaliacao)
                return@withContext BasicOkResponse(ok = true, message = "Avaliação eliminada (modo offline)")
            }
            try {
                api.deleteAvaliacao(req)
            } catch (_: Exception) {
                MockDataProvider.deleteAvaliacao(req.idAvaliacao)
                BasicOkResponse(ok = true, message = "Avaliação eliminada (modo offline)")
            }
        }

    suspend fun responderAvaliacao(req: CreateRespostaRequest): BasicOkResponse =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext BasicOkResponse(ok = true, message = "Resposta guardada (modo offline)")
            try {
                api.responderAvaliacao(req)
            } catch (_: Exception) {
                BasicOkResponse(ok = true, message = "Resposta guardada (modo offline)")
            }
        }

    // ─── Avaliações do utilizador ───────────────────────────────────────

    suspend fun listMyAvaliacoes(userId: Int): List<MyReviewItemDto> =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext MockDataProvider.getMyReviews(userId)
            try {
                val res = api.listMyAvaliacoes(userId)
                if (res.ok) res.items else MockDataProvider.getMyReviews(userId)
            } catch (_: Exception) {
                useOnlyMock = true
                MockDataProvider.getMyReviews(userId)
            }
        }

    // ─── Atualizar Prato ────────────────────────────────────────────────

    suspend fun updatePrato(req: UpdatePratoRequest): BasicOkResponse =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) return@withContext BasicOkResponse(ok = true, message = "Prato atualizado (modo offline)")
            try {
                api.updatePrato(req)
            } catch (_: Exception) {
                BasicOkResponse(ok = true, message = "Prato atualizado (modo offline)")
            }
        }

    // ─── Criar Prato ────────────────────────────────────────────────────

    suspend fun createPrato(req: CreatePratoRequest): BasicOkResponse =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) {
                MockDataProvider.createPrato(req.restauranteId, req.nome, req.descricao, req.categoria, req.preco, req.imagemUrl)
                return@withContext BasicOkResponse(ok = true, message = "Prato criado (modo offline)")
            }
            try {
                api.createPrato(req)
            } catch (_: Exception) {
                MockDataProvider.createPrato(req.restauranteId, req.nome, req.descricao, req.categoria, req.preco, req.imagemUrl)
                BasicOkResponse(ok = true, message = "Prato criado (modo offline)")
            }
        }

    // ─── Eliminar Prato ─────────────────────────────────────────────────

    suspend fun deletePrato(req: DeletePratoRequest): BasicOkResponse =
        withContext(Dispatchers.IO) {
            if (useOnlyMock) {
                MockDataProvider.deletePrato(req.idPrato)
                return@withContext BasicOkResponse(ok = true, message = "Prato eliminado (modo offline)")
            }
            try {
                api.deletePrato(req)
            } catch (_: Exception) {
                MockDataProvider.deletePrato(req.idPrato)
                BasicOkResponse(ok = true, message = "Prato eliminado (modo offline)")
            }
        }

    // ─── Nome do restaurante ────────────────────────────────────────────

    fun getRestauranteNome(id: Int): String = MockDataProvider.getRestauranteNome(id)

    // ─── Pratos em Destaque ─────────────────────────────────────────────

    fun getDestacados(cidade: String = "Todas"): List<PratoDto> = MockDataProvider.getDestacados(cidade)

    fun toggleDestacado(pratoId: Int): Boolean = MockDataProvider.toggleDestacado(pratoId)

    // ─── Dados estáticos de filtros ─────────────────────────────────────

    fun getCidades() = MockDataProvider.cidades
    fun getZonas(cidade: String) = MockDataProvider.zonasPorCidade[cidade] ?: emptyList()
    fun getPriceRanges() = MockDataProvider.priceRanges
    fun getCategoryImageUrl(name: String) = MockDataProvider.getCategoryImageUrl(name)

    // ─── Agrupamento por tipo de prato ──────────────────────────────────

    fun getPratoTipo(pratoId: Int) = MockDataProvider.getPratoTipo(pratoId)
    fun getPratosByTipo(tipo: String, cidade: String = "Todas") = MockDataProvider.getPratosByTipo(tipo, cidade)
    fun deduplicatePratos(source: List<PratoDto>) = MockDataProvider.deduplicatePratos(source)
}
