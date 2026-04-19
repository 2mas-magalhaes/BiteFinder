package com.example.bytefinder.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.components.BrandHeader
import com.example.bytefinder.ui.components.ClayButton
import com.example.bytefinder.ui.components.ClayCard
import com.example.bytefinder.ui.components.claySceneBackground
import com.example.bytefinder.ui.theme.*

@Composable
fun ClaySettingsScreen(
    onBack: () -> Unit,
    onGoHome: () -> Unit
) {
    val context = LocalContext.current

    var expandedSection by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* result handled by system */ }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .claySceneBackground()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandHeader(onGoHome = onGoHome)
            Spacer(Modifier.weight(1f))
            ClayButton(text = "← Voltar", onClick = onBack, isSecondary = true)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Definições",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = ClayOnDark
        )

        Spacer(Modifier.height(20.dp))

        // ─── Permissões ─────────────────────────────────────────────────
        SettingsSection(
            icon = Icons.Filled.Security,
            title = "Permissões",
            description = "Gerir permissões da aplicação",
            isExpanded = expandedSection == "permissions",
            onToggle = { expandedSection = if (expandedSection == "permissions") null else "permissions" }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PermissionRow(
                    title = "Localização",
                    description = "Necessária para encontrar restaurantes perto de ti",
                    onRequest = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

                PermissionRow(
                    title = "Notificações",
                    description = "Para receber alertas de novas avaliações e promoções",
                    onRequest = {
                        permissionLauncher.launch(
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                        )
                    }
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

                // Open app settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ClayBlue.copy(alpha = 0.15f))
                        .clickable {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            )
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = ClayBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Abrir definições do sistema",
                        color = ClayBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── FAQ ────────────────────────────────────────────────────────
        SettingsSection(
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            title = "Perguntas Frequentes",
            description = "Dúvidas comuns sobre a app",
            isExpanded = expandedSection == "faq",
            onToggle = { expandedSection = if (expandedSection == "faq") null else "faq" }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FaqItem(
                    question = "Como funciona a pesquisa?",
                    answer = "A pesquisa filtra pratos e restaurantes com base na tua localização atual. Podes pesquisar por nome do prato, tipo de comida ou nome do restaurante."
                )
                FaqItem(
                    question = "Como deixo uma avaliação?",
                    answer = "Vai à página de detalhe de um prato, seleciona as estrelas e escreve o teu comentário. A avaliação aparece imediatamente."
                )
                FaqItem(
                    question = "Posso editar a minha avaliação?",
                    answer = "Sim! Na página de detalhe do prato, podes editar ou eliminar a tua avaliação a qualquer momento."
                )
                FaqItem(
                    question = "A localização é obrigatória?",
                    answer = "Não, mas recomendamos ativar para veres restaurantes perto de ti. Sem localização, verás todos os restaurantes disponíveis."
                )
                FaqItem(
                    question = "Como é calculado o rating?",
                    answer = "O rating é a média de todas as avaliações dos utilizadores. Quando um prato está em vários restaurantes, mostramos a média de todos."
                )
                FaqItem(
                    question = "Os meus dados estão seguros?",
                    answer = "Sim, utilizamos encriptação e seguimos as melhores práticas de segurança. Os teus dados nunca são partilhados com terceiros."
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── Notificações ───────────────────────────────────────────────
        SettingsSection(
            icon = Icons.Filled.Notifications,
            title = "Notificações",
            description = "Gerir alertas e notificações",
            isExpanded = expandedSection == "notifications",
            onToggle = { expandedSection = if (expandedSection == "notifications") null else "notifications" }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "As notificações push serão implementadas em breve. Fica atento às atualizações!",
                    color = ClayOnDarkSecond,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── Sobre Nós ──────────────────────────────────────────────────
        SettingsSection(
            icon = Icons.Filled.Info,
            title = "Sobre Nós",
            description = "Conhece a equipa BiteFinder",
            isExpanded = expandedSection == "about",
            onToggle = { expandedSection = if (expandedSection == "about") null else "about" }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "BiteFinder",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = ClayOnDark
                )
                Text(
                    text = "A tua app para descobrir os melhores pratos perto de ti.",
                    color = ClayOnDarkSecond,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                Text(
                    text = "O BiteFinder ajuda-te a encontrar e comparar pratos em diferentes restaurantes da tua cidade. " +
                            "Consulta avaliações de outros utilizadores, descobre novos sabores e partilha a tua opinião.",
                    color = ClayOnDarkSecond,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

                // App info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Versão", color = ClayOnDarkSecond, fontSize = 13.sp)
                    Text("1.0.0", color = ClayOnDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Plataforma", color = ClayOnDarkSecond, fontSize = 13.sp)
                    Text("Android", color = ClayOnDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Desenvolvido por", color = ClayOnDarkSecond, fontSize = 13.sp)
                    Text("Equipa BiteFinder", color = ClayOnDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "© 2025 BiteFinder. Todos os direitos reservados.",
                    color = ClayOnDarkSecond.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─── Componentes auxiliares ─────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    icon: ImageVector,
    title: String,
    description: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF111E30),
        cornerRadius = 20.dp,
        elevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ClayBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = ClayOnDark
                    )
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = ClayOnDarkSecond
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = ClayOnDarkSecond,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                    content()
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    description: String,
    onRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = ClayOnDark
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = ClayOnDarkSecond
            )
        }
        Spacer(Modifier.width(12.dp))
        ClayButton(
            text = "Pedir",
            onClick = onRequest,
            modifier = Modifier.height(36.dp)
        )
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ClayOnDark,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = ClayOnDarkSecond,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                color = ClayOnDarkSecond,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
