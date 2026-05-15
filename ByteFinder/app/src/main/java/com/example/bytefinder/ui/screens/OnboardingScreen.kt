package com.example.bytefinder.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.ui.components.BiteButton
import com.example.bytefinder.ui.components.claySceneBackground
import com.example.bytefinder.ui.theme.ClayBlue
import com.example.bytefinder.ui.theme.ClayBlueSoft
import com.example.bytefinder.ui.theme.ClayOnDark
import com.example.bytefinder.ui.theme.ClayOnDarkSecond

private data class OnboardingPage(
    val title: String,
    val body: String,
    val imageUrl: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Descobre pratos, não só restaurantes.",
        body = "Escolhe pelo que realmente interessa: sabor, rating, preço, distância e contexto.",
        imageUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1400&q=85"
    ),
    OnboardingPage(
        title = "Compara antes de decidir.",
        body = "Vê a melhor versão do mesmo prato em vários restaurantes e decide com confiança.",
        imageUrl = "https://images.unsplash.com/photo-1551218808-94e220e084d2?auto=format&fit=crop&w=1400&q=85"
    ),
    OnboardingPage(
        title = "Guia inteligente para comer melhor.",
        body = "BiteFinder combina reviews, localização e pratos em destaque numa experiência premium.",
        imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1400&q=85"
    )
)

@Composable
fun BiteOnboardingScreen(onFinish: () -> Unit) {
    var pageIndex by remember { mutableIntStateOf(0) }
    val page = onboardingPages[pageIndex]
    val biteEase = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
    ) {
        AnimatedContent(
            targetState = page,
            transitionSpec = { fadeIn(tween(260, easing = biteEase)) togetherWith fadeOut(tween(180, easing = biteEase)) },
            label = "onboarding-page"
        ) { current ->
            AsyncImage(
                model = current.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99061121),
                            Color(0x33061121),
                            Color(0xF0061121)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BiteFinder", color = ClayOnDark, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text(
                    "Saltar",
                    color = ClayOnDarkSecond,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onFinish() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    page.title,
                    color = ClayOnDark,
                    fontSize = 34.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    page.body,
                    color = ClayOnDarkSecond,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(22.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    onboardingPages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(width = if (index == pageIndex) 28.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (index == pageIndex) ClayBlueSoft else Color.White.copy(alpha = 0.32f))
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                BiteButton(
                    text = if (pageIndex == onboardingPages.lastIndex) "Entrar na BiteFinder" else "Continuar",
                    onClick = {
                        if (pageIndex == onboardingPages.lastIndex) onFinish() else pageIndex++
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = ClayBlue,
                    contentColor = ClayOnDark
                )
            }
        }
    }
}
