package com.example.bytefinder.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                val res = api.listCategorias()
                if (res.ok && res.items.isNotEmpty()) res.items else MockDataProvider.categorias
            } catch (_: Exception) {
                useOnlyMock = true
                MockDataProvider.categorias
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
            val res = api.listPratos(search, categoria, restauranteId, limit, offset)
            if (res.ok && res.items.isNotEmpty()) res.items
            else MockDataProvider.filterPratos(categoria = categoria, searchQuery = search)
                .let { list ->
                    if (restauranteId != null) list.filter { it.restauranteId == restauranteId }
                    else list
                }.drop(offset).take(limit)
        } catch (_: Exception) {
            useOnlyMock = true
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
        minRating: Double = 0.0
    ): List<PratoDto> = MockDataProvider.filterPratos(
        categoria = categoria,
        cidade = cidade,
        zona = zona,
        priceRange = priceRange,
        searchQuery = searchQuery,
        minRating = minRating
    )

    // ─── Sugestões de pesquisa ──────────────────────────────────────────

    fun getSearchSuggestions(query: String): List<PratoDto> =
        MockDataProvider.getSearchSuggestions(query)

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
                MockDataProvider.deleteAvaliacao(req.avaliacaoId)
                return@withContext BasicOkResponse(ok = true, message = "Avaliação eliminada (modo offline)")
            }
            try {
                api.deleteAvaliacao(req)
            } catch (_: Exception) {
                MockDataProvider.deleteAvaliacao(req.avaliacaoId)
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
