package com.example.bytefinder.ui.theme

import androidx.compose.ui.graphics.Color

// BiteFinder blue premium system: Sapphire Food-Tech.

// Primarios - azul BiteFinder refinado
val ClayBlue        = Color(0xFF1B6EC2)
val ClayBlueDeep    = Color(0xFF103E7D)
val ClayBlueSoft    = Color(0xFF4A90D9)
val ClayBluePale    = Color(0xFFD6E8F8)
val ClayBlueLight   = Color(0xFFEBF3FF)

// Neutros frios premium
val ClaySurface     = Color(0xFFF4F7FB)
val ClayWhite       = Color(0xFFFFFFFF)
val ClayOffWhite    = Color(0xFFF8FAFF)
val ClayBeigeSoft   = Color(0xFFF5F8FC)

// Compatibilidade (aliases para codigo existente)
val ClayCream       = ClaySurface
val ClayBeige       = ClayBlueLight
val ClayBeigeDeep   = ClayBluePale
val ClayOrange      = ClayBlue
val ClayOrangeDeep  = ClayBlueDeep
val ClayPeach       = ClayBluePale

// Texto
val ClayTextDark    = Color(0xFF12213D)
val ClayTextMedium  = Color(0xFF445069)
val ClayTextLight   = Color(0xFF8FA3BF)

// Acentos
val ClayGreen       = Color(0xFF34C67A)
val ClayGreenDeep   = Color(0xFF1E9E5E)
val ClayRed         = Color(0xFFE85C5C)
val ClayRedDeep     = Color(0xFFC93A3A)
val ClayYellow      = Color(0xFFFFC107)
val ClayYellowDeep  = Color(0xFFE6A800)

// Sombras suaves e neutras para evitar halos/artefactos no emulador
val ClayShadowLight = Color(0xCCFFFFFF)
val ClayShadowDark  = Color(0x160E223F)
val ClayShadowOuter = Color(0x120E223F)

// Acentos matte para categorias e estados
val ClayOrangeBolt  = ClayBlueSoft
val ClayMustard     = Color(0xFFF2C94C)
val ClayMintGreen   = Color(0xFF6FCF97)
val ClayPastelPink  = Color(0xFFFF9FAD)
val ClayLavender    = Color(0xFFBB9AF7)
val ClaySkyBlue     = Color(0xFF7EC8E3)

// Categorias — tons saturados matte, cada um distinto
val ClayCategory = mapOf(
    "Pizza"        to Color(0xFFE85C5C),
    "Marisco"      to Color(0xFF7FACD6),
    "Francesinha"  to Color(0xFFD4A76A),
    "Hambúrguer"   to ClayMustard,
    "Sushi"        to ClayMintGreen,
    "Pasta"        to ClayOrangeBolt,
    "Sobremesas"   to ClayPastelPink,
    "Todos"        to ClayBluePale
)

fun getCategoryColor(category: String): Color =
    ClayCategory[category] ?: ClayBluePale

val ClayDarkNavy     = Color(0xFF0A1628)
val ClayOnDark       = Color.White
val ClayOnDarkSecond = Color(0x99FFFFFF)
