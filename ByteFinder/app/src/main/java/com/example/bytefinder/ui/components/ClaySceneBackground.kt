package com.example.bytefinder.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color

/**
 * ClaySceneBackground — Fundo sólido dark navy.
 *
 * Cor sólida pura: o contraste total entre o fundo escuro e os cards
 * brancos/coloridos é o que cria o efeito Claymorphism mais forte.
 * Sem gradientes, sem blobs — máximo impacto visual.
 */
fun Modifier.claySceneBackground(): Modifier = this
    .drawWithCache {
        val bg = Color(0xFF0D1B2A)  // Dark navy sólido
        onDrawBehind { drawRect(bg) }
    }
