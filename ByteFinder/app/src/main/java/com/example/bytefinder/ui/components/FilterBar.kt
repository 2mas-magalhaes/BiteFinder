package com.example.bytefinder.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.data.MockDataProvider
import com.example.bytefinder.ui.theme.*

/**
 * FilterBar — Barra de filtros expansível com chips Claymorphism.
 *
 * Permite filtrar por: Cidade, Zona, Faixa de Preço.
 * Colapsa/expande com animação suave.
 */
@Composable
fun FilterBar(
    expanded: Boolean,
    onToggle: () -> Unit,
    selectedCity: String,
    onCityChange: (String) -> Unit,
    selectedZone: String,
    onZoneChange: (String) -> Unit,
    selectedPriceRange: MockDataProvider.PriceRange?,
    onPriceRangeChange: (MockDataProvider.PriceRange?) -> Unit,
    availableZones: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Toggle button
        ClayCard(
            onClick = onToggle,
            backgroundColor = ClayBeige,
            cornerRadius = 14.dp,
            elevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.FilterAlt,
                        contentDescription = null,
                        tint = ClayTextDark,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Filtros",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = ClayTextDark
                    )

                    // Indicadores de filtros ativos
                    val activeFilters = listOfNotNull(
                        if (selectedCity != "Todas") selectedCity else null,
                        if (selectedZone != "Todas") selectedZone else null,
                        if (selectedPriceRange != null && selectedPriceRange.label != "Todos") selectedPriceRange.label else null
                    )
                    if (activeFilters.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ClayOrange)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "${activeFilters.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayWhite
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Fechar filtros" else "Abrir filtros",
                    tint = ClayTextMedium
                )
            }
        }

        // Filtros expandidos
        AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cidade
                FilterSection(
                    label = "Cidade",
                    options = MockDataProvider.cidades,
                    selected = selectedCity,
                    onSelect = {
                        onCityChange(it)
                        onZoneChange("Todas")
                    }
                )

                // Zona (só aparece se cidade selecionada)
                if (selectedCity != "Todas" && availableZones.isNotEmpty()) {
                    FilterSection(
                        label = "Zona",
                        options = availableZones,
                        selected = selectedZone,
                        onSelect = onZoneChange
                    )
                }

                // Faixa de Preço
                FilterSection(
                    label = "Preço",
                    options = MockDataProvider.priceRanges.map { it.label },
                    selected = selectedPriceRange?.label ?: "Todos",
                    onSelect = { label ->
                        val range = MockDataProvider.priceRanges.find { it.label == label }
                        onPriceRangeChange(range)
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = ClayTextMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    text = option,
                    isSelected = option == selected,
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "chip-scale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) ClayOrange else ClayBeige
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ClayWhite else ClayTextDark
        )
    }
}
