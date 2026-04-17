package com.example.bytefinder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.ui.components.ClayButton
import com.example.bytefinder.ui.components.ClayCard
import com.example.bytefinder.ui.theme.*
import kotlinx.coroutines.launch

/**
 * LoginScreen — Ecrã de autenticação com estilo Claymorphism.
 *
 * Funcionalidades:
 * - Login com email/password via DataRepository (API ou Mock)
 * - Animação spring de entrada do card
 * - Validação visual com mensagem de erro animada
 * - Keyboard-aware (imePadding + scroll)
 */
@Composable
fun ClayLoginScreen(
    repository: DataRepository,
    onLoggedIn: (token: String, userId: Int, nome: String, role: String, restaurantes: List<Int>) -> Unit
) {
    var email by remember { mutableStateOf("goncalo@teste.com") }
    var password by remember { mutableStateOf("123456") }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Animação de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "login-scale"
    )

    /**
     * Função de login reutilizada (DRY — evita duplicação entre botão e teclado).
     */
    fun performLogin() {
        if (email.isBlank() || password.isBlank()) {
            errorMsg = "Preenche todos os campos"
            return
        }
        isLoading = true
        errorMsg = ""
        scope.launch {
            try {
                val res = repository.login(email, password)
                if (res.ok && res.token != null && res.user != null) {
                    errorMsg = ""
                    onLoggedIn(
                        res.token,
                        res.user.id,
                        res.user.nome,
                        res.user.role,
                        res.user.restaurantes
                    )
                } else {
                    errorMsg = res.error ?: res.message ?: "Falha no login"
                }
            } catch (e: Exception) {
                errorMsg = e.message ?: "Erro de rede"
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClaySurface)
            .statusBarsPadding()
            .imePadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = ClayWhite,
                cornerRadius = 28.dp,
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    AsyncImage(
                        model = "https://bitefinderstorage.blob.core.windows.net/icons/logo-bf.png",
                        contentDescription = "Logo BiteFinder",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "BiteFinder",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = ClayBlue
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Encontra o prato certo, já 🍽",
                        color = ClayTextMedium,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    // Campo de email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        placeholder = { Text("exemplo@email.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Campo de email para login" },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = clayTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(Modifier.height(14.dp))

                    // Campo de password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("••••••") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Campo de palavra-passe para login" },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = clayTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { performLogin() }
                        )
                    )

                    Spacer(Modifier.height(22.dp))

                    // Botão de login
                    ClayButton(
                        text = if (isLoading) "A entrar..." else "Entrar",
                        onClick = { performLogin() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    // Mensagem de erro animada
                    AnimatedVisibility(
                        visible = errorMsg.isNotBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = errorMsg,
                                color = ClayRedDeep,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Ajuda: credenciais de teste
                    ClayCard(
                        backgroundColor = ClayBlueLight,
                        cornerRadius = 12.dp,
                        elevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Contas de demonstração:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = ClayTextMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "👤 goncalo@teste.com / 123456",
                                fontSize = 11.sp,
                                color = ClayTextLight
                            )
                            Text(
                                "🏪 restaurante@teste.com / 123456",
                                fontSize = 11.sp,
                                color = ClayTextLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun clayTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ClayBlue,
    unfocusedBorderColor = ClayBluePale,
    focusedLabelColor = ClayBlue,
    unfocusedLabelColor = ClayTextLight,
    cursorColor = ClayBlue,
    focusedContainerColor = ClayWhite,
    unfocusedContainerColor = ClayWhite
)
