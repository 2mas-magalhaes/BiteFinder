package com.example.bytefinder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bytefinder.data.ApiService

/**
 * Factory para criação de ViewModels com injeção de dependências
 * Padrão: Factory Pattern para ViewModels complexos
 */

class AuthViewModelFactory(private val api: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                AuthViewModel(api) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
