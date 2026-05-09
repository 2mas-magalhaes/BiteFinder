package com.example.bytefinder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.data.MockDataProvider
import com.example.bytefinder.data.PratoDto
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * HomeViewModel — Gere todo o estado de filtragem e pesquisa do ecrã Home.
 *
 * Centraliza a lógica de filtragem multi-critério, garantindo:
 * - Separação de responsabilidades (lógica de filtros fora da UI)
 * - Interseção perfeita de filtros (categoria + cidade + zona + preço + pesquisa)
 * - Debounce na pesquisa textual (300ms)
 * - Zero NPE / arrays vazios controlados com Empty States
 */
data class HomeState(
    val categorias: List<String> = emptyList(),
    val selectedCategory: String = "Todos",
    val allPratos: List<PratoDto> = emptyList(),
    val filteredPratos: List<PratoDto> = emptyList(),
    val featuredPratos: List<PratoDto> = emptyList(),
    val searchSuggestions: List<PratoDto> = emptyList(),
    val searchQuery: String = "",
    val selectedCity: String = "Todas",
    val selectedZone: String = "Todas",
    val selectedPriceRange: MockDataProvider.PriceRange? = null,
    val availableZones: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isFiltersExpanded: Boolean = false,
    val viewAllCategory: String? = null,
    val viewAllTitle: String = "",
    val detectedCity: String = "",
    val detectedStreet: String = "",
    val locationLoaded: Boolean = false,
    val nearbyPratos: List<PratoDto> = emptyList(),
    val tradicionaisPratos: List<PratoDto> = emptyList(),
    val userLat: Double? = null,
    val userLng: Double? = null
)

@OptIn(FlowPreview::class)
class HomeViewModel(private val repository: DataRepository) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Flow separado para debounce da pesquisa
    private val _searchInput = MutableStateFlow("")

    init {
        loadInitialData()
        observeSearchInput()
    }

    // ─── Carregamento inicial de dados ──────────────────────────────────

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val categorias = repository.listCategorias()
                val pratos = repository.listPratos(limit = 100)

                _state.value = _state.value.copy(
                    categorias = categorias,
                    allPratos = pratos,
                    isLoading = false
                )
                applyFilters()
                refreshFeatured()
            } catch (_: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun refresh() {
        loadInitialData()
    }

    // ─── Observa input de pesquisa com debounce ─────────────────────────

    private fun observeSearchInput() {
        viewModelScope.launch {
            _searchInput
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    val suggestions = if (query.trim().length >= 2) {
                        repository.getSearchSuggestions(query, _state.value.selectedCity)
                    } else {
                        emptyList()
                    }
                    _state.value = _state.value.copy(searchSuggestions = suggestions)
                    applyFilters()
                }
        }
    }

    // ─── Ações públicas chamadas pela UI ────────────────────────────────

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        _searchInput.value = query
    }

    fun onCategorySelected(category: String) {
        _state.value = _state.value.copy(
            selectedCategory = category,
            viewAllCategory = null
        )
        applyFilters()
    }

    fun onCityChanged(city: String) {
        val zones = repository.getZonas(city)
        _state.value = _state.value.copy(
            selectedCity = city,
            selectedZone = "Todas",
            availableZones = zones
        )
        applyFilters()
    }

    fun onLocationDetected(matchedCity: String, displayCity: String, displayStreet: String, lat: Double? = null, lng: Double? = null) {
        val zones = repository.getZonas(matchedCity)
        _state.value = _state.value.copy(
            selectedCity = matchedCity,
            selectedZone = "Todas",
            availableZones = zones,
            detectedCity = displayCity,
            detectedStreet = displayStreet,
            locationLoaded = true,
            userLat = lat,
            userLng = lng
        )
        applyFilters()
        refreshFeatured()

        if (lat != null && lng != null) {
            viewModelScope.launch {
                val nearby = repository.getNearbyPratos(lat, lng)
                val trad = repository.getNearbyPratos(lat, lng, categoria = "Pratos Tradicionais")
                _state.value = _state.value.copy(
                    nearbyPratos = nearby,
                    tradicionaisPratos = trad
                )
            }
        }
    }

    fun onZoneChanged(zone: String) {
        _state.value = _state.value.copy(selectedZone = zone)
        applyFilters()
    }

    fun onPriceRangeChanged(range: MockDataProvider.PriceRange?) {
        _state.value = _state.value.copy(selectedPriceRange = range)
        applyFilters()
    }

    fun onToggleFilters() {
        _state.value = _state.value.copy(isFiltersExpanded = !_state.value.isFiltersExpanded)
    }

    fun onViewAll(category: String, title: String) {
        _state.value = _state.value.copy(viewAllCategory = category, viewAllTitle = title)
    }

    fun onBackFromViewAll() {
        _state.value = _state.value.copy(viewAllCategory = null)
    }

    fun resetToDefault() {
        _state.value = _state.value.copy(
            selectedCategory = "Todos",
            searchQuery = "",
            searchSuggestions = emptyList(),
            selectedCity = "Todas",
            selectedZone = "Todas",
            selectedPriceRange = null,
            isFiltersExpanded = false,
            viewAllCategory = null,
            viewAllTitle = ""
        )
        _searchInput.value = ""
        applyFilters()
    }

    fun clearSearch() {
        _state.value = _state.value.copy(
            searchQuery = "",
            searchSuggestions = emptyList()
        )
        _searchInput.value = ""
        applyFilters()
    }

    // ─── Algoritmo de filtragem multi-critério ──────────────────────────

    /**
     * Aplica TODOS os filtros ativos em interseção.
     * Ordem de filtragem: Categoria → Cidade → Zona → Preço → Pesquisa textual
     * Resultado ordenado por rating descendente.
     */
    private fun applyFilters() {
        val s = _state.value
        val filtered = repository.filterPratosLocal(
            categoria = s.selectedCategory,
            cidade = s.selectedCity,
            zona = s.selectedZone,
            priceRange = s.selectedPriceRange,
            searchQuery = s.searchQuery.ifBlank { null }
        )
        val deduplicated = repository.deduplicatePratos(filtered)
        _state.value = s.copy(filteredPratos = deduplicated)
    }

    private fun refreshFeatured() {
        val city = _state.value.selectedCity
        _state.value = _state.value.copy(
            featuredPratos = repository.getDestacados(city)
        )
    }
}

/**
 * Factory para injeção de dependência do HomeViewModel.
 */
class HomeViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
