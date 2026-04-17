package com.example.bytefinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.components.ClayButton
import com.example.bytefinder.ui.components.ClayCard
import com.example.bytefinder.ui.components.claySceneBackground
import com.example.bytefinder.ui.theme.*

@Composable
fun ClayAccountScreen(
    userName: String,
    userRole: String,
    isRestaurantUser: Boolean,
    onOpenBusiness: () -> Unit,
    onOpenMyReviews: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(32.dp))

        // ─── Avatar + Nome ────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ClayBlue)
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(Modifier.size(18.dp))

            Column {
                Text(
                    text = userName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = ClayOnDark
                )
                Text(
                    text = if (isRestaurantUser) "Restaurante" else "Utilizador",
                    color = ClayOnDarkSecond,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(36.dp))

        // ─── Opções ──────────────────────────────────────────────────────
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF111E30),
            cornerRadius = 24.dp,
            elevation = 0.dp
        ) {
            Column {
                if (isRestaurantUser) {
                    AccountRow(icon = Icons.Filled.Storefront, label = "Gerir Restaurante", onClick = onOpenBusiness)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                }
                AccountRow(icon = Icons.Filled.Star, label = "As minhas avaliações", onClick = onOpenMyReviews)
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                AccountRow(icon = Icons.Filled.Settings, label = "Definições", onClick = {})
            }
        }

        Spacer(Modifier.height(24.dp))

        // ─── Sign Out ────────────────────────────────────────────────────
        ClayButton(
            text = "Terminar sessão",
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFF1E2D42),
            contentColor = ClayOnDark
        )
    }
}

@Composable
private fun AccountRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ClayOnDarkSecond,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = ClayOnDark,
            modifier = Modifier.weight(1f)
        )
        Text("›", fontSize = 22.sp, color = ClayOnDarkSecond)
    }
}
