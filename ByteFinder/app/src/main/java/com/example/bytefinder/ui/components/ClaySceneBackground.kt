package com.example.bytefinder.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
        val bg = Brush.linearGradient(
            colors = listOf(
                Color(0xFF061121),
                Color(0xFF0A1628),
                Color(0xFF102844)
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
        onDrawBehind { drawRect(bg) }
    }
