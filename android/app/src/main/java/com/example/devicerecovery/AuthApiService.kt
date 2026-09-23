package com.example.devicerecovery

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("api/devices")
    suspend fun getDevices(@Header("Authorization") token: String): List<DeviceResponse>

    @POST("api/devices")
    suspend fun createDevice(@Header("Authorization") token: String, @Body body: DeviceCreateRequest): DeviceResponse

    @POST("api/devices/{deviceId}/location")
    suspend fun uploadLocation(@Header("Authorization") token: String, @Path("deviceId") deviceId: Int, @Body body: LocationRequest): Map<String, String>

    @POST("api/devices/{deviceId}/mark-lost")
    suspend fun markDeviceLost(@Header("Authorization") token: String, @Path("deviceId") deviceId: Int): Map<String, String>
}

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val access_token: String, val token_type: String)
data class DeviceCreateRequest(val name: String, val platform: String = "android", val device_token: String)
data class DeviceResponse(val id: Int, val name: String, val platform: String, val device_token: String, val is_active: Boolean, val is_lost: Boolean, val created_at: String)
data class LocationRequest(val latitude: Double, val longitude: Double, val accuracy_meters: Double = 0.0, val source: String = "app")
