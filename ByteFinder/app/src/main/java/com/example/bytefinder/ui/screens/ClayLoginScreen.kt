package com.example.bytefinder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.data.DataRepository
import com.example.bytefinder.ui.components.ClayButton
import com.example.bytefinder.ui.components.ClayCard
import com.example.bytefinder.ui.components.claySceneBackground
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
    var successMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotSent by remember { mutableStateOf(false) }
    // Register fields
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
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
            .claySceneBackground()
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
                        text = if (isRegisterMode) "Criar nova conta" else "Encontra o prato certo",
                        color = ClayTextMedium,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    if (!isRegisterMode) {
                        // ═══════ LOGIN MODE ═══════

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMsg = "" },
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

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMsg = "" },
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

                        // "Esqueceu-se da palavra-passe?"
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Esqueceu-se da palavra-passe?",
                                color = ClayBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    forgotEmail = email
                                    forgotSent = false
                                    showForgotDialog = true
                                }
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        ClayButton(
                            text = if (isLoading) "A entrar..." else "Entrar",
                            onClick = { performLogin() },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        )

                        // Error
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

                        Spacer(Modifier.height(20.dp))

                        // Separator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(ClayBluePale)
                            )
                            Text(
                                text = "  ou entrar com  ",
                                color = ClayTextLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(ClayBluePale)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SocialButton("Google") { /* TODO: connect to AuthViewModel */ }
                            SocialButton("Apple") { /* TODO: connect to AuthViewModel */ }
                            SocialButton("Facebook") { /* TODO: connect to AuthViewModel */ }
                            SocialButton("Microsoft") { /* TODO: connect to AuthViewModel */ }
                        }

                        Spacer(Modifier.height(16.dp))

                        // "Criar Conta" button
                        ClayButton(
                            text = "Criar Conta",
                            onClick = {
                                isRegisterMode = true
                                errorMsg = ""
                                successMsg = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            containerColor = ClayBlueLight,
                            contentColor = ClayBlue
                        )


                    } else {
                        // ═══════ REGISTER MODE ═══════

                        OutlinedTextField(
                            value = registerName,
                            onValueChange = { registerName = it; errorMsg = "" },
                            label = { Text("Nome completo") },
                            placeholder = { Text("Ex: João Silva") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = clayTextFieldColors(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = registerEmail,
                            onValueChange = { registerEmail = it; errorMsg = "" },
                            label = { Text("Email") },
                            placeholder = { Text("exemplo@email.com") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = clayTextFieldColors(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = registerPassword,
                            onValueChange = { registerPassword = it; errorMsg = "" },
                            label = { Text("Palavra-passe") },
                            placeholder = { Text("Mín. 6 caracteres") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = clayTextFieldColors(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = registerConfirmPassword,
                            onValueChange = { registerConfirmPassword = it; errorMsg = "" },
                            label = { Text("Confirmar palavra-passe") },
                            placeholder = { Text("Repita a palavra-passe") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = clayTextFieldColors(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                        )

                        Spacer(Modifier.height(22.dp))

                        ClayButton(
                            text = if (isLoading) "A criar conta..." else "Criar Conta",
                            onClick = {
                                errorMsg = ""
                                if (registerName.isBlank() || registerEmail.isBlank() || registerPassword.isBlank()) {
                                    errorMsg = "Preenche todos os campos"
                                    return@ClayButton
                                }
                                if (!registerEmail.contains("@") || !registerEmail.contains(".")) {
                                    errorMsg = "Email inválido"
                                    return@ClayButton
                                }
                                if (registerPassword.length < 6) {
                                    errorMsg = "A palavra-passe deve ter pelo menos 6 caracteres"
                                    return@ClayButton
                                }
                                if (registerPassword != registerConfirmPassword) {
                                    errorMsg = "As palavras-passe não coincidem"
                                    return@ClayButton
                                }
                                isLoading = true
                                scope.launch {
                                    kotlinx.coroutines.delay(1200)
                                    isLoading = false
                                    successMsg = "Conta criada com sucesso! Faça login."
                                    email = registerEmail
                                    password = ""
                                    isRegisterMode = false
                                    registerName = ""
                                    registerEmail = ""
                                    registerPassword = ""
                                    registerConfirmPassword = ""
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        )

                        // Error in register
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

                        Spacer(Modifier.height(20.dp))

                        // Back to login
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Já tem conta? ",
                                color = ClayTextMedium,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Entrar",
                                color = ClayBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    isRegisterMode = false
                                    errorMsg = ""
                                }
                            )
                        }
                    }

                    // Success message
                    AnimatedVisibility(
                        visible = successMsg.isNotBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = successMsg,
                                color = ClayGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // ═══════ FORGOT PASSWORD DIALOG ═══════
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Text(
                    "Recuperar palavra-passe",
                    fontWeight = FontWeight.Bold,
                    color = ClayTextDark
                )
            },
            text = {
                if (forgotSent) {
                    Text(
                        "Se o email existir na nossa base de dados, receberá um link para redefinir a palavra-passe.",
                        color = ClayTextMedium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                } else {
                    Column {
                        Text(
                            "Introduza o seu email e enviaremos um link para redefinir a sua palavra-passe.",
                            color = ClayTextMedium,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = forgotEmail,
                            onValueChange = { forgotEmail = it },
                            label = { Text("Email") },
                            placeholder = { Text("exemplo@email.com") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = clayTextFieldColors(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            )
                        )
                    }
                }
            },
            confirmButton = {
                if (forgotSent) {
                    TextButton(onClick = { showForgotDialog = false }) {
                        Text("OK", color = ClayBlue, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(
                        onClick = {
                            if (forgotEmail.isNotBlank() && forgotEmail.contains("@")) {
                                forgotSent = true
                            }
                        }
                    ) {
                        Text("Enviar", color = ClayBlue, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!forgotSent) {
                    TextButton(onClick = { showForgotDialog = false }) {
                        Text("Cancelar", color = ClayTextMedium)
                    }
                }
            },
            containerColor = ClayWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun SocialButton(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(com.example.bytefinder.ui.theme.ClayBluePale)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(name, fontSize = 12.sp, color = com.example.bytefinder.ui.theme.ClayBlue, fontWeight = FontWeight.Bold)
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
