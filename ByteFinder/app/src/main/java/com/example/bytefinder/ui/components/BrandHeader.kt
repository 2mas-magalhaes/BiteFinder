package com.example.bytefinder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.ui.theme.*

/**
 * BrandHeader — Cabeçalho com logo e nome da app. Clicável para navegar para Home.
 */
@Composable
fun BrandHeader(
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onGoHome() }
    ) {
        AsyncImage(
            model = "https://bitefinderstorage.blob.core.windows.net/icons/logo-bf.png",
            contentDescription = "Logo BiteFinder",
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(8.dp))

        Column {
            Text(
                "BiteFinder",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ClayOrange
            )
            Text(
                "encontra o prato certo, já",
                fontSize = 11.sp,
                color = ClayTextLight
            )
        }
    }
}
