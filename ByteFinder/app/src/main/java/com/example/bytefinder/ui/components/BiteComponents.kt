package com.example.bytefinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.ClayBeigeDeep
import com.example.bytefinder.ui.theme.ClayBlue
import com.example.bytefinder.ui.theme.ClayBlueDeep
import com.example.bytefinder.ui.theme.ClayBlueLight
import com.example.bytefinder.ui.theme.ClayTextDark
import com.example.bytefinder.ui.theme.ClayTextMedium
import com.example.bytefinder.ui.theme.ClayWhite
import com.example.bytefinder.ui.theme.ClayYellow
import java.util.Locale

@Composable
fun BiteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = ClayBlue,
    contentColor: Color = ClayWhite,
    secondary: Boolean = false
) {
    ClayButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        containerColor = containerColor,
        contentColor = contentColor,
        isSecondary = secondary
    )
}

@Composable
fun BiteCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = ClayWhite,
    radius: Dp = 24.dp,
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    ClayCard(
        modifier = modifier,
        onClick = onClick,
        backgroundColor = backgroundColor,
        cornerRadius = radius,
        elevation = elevation,
        content = content
    )
}

@Composable
fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ClayYellow.copy(alpha = 0.28f))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "★ ${String.format(Locale.US, "%.1f", rating)}",
            color = ClayTextDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PriceTag(price: Double?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(ClayBlueLight)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = price?.let { String.format(Locale.US, "%.2f€", it) } ?: "-",
            color = ClayBlueDeep,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun DistanceChip(distanceKm: Double?, modifier: Modifier = Modifier) {
    if (distanceKm == null) return
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(ClayBlue.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = String.format(Locale.US, "%.1f km", distanceKm),
            color = ClayBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ClayBlue,
            unfocusedBorderColor = ClayBeigeDeep,
            focusedLabelColor = ClayBlue,
            unfocusedLabelColor = ClayTextMedium,
            cursorColor = ClayBlue,
            focusedContainerColor = ClayWhite,
            unfocusedContainerColor = ClayWhite
        )
    )
}

@Composable
fun BiteSectionLabel(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, color = ClayTextDark, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(3.dp))
            Text(subtitle, color = ClayTextMedium, fontSize = 13.sp)
        }
    }
}
