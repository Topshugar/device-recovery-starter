package com.example.devicerecovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val service = RetrofitClient.create().create(AuthApiService::class.java)

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Email and password are required")
            return
        }

        viewModelScope.launch {
            try {
                val response = service.register(RegisterRequest(email.trim(), password))
                if (response.access_token.isNotBlank()) {
                    onSuccess()
                } else {
                    onError("Registration failed")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Email and password are required")
            return
        }

        viewModelScope.launch {
            try {
                val response = service.login(LoginRequest(email.trim(), password))
                if (response.access_token.isNotBlank()) {
                    onSuccess()
                } else {
                    onError("Login failed")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Login failed")
            }
        }
    }
}
