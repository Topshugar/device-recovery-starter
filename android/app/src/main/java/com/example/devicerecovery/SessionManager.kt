package com.example.devicerecovery

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("device_recovery_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("jwt_token", token).apply()

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun clearToken() = prefs.edit().remove("jwt_token").apply()

    fun authHeader(): String? = getToken()?.takeUnless { it.isBlank() }?.let { "Bearer $it" }
}
