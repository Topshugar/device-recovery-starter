package com.example.devicerecovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(applicationContext)
        setContent {
            MaterialTheme {
                DeviceRecoveryApp(sessionManager)
            }
        }
    }
}

@Composable
fun DeviceRecoveryApp(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val startDestination = if (sessionManager.getToken().isNullOrBlank()) "welcome" else "devices"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") { WelcomeScreen(navController) }
        composable("register") { AuthScreen(navController, sessionManager, isRegistration = true) }
        composable("login") { AuthScreen(navController, sessionManager, isRegistration = false) }
        composable("devices") { DeviceManagementScreen(navController, sessionManager) }
    }
}

@Composable
private fun WelcomeScreen(navController: NavHostController) {
    Scaffold { padding ->
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Device Recovery Starter", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Consent-based lost-device recovery",
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                )
                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Create account") }
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                ) { Text("Login") }
            }
        }
    }
}

@Composable
private fun AuthScreen(
    navController: NavHostController,
    sessionManager: SessionManager,
    isRegistration: Boolean,
) {
    val viewModel = remember { AuthViewModel() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                if (isRegistration) "Create account" else "Login",
                style = MaterialTheme.typography.headlineMedium,
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
            Button(
                onClick = {
                    val onSuccess: (String) -> Unit = { token ->
                        sessionManager.saveToken(token)
                        navController.navigate("devices") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                    val onError: (String) -> Unit = { message -> status = message }
                    if (isRegistration) {
                        viewModel.register(email, password, onSuccess, onError)
                    } else {
                        viewModel.login(email, password, onSuccess, onError)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            ) { Text(if (isRegistration) "Register" else "Login") }
            if (status.isNotBlank()) {
                Text(status, modifier = Modifier.padding(top = 16.dp))
            }
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) { Text("Back") }
        }
    }
}

@Composable
private fun DeviceManagementScreen(
    navController: NavHostController,
    sessionManager: SessionManager,
) {
    val service = remember { RetrofitClient.create().create(AuthApiService::class.java) }
    val scope = rememberCoroutineScope()
    val authHeader = sessionManager.authHeader()
    var devices by remember { mutableStateOf<List<DeviceResponse>>(emptyList()) }
    var deviceName by remember { mutableStateOf("") }
    var deviceToken by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    fun loadDevices() {
        if (authHeader.isNullOrBlank()) {
            status = "Your session has expired. Please log in again."
            return
        }
        scope.launch {
            runCatching { service.getDevices(authHeader) }
                .onSuccess {
                    devices = it
                    status = "Loaded ${it.size} device(s)"
                }
                .onFailure { status = it.localizedMessage ?: "Could not load devices" }
        }
    }

    LaunchedEffect(authHeader) {
        if (authHeader.isNullOrBlank()) {
            navController.navigate("welcome") {
                popUpTo("devices") { inclusive = true }
            }
        } else {
            runCatching { service.getDevices(authHeader) }
                .onSuccess { devices = it }
                .onFailure { status = it.localizedMessage ?: "Could not load devices" }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("My devices", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                label = { Text("Device name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            OutlinedTextField(
                value = deviceToken,
                onValueChange = { deviceToken = it },
                label = { Text("Device token") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
            Button(
                onClick = {
                    if (authHeader.isNullOrBlank() || deviceName.isBlank() || deviceToken.isBlank()) {
                        status = "Device name and token are required"
                    } else {
                        scope.launch {
                            runCatching {
                                service.createDevice(
                                    authHeader,
                                    DeviceCreateRequest(deviceName.trim(), "android", deviceToken.trim()),
                                )
                            }.onSuccess {
                                deviceName = ""
                                deviceToken = ""
                                status = "Device registered"
                                loadDevices()
                            }.onFailure {
                                status = it.localizedMessage ?: "Device registration failed"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) { Text("Register device") }
            Button(
                onClick = {
                    sessionManager.clearToken()
                    navController.navigate("welcome") {
                        popUpTo("devices") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) { Text("Logout") }
            if (status.isNotBlank()) {
                Text(status, modifier = Modifier.padding(top = 16.dp))
            }
            devices.forEach { device ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text("${device.name} (${device.platform})")
                    Text("Lost: ${device.is_lost}")
                    Button(
                        onClick = {
                            if (!authHeader.isNullOrBlank()) {
                                scope.launch {
                                    runCatching { service.markDeviceLost(authHeader, device.id) }
                                        .onSuccess {
                                            status = "Device marked as lost"
                                            loadDevices()
                                        }
                                        .onFailure {
                                            status = it.localizedMessage ?: "Could not mark device as lost"
                                        }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) { Text("Mark lost") }
                }
            }
        }
    }
}
