package com.example.bytefinder.ui.theme

import androidx.compose.ui.graphics.Color

// ─── PALETA CLAYMORPHISM ─── Azul + Branco ──────────────────────────────────

// Primários — azul do BF
val ClayBlue        = Color(0xFF1B6EC2)   // Azul principal — botões, CTAs
val ClayBlueDeep    = Color(0xFF1453A0)   // Azul profundo — pressed state
val ClayBlueSoft    = Color(0xFF4A90D9)   // Azul suave — variante hover
val ClayBluePale    = Color(0xFFD6E8F8)   // Azul pálido — containers, chips
val ClayBlueLight   = Color(0xFFEBF3FF)   // Azul muito claro — backgrounds

// Neutros brancos
val ClaySurface     = Color(0xFFF4F7FB)   // Fundo off-white (Claymorphism)
val ClayWhite       = Color(0xFFFFFFFF)   // Branco puro — cards
val ClayOffWhite    = Color(0xFFF8FAFF)   // Branco tintado — surfaces
val ClayBeigeSoft   = Color(0xFFF5F0E8)   // Beige suave — fundo cards de pratos

// Compatibilidade (aliases para código existente)
val ClayCream       = ClaySurface
val ClayBeige       = ClayBlueLight
val ClayBeigeDeep   = ClayBluePale
val ClayOrange      = ClayBlue          // Redireciona para azul
val ClayOrangeDeep  = ClayBlueDeep
val ClayPeach       = ClayBluePale

// Texto
val ClayTextDark    = Color(0xFF12213D)   // Azul-escuro — texto primário
val ClayTextMedium  = Color(0xFF445069)   // Slate médio — texto secundário
val ClayTextLight   = Color(0xFF8FA3BF)   // Azul-cinza suave — labels, hints

// Acentos
val ClayGreen       = Color(0xFF34C67A)   // Verde — sucesso
val ClayGreenDeep   = Color(0xFF1E9E5E)   // Verde escuro
val ClayRed         = Color(0xFFE85C5C)   // Vermelho — erros
val ClayRedDeep     = Color(0xFFC93A3A)   // Vermelho escuro
val ClayYellow      = Color(0xFFFFC107)   // Amarelo — ratings/estrelas
val ClayYellowDeep  = Color(0xFFE6A800)   // Amarelo escuro

// Sombras suaves e neutras para evitar halos/artefactos no emulador
val ClayShadowLight = Color(0xCCFFFFFF)   // Highlight branco (bevel topo)
val ClayShadowDark  = Color(0x160E223F)   // Sombra azul-acinzentada subtil
val ClayShadowOuter = Color(0x120E223F)   // Sombra exterior leve

// ─── PALETA CLAY SATURADA MATTE (sem gradientes, cores sólidas) ──────────────

val ClayOrangeBolt  = ClayBlueSoft        // Alias → azul suave (sem laranja)
val ClayMustard     = Color(0xFFF2C94C)   // Amarelo Mostarda
val ClayMintGreen   = Color(0xFF6FCF97)   // Verde Menta
val ClayPastelPink  = Color(0xFFFF9FAD)   // Rosa Pastel
val ClayLavender    = Color(0xFFBB9AF7)   // Lavanda suave
val ClaySkyBlue     = Color(0xFF7EC8E3)   // Azul Céu

// Categorias — tons saturados matte, cada um distinto
val ClayCategory = mapOf(
    "Pizza"        to Color(0xFFE85C5C),  // Vermelho tomate
    "Marisco"      to Color(0xFF7FACD6),  // Azul oceano
    "Francesinha"  to Color(0xFFD4A76A),  // Caramelo dourado
    "Hambúrguer"   to ClayMustard,        // Amarelo mostarda
    "Sushi"        to ClayMintGreen,      // Verde menta
    "Pasta"        to ClayOrangeBolt,     // Azul suave
    "Sobremesas"   to ClayPastelPink,     // Rosa pastel
    "Todos"        to ClayBluePale
)

fun getCategoryColor(category: String): Color =
    ClayCategory[category] ?: ClayBluePale

// ─── SCENE DARK (fundo Claymorphism) ─────────────────────────────────────────

val ClayDarkNavy     = Color(0xFF0A1628)  // Fundo principal escuro
val ClayOnDark       = Color.White        // Texto sobre fundo escuro
val ClayOnDarkSecond = Color(0x99FFFFFF)  // Texto secundário sobre escuro (60% white)
