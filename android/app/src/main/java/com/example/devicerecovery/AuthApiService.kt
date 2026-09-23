package com.example.devicerecovery

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse
}

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val access_token: String, val token_type: String)
