package com.example.bytefinder.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── COLOR SCHEME CLAYMORPHISM ──────────────────────────────────────────────

private val ClayColorScheme = lightColorScheme(
    primary = ClayBlue,
    onPrimary = Color.White,
    primaryContainer = ClayBluePale,
    onPrimaryContainer = ClayTextDark,
    secondary = ClayBlueSoft,
    onSecondary = Color.White,
    secondaryContainer = ClayBlueLight,
    onSecondaryContainer = ClayTextDark,
    tertiary = ClayGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD4F0DD),
    onTertiaryContainer = ClayTextDark,
    background = ClaySurface,
    onBackground = ClayTextDark,
    surface = ClayWhite,
    onSurface = ClayTextDark,
    surfaceVariant = ClayBlueLight,
    onSurfaceVariant = ClayTextMedium,
    outline = ClayBluePale,
    outlineVariant = ClayBlueLight,
    error = ClayRedDeep,
    onError = Color.White,
    errorContainer = Color(0xFFFDE8E8),
    onErrorContainer = ClayRedDeep
)

private fun clayEmbossed(style: TextStyle): TextStyle = style.copy(
    shadow = Shadow(
        color = ClayShadowDark.copy(alpha = 0.14f),
        offset = Offset(0f, 0.8f),
        blurRadius = 0.8f
    )
)

// ─── TIPOGRAFIA CLAYMORPHISM ────────────────────────────────────────────────

private val ClayTypography = Typography(
    headlineLarge = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        color = ClayTextDark
    )),
    headlineMedium = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    )),
    titleLarge = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    )),
    titleMedium = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp
    )),
    bodyLarge = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )),
    bodyMedium = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )),
    labelLarge = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )),
    labelSmall = clayEmbossed(TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ))
)

// ─── FORMAS ARREDONDADAS ────────────────────────────────────────────────────

private val ClayShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

// ─── TEMA PRINCIPAL ─────────────────────────────────────────────────────────

@Composable
fun BitefinderClayTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClayColorScheme,
        typography = ClayTypography,
        shapes = ClayShapes,
        content = {
            CompositionLocalProvider(
                LocalTextStyle provides clayEmbossed(MaterialTheme.typography.bodyLarge)
            ) {
                content()
            }
        }
    )
}
