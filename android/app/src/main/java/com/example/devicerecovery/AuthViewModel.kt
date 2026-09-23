package com.example.devicerecovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val service = RetrofitClient.create().create(AuthApiService::class.java)

    fun register(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        submit(email, password, onSuccess, onError) { service.register(RegisterRequest(email.trim(), password)) }
    }

    fun login(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        submit(email, password, onSuccess, onError) { service.login(LoginRequest(email.trim(), password)) }
    }

    private fun submit(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        request: suspend () -> AuthResponse,
    ) {
        if (email.isBlank() || password.length < 8) {
            onError("Enter a valid email and a password of at least 8 characters")
            return
        }
        viewModelScope.launch {
            try {
                val response = request()
                if (response.access_token.isNotBlank()) onSuccess(response.access_token)
                else onError("Authentication failed")
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Network request failed")
            }
        }
    }
}
