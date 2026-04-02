package com.example.bytefinder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytefinder.data.ApiService
import com.example.bytefinder.data.LoginRequest
import com.example.bytefinder.data.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel - Gerencia autenticação e sessão do utilizador
 * Separation of Concerns: UI logic separado da Business logic
 * 
 * State Management: StateFlow para reatividade
 * Memory Safety: Coroutines com escopo vinculado ao ViewModel
 */

data class AuthState(
    val token: String? = null,
    val user: UserDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val csrf_token: String? = null
)

enum class AuthStatus {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}

class AuthViewModel(private val api: ApiService) : ViewModel() {

    // Private mutable state
    private val _authState = MutableStateFlow(AuthState())
    
    // Public read-only state for UI
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Convenience properties
    val isLoggedIn: Boolean
        get() = _authState.value.token != null

    val currentToken: String?
        get() = _authState.value.token

    val currentUser: UserDto?
        get() = _authState.value.user

    /**
     * Attempt login with email/password
     * Updates state reactively
     * 
     * @param email User email
     * @param password User password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                // Update state: loading
                _authState.value = _authState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                // API call
                val response = api.login(LoginRequest(email, password))

                if (response.ok && response.token != null && response.user != null) {
                    // Login success
                    _authState.value = _authState.value.copy(
                        token = response.token,
                        user = response.user,
                        isLoading = false,
                        errorMessage = null,
                        csrf_token = response.csrf_token
                    )
                } else {
                    // Login failed (server returned error)
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = response.error ?: response.message ?: "Falha no login"
                    )
                }
            } catch (e: Exception) {
                // Network or parsing error
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = when (e) {
                        is java.net.SocketTimeoutException -> "Timeout: conexão demorada"
                        is java.net.UnknownHostException -> "Sem internet"
                        else -> e.message ?: "Erro de rede"
                    }
                )
            }
        }
    }

    /**
     * Logout - Clear session data
     * Called by UI when user clicks sign out
     */
    fun logout() {
        _authState.value = AuthState()
    }

    /**
     * Update CSRF token
     * Called when server provides new token
     */
    fun updateCsrfToken(token: String) {
        _authState.value = _authState.value.copy(csrf_token = token)
    }

    /**
     * Clear error message
     * Called when user dismisses error dialog
     */
    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }
}
