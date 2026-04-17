package com.example.bytefinder.ui.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint as NativePaint
import android.graphics.Path as NativePath
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * CLAYMORPHISM — "Triple Shadow" Modifier (Performance Edition)
 *
 * Sombras 2 e 3 (inset) renderizadas com Canvas nativo.
 * A sombra 1 (outer drop shadow) é gerida via Modifier.shadow()
 * pelo componente pai — é hardware-accelerated e gratuita.
 *
 * OTIMIZAÇÃO CHAVE: drawWithCache
 * NativePaint e NativePath são criados UMA VEZ quando o size muda
 * (tipicamente durante layout inicial) e reutilizados em todos os
 * frames seguintes — zero alocações no hot path de draw.
 *
 *  2. SOMBRA INTERNA 1 — Top-Left Highlight (Inset)
 *     Branca a ~55%, blur 8dp → reflexo de luz plástico
 *
 *  3. SOMBRA INTERNA 2 — Bottom-Right Depth (Inset)
 *     Cor escurecida a ~18%, blur 8dp → espessura/volume 3D
 *
 * Técnica: frame/hole com Path.FillType.EVEN_ODD dentro de clipPath
 * → inset shadows reais (não gradientes simulados).
 *
 * @param cornerRadius   Raio dos cantos (mínimo 24dp)
 * @param fillColor      Cor sólida matte (NUNCA gradiente)
 * @param highlightAlpha Opacidade do highlight inset top-left  (0.0–1.0)
 * @param depthAlpha     Opacidade da profundidade inset bottom-right (0.0–1.0)
 */
fun Modifier.claySurface(
    cornerRadius: Dp = 24.dp,
    fillColor: Color = Color.White,
    // ── 2. Inner Top-Left Highlight ──────────────────────────────
    highlightAlpha: Float = 0.55f,
    // ── 3. Inner Bottom-Right Depth ──────────────────────────────
    depthAlpha: Float = 0.18f,
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .drawWithCache {
        // ══════════════════════════════════════════════════════════
        // BLOCO DE CACHE — executado apenas quando o SIZE muda.
        // Todos os objetos Paint/Path são criados aqui e reutilizados
        // em cada frame sem qualquer alocação de heap.
        // ══════════════════════════════════════════════════════════
        val cr = cornerRadius.toPx()
        val insetOffset = 4.dp.toPx()
        val blurR = 8.dp.toPx().coerceAtLeast(1f)
        val depthColor = Color(0xFF0E223F)

        // Paint — background fill (sem blur, muito barato)
        val bgPaint = NativePaint(NativePaint.ANTI_ALIAS_FLAG).apply {
            color = fillColor.toArgb()
            style = NativePaint.Style.FILL
        }

        // Paint — inset highlight (top-left)
        val hlPaint = NativePaint(NativePaint.ANTI_ALIAS_FLAG).apply {
            color = Color.White.copy(alpha = highlightAlpha).toArgb()
            maskFilter = BlurMaskFilter(blurR, BlurMaskFilter.Blur.NORMAL)
        }

        // Paint — inset depth (bottom-right)
        val dpPaint = NativePaint(NativePaint.ANTI_ALIAS_FLAG).apply {
            color = depthColor.copy(alpha = depthAlpha).toArgb()
            maskFilter = BlurMaskFilter(blurR, BlurMaskFilter.Blur.NORMAL)
        }

        // Clip path — fica dentro do shape arredondado
        val clipPath = NativePath().apply {
            addRoundRect(0f, 0f, size.width, size.height, cr, cr, NativePath.Direction.CW)
        }

        // Frame EVEN_ODD: hole deslocado (+off,+off) → gap no topo-esquerda
        val hlFrame = NativePath().apply {
            fillType = NativePath.FillType.EVEN_ODD
            addRect(-200f, -200f, size.width + 200f, size.height + 200f, NativePath.Direction.CW)
            addRoundRect(
                insetOffset, insetOffset,
                size.width + insetOffset, size.height + insetOffset,
                cr, cr, NativePath.Direction.CW
            )
        }

        // Frame EVEN_ODD: hole deslocado (-off,-off) → gap no fundo-direita
        val dpFrame = NativePath().apply {
            fillType = NativePath.FillType.EVEN_ODD
            addRect(-200f, -200f, size.width + 200f, size.height + 200f, NativePath.Direction.CW)
            addRoundRect(
                -insetOffset, -insetOffset,
                size.width - insetOffset, size.height - insetOffset,
                cr, cr, NativePath.Direction.CW
            )
        }

        // ══════════════════════════════════════════════════════════
        // DRAW LAMBDA — chamado em cada frame. Usa apenas objetos
        // pré-alocados acima, sem new/alloc aqui.
        // ══════════════════════════════════════════════════════════
        onDrawWithContent {
            val canvas = drawContext.canvas.nativeCanvas

            // Background sólido matte
            canvas.drawRoundRect(0f, 0f, size.width, size.height, cr, cr, bgPaint)

            // Conteúdo composable (imagens, textos, etc.)
            drawContent()

            // Inset highlight — top-left (luz plástica)
            canvas.save()
            canvas.clipPath(clipPath)
            canvas.drawPath(hlFrame, hlPaint)
            canvas.restore()

            // Inset depth — bottom-right (volume 3D)
            canvas.save()
            canvas.clipPath(clipPath)
            canvas.drawPath(dpFrame, dpPaint)
            canvas.restore()
        }
    }
