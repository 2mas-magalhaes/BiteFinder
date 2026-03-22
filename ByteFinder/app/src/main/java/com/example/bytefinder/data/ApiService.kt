package com.example.bytefinder.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// -------------------- LOGIN --------------------

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserDto(
    val id: Int,
    val nome: String,
    val role: String,
    val restaurantes: List<Int> = emptyList()
)

data class LoginResponse(
    val ok: Boolean,
    val token: String?,
    val user: UserDto?,
    val error: String?,
    val message: String? = null
)

// -------------------- CATEGORIAS --------------------

data class CategoriasResponse(
    val ok: Boolean,
    val items: List<String> = emptyList(),
    val error: String? = null,
    val message: String? = null
)

// -------------------- LISTAGEM DE PRATOS --------------------

data class PratoDto(
    val id: Int,
    val nome: String,
    val categoria: String?,
    val descricao: String?,
    val preco: Double?,
    val imagemUrl: String?,
    val restauranteId: Int,
    val restauranteNome: String,
    val ratingMedio: Double,
    val totalAvaliacoes: Int
)

data class PratosListResponse(
    val ok: Boolean,
    val count: Int = 0,
    val items: List<PratoDto> = emptyList(),
    val error: String? = null,
    val message: String? = null
)

// -------------------- DETALHE DO PRATO --------------------

data class PratoDetailDto(
    val id: Int,
    val nome: String,
    val categoria: String?,
    val descricao: String?,
    val preco: Double?,
    val imagemUrl: String?,
    val disponivel: Boolean,
    val restauranteId: Int,
    val restauranteNome: String,
    val restauranteMorada: String?,
    val restauranteLatitude: Double?,
    val restauranteLongitude: Double?,
    val ratingMedio: Double,
    val totalAvaliacoes: Int
)

data class PratoDetailResponse(
    val ok: Boolean,
    val item: PratoDetailDto?,
    val error: String? = null,
    val message: String? = null
)

// -------------------- AVALIAÇÕES DO PRATO --------------------

data class AvaliacaoDto(
    val idAvaliacao: Int,
    val userId: Int,
    val autorNome: String?,
    val classificacao: Int,
    val comentario: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val idResposta: Int?,
    val respostaTexto: String?,
    val respostaCreatedAt: String?
)

data class AvaliacoesListResponse(
    val ok: Boolean,
    val items: List<AvaliacaoDto> = emptyList(),
    val error: String? = null,
    val message: String? = null
)

data class CreateAvaliacaoRequest(
    val pratoId: Int,
    val userId: Int,
    val classificacao: Int,
    val comentario: String
)

data class DeleteAvaliacaoRequest(
    val idAvaliacao: Int,
    val userId: Int
)

data class CreateRespostaRequest(
    val idAvaliacao: Int,
    val userId: Int,
    val texto: String
)

data class UpdatePratoRequest(
    val idPrato: Int,
    val userId: Int,
    val restauranteId: Int,
    val nome: String,
    val descricao: String?,
    val categoria: String?,
    val preco: Double?,
    val imagemUrl: String?
)

data class BasicOkResponse(
    val ok: Boolean,
    val error: String? = null,
    val message: String? = null
)

// -------------------- AVALIAÇÕES DO UTILIZADOR --------------------

data class MyReviewItemDto(
    val idAvaliacao: Int,
    val pratoId: Int,
    val pratoNome: String,
    val pratoImagemUrl: String?,
    val restauranteNome: String,
    val classificacao: Int,
    val comentario: String?,
    val createdAt: String?
)

data class MyReviewsResponse(
    val ok: Boolean,
    val items: List<MyReviewItemDto> = emptyList(),
    val error: String? = null,
    val message: String? = null
)

// -------------------- API SERVICE --------------------

interface ApiService {

    @POST("api/auth/login_jwt.php")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("api/categorias/list.php")
    suspend fun listCategorias(): CategoriasResponse

    @GET("api/pratos/list.php")
    suspend fun listPratos(
        @Query("search") search: String? = null,
        @Query("categoria") categoria: String? = null,
        @Query("restauranteId") restauranteId: Int? = null,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): PratosListResponse

    @GET("api/pratos/detail.php")
    suspend fun getPratoDetail(
        @Query("id") id: Int
    ): PratoDetailResponse

    @GET("api/avaliacoes/list.php")
    suspend fun listAvaliacoes(
        @Query("pratoId") pratoId: Int
    ): AvaliacoesListResponse

    @POST("api/avaliacoes/create.php")
    suspend fun createAvaliacao(
        @Body req: CreateAvaliacaoRequest
    ): BasicOkResponse

    @POST("api/avaliacoes/delete.php")
    suspend fun deleteAvaliacao(
        @Body req: DeleteAvaliacaoRequest
    ): BasicOkResponse

    @POST("api/avaliacoes/respond.php")
    suspend fun responderAvaliacao(
        @Body req: CreateRespostaRequest
    ): BasicOkResponse

    @GET("api/avaliacoes/my.php")
    suspend fun listMyAvaliacoes(
        @Query("userId") userId: Int
    ): MyReviewsResponse

    @POST("api/pratos/update.php")
    suspend fun updatePrato(
        @Body req: UpdatePratoRequest
    ): BasicOkResponse
}