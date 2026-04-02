package com.example.bytefinder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytefinder.data.ApiService
import com.example.bytefinder.data.LoginRequest
import kotlinx.coroutines.launch

/**
 * LoginScreen - Melhorado com WindowInsets (IME Padding)
 * 
 * Improvements (PROMPT 1):
 * ✅ .imePadding() - Teclado virtual não sobrepõe campos
 * ✅ .verticalScroll() - Conteúdo scrollável se comprimido
 * ✅ .statusBarsPadding() - Respeita status bar
 * ✅ Loading state durante requisição
 */
@Composable
fun LoginScreen(
    api: ApiService,
    onLoggedIn: (token: String, userId: Int, nome: String, role: String, restaurantes: List<Int>) -> Unit
) {
    var email by remember { mutableStateOf("goncalo@teste.com") }
    var password by remember { mutableStateOf("123456") }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()  // ✅ CRÍTICO: Afasta conteúdo quando teclado abre
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())  // ✅ Scroll se teclado comprime
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 28.dp),
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

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "BiteFinder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Encontra o prato certo, já",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(26.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            errorMsg = ""
                        },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(Modifier.height(12.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            errorMsg = ""
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !isLoading
                    )

                    Spacer(Modifier.height(18.dp))

                    // Login Button
                    Button(
                        onClick = {
                            errorMsg = ""
                            if (email.isEmpty() || password.isEmpty()) {
                                errorMsg = "Email e password são obrigatórios"
                                return@Button
                            }
                            
                            isLoading = true
                            scope.launch {
                                try {
                                    val res = api.login(LoginRequest(email, password))
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
                        },
                        enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (isLoading) "A conectar..." else "Entrar",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Error Message Display
                    AnimatedVisibility(
                        visible = errorMsg.isNotBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = errorMsg,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
