package com.example.bytefinder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.ClayBlue
import com.example.bytefinder.ui.theme.ClayOnDark
import com.example.bytefinder.ui.theme.ClayOnDarkSecond

enum class NavTab(val label: String, val icon: ImageVector) {
    HOME("Início", Icons.Filled.Home),
    SEARCH("Pesquisar", Icons.Filled.Search),
    NEAR("Na zona", Icons.Filled.Place),
    ACCOUNT("Conta", Icons.Filled.Person)
}

/**
 * ClayBottomNav — Barra de navegação inferior Claymorphism.
 *
 * Tab ativa: pill indicator + ícone aumentado + texto bold branco
 * Tab inativa: ícone + texto a 60% white, sem indicator
 * Squish spring individual por tab ao pressionar
 *
 * Para utilizadores restaurante, o tab NEAR mostra "Restaurante" com ícone de loja.
 */
@Composable
fun ClayBottomNav(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isRestaurantUser: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF111E30))
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val overrideLabel = if (tab == NavTab.NEAR && isRestaurantUser) "Restaurante" else tab.label
                val overrideIcon = if (tab == NavTab.NEAR && isRestaurantUser) Icons.Filled.Storefront else tab.icon
                ClayNavItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    labelOverride = overrideLabel,
                    iconOverride = overrideIcon
                )
            }
        }
    }
}

@Composable
private fun ClayNavItem(
    tab: NavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    labelOverride: String = tab.label,
    iconOverride: ImageVector = tab.icon
) {
    val interaction = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 380f),
        label = "nav-scale-${tab.name}"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = iconOverride,
            contentDescription = labelOverride,
            tint = if (isSelected) ClayBlue else ClayOnDarkSecond,
            modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = labelOverride,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) ClayOnDark else ClayOnDarkSecond
        )
    }
}
