package com.example.bytefinder.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.text.Normalizer
import java.util.Locale

// -------------------- LOGIN --------------------

data class LoginRequest(
    val email: String,
    val password: String,
    val _csrf: String? = null  // CSRF token for cross-site attack protection
)

data class SocialLoginRequest(
    val provider: String,
    val token: String,
    val email: String? = null,
    val name: String? = null
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
    val message: String? = null,
    val csrf_token: String? = null  // CSRF token for next request
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
    val totalAvaliacoes: Int,
    val restauranteLatitude: Double? = null,
    val restauranteLongitude: Double? = null,
    val distanciaKm: Double? = null,
    val destacado: Boolean = false
)

fun PratoDto.displayImageUrl(): String? {
    return resolveDisplayImageUrl(nome = nome, categoria = categoria, imagemUrl = imagemUrl)
}

fun PratoDetailDto.displayImageUrl(): String? {
    return resolveDisplayImageUrl(nome = nome, categoria = categoria, imagemUrl = imagemUrl)
}

private fun resolveDisplayImageUrl(nome: String, categoria: String?, imagemUrl: String?): String? {
    val original = imagemUrl?.takeIf { it.isNotBlank() }
    val normalizedName = normalizeImageKey(nome)
    val fallback = reliableDishFallbackImages.firstNotNullOfOrNull { (needle, url) ->
        url.takeIf { normalizedName.contains(needle) }
    } ?: categoria?.let { categoryFallbackImage(it) }

    return when {
        original == null -> fallback
        original.contains("Arroz_de_pato_no_forno", ignoreCase = true) -> fallback
        original.contains("A%C3%A7orda_alentejana", ignoreCase = true) -> fallback
        original.contains("Feijoada_%C3%A0_transmontana", ignoreCase = true) -> fallback
        else -> original
    }
}

private val dishFallbackImages = listOf(
    "Arroz de Pato" to "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&q=80&w=700",
    "Açorda" to "https://commons.wikimedia.org/wiki/Special:Redirect/file/A%C3%A7orda%20%C3%A0%20Alentejana.jpg",
    "Feijoada" to "https://commons.wikimedia.org/wiki/Special:Redirect/file/Feijoada%20%C3%A0%20transmontada.jpg",
    "Cozido" to "https://commons.wikimedia.org/wiki/Special:Redirect/file/Cozido%20a%20portuguesa%201.JPG",
    "Rojões" to "https://images.unsplash.com/photo-1432139555190-58524dae6a55?auto=format&fit=crop&q=80&w=700",
    "Tripas" to "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&q=80&w=700",
    "Cabidela" to "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&q=80&w=700"
)

private val reliableDishFallbackImages = listOf(
    "arroz de pato" to "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&q=80&w=700",
    "acorda" to "https://commons.wikimedia.org/wiki/Special:FilePath/A%C3%A7orda%20%C3%A0%20Alentejana.jpg",
    "feijoada" to "https://commons.wikimedia.org/wiki/Special:FilePath/Feijoada%20%C3%A0%20transmontada.jpg",
    "cozido" to "https://commons.wikimedia.org/wiki/Special:FilePath/Cozido%20a%20portuguesa%201.JPG",
    "rojoes" to "https://images.unsplash.com/photo-1432139555190-58524dae6a55?auto=format&fit=crop&q=80&w=700",
    "tripas" to "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&q=80&w=700",
    "cabidela" to "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&q=80&w=700"
)

private fun categoryFallbackImage(category: String): String {
    val normalizedCategory = normalizeImageKey(category)
    return MockDataProvider.categoryImages.entries
        .firstOrNull { normalizeImageKey(it.key) == normalizedCategory }
        ?.value
        ?: MockDataProvider.getCategoryImageUrl(category)
}

private fun normalizeImageKey(value: String): String =
    Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.ROOT)

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

data class CreatePratoRequest(
    val userId: Int,
    val restauranteId: Int,
    val nome: String,
    val descricao: String?,
    val categoria: String?,
    val preco: Double?,
    val imagemUrl: String?
)

data class DeletePratoRequest(
    val idPrato: Int,
    val userId: Int,
    val restauranteId: Int
)

data class ToggleDestacadoRequest(
    val idPrato: Int,
    val userId: Int,
    val restauranteId: Int,
    val destacado: Boolean
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

    @POST("api/auth/social_login.php")
    suspend fun socialLogin(@Body req: SocialLoginRequest): LoginResponse

    @GET("api/categorias/list.php")
    suspend fun listCategorias(): CategoriasResponse

    @GET("api/pratos/nearby.php")
    suspend fun getNearbyPratos(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double = 5.0,
        @Query("categoria") categoria: String? = null,
        @Query("limit") limit: Int = 30
    ): PratosListResponse

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

    @POST("api/pratos/create.php")
    suspend fun createPrato(
        @Body req: CreatePratoRequest
    ): BasicOkResponse

    @POST("api/pratos/delete.php")
    suspend fun deletePrato(
        @Body req: DeletePratoRequest
    ): BasicOkResponse
}
