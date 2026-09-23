package com.example.devicerecovery

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
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

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
    val startDestination = if (sessionManager.getToken() != null) "devices" else "welcome"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController, sessionManager)
        }
        composable("login") {
            LoginScreen(navController, sessionManager)
        }
        composable("devices") {
            DeviceListScreen(navController, sessionManager)
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Scaffold { padding ->
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Device Recovery Starter", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Consent-based lost-device recovery",
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
                )

                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create account")
                }

                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    sessionManager: SessionManager
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Create account", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            Button(
                onClick = {
                    viewModel.register(
                        email = email,
                        password = password,
                        onSuccess = { token ->
                            sessionManager.saveToken(token)
                            status = "Account created successfully"
                            navController.navigate("devices") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        onError = { message -> status = message }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text("Register")
            }

            if (status.isNotBlank()) {
                Text(text = status, modifier = Modifier.padding(top = 16.dp))
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    sessionManager: SessionManager
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            Button(
                onClick = {
                    viewModel.login(
                        email = email,
                        password = password,
                        onSuccess = { token ->
                            sessionManager.saveToken(token)
                            status = "Login successful"
                            navController.navigate("devices") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        onError = { message -> status = message }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text("Login")
            }

            if (status.isNotBlank()) {
                Text(text = status, modifier = Modifier.padding(top = 16.dp))
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun DeviceListScreen(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    val service = remember { RetrofitClient.create().create(AuthApiService::class.java) }
    var devices by remember { mutableStateOf<List<DeviceResponse>>(emptyList()) }
    var status by remember { mutableStateOf("") }
    var deviceName by remember { mutableStateOf("") }
    var deviceToken by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val authHeader = sessionManager.authHeader() ?: ""

    fun loadDevices() {
        if (authHeader.isBlank()) {
            status = "No active session"
            return
        }

        scope.launch {
            try {
                devices = service.getDevices(authHeader)
                status = "Loaded ${devices.size} device(s)"
            } catch (e: Exception) {
                status = e.localizedMessage ?: "Could not load devices"
            }
        }
    }

    // Load devices on screen start.
    scope.launch {
        try {
            if (authHeader.isNotBlank()) {
                devices = service.getDevices(authHeader)
            }
        } catch (e: Exception) {
            status = e.localizedMessage ?: "Could not load devices"
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("My devices", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                label = { Text("Device name") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
                value = deviceToken,
                onValueChange = { deviceToken = it },
                label = { Text("Device token") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            Button(
                onClick = {
                    if (authHeader.isBlank()) {
                        status = "Please login again"
                        return@Button
                    }

                    scope.launch {
                        try {
                            val created = service.createDevice(
                                authHeader,
                                DeviceCreateRequest(
                                    name = deviceName,
                                    platform = "android",
                                    device_token = deviceToken
                                )
                            )
                            status = "Device created: ${created.name}"
                            loadDevices()
                        } catch (e: Exception) {
                            status = e.localizedMessage ?: "Device creation failed"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Register device")
            }

            Button(
                onClick = {
                    sessionManager.clearToken()
                    navController.navigate("welcome") {
                        popUpTo("devices") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Logout")
            }

            if (status.isNotBlank()) {
                Text(text = status, modifier = Modifier.padding(top = 16.dp))
            }

            if (devices.isNotEmpty()) {
                devices.forEach { device ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text("Name: ${device.name}")
                        Text("Platform: ${device.platform}")
                        Text("Token: ${device.device_token}")
                        Text("Lost: ${device.is_lost}")

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        service.markDeviceLost(authHeader, device.id)
                                        status = "Device marked as lost"
                                        loadDevices()
                                    } catch (e: Exception) {
                                        status = e.localizedMessage ?: "Device mark-lost failed"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Mark lost")
                        }
                    }
                }
            }
        }
    }
}
