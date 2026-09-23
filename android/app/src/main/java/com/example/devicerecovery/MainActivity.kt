package com.example.devicerecovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DeviceRecoveryScreen()
                }
            }
        }
    }
}

@Composable
fun DeviceRecoveryScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Device Recovery Starter",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Consent-based location recovery for your own device.",
                modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
            )

            Button(onClick = { /* TODO: navigate to register screen */ }) {
                Text("Register")
            }

            Button(
                onClick = { /* TODO: navigate to login screen */ },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Login")
            }
        }
    }
}
