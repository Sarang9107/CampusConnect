package com.example.campusconnect



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatbotScreen() {
    var userMessage by remember { mutableStateOf("") }
    var botResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Ask the Campus Bot", style = MaterialTheme.typography.h6)

        OutlinedTextField(
            value = userMessage,
            onValueChange = { userMessage = it },
            label = { Text("Your Question") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                GeminiApi.sendMessage(
                    userMessage,
                    onResponse = {
                        botResponse = it
                        isLoading = false
                    },
                    onError = {
                        botResponse = it
                        isLoading = false
                    }
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Ask")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Text("Loading...", style = MaterialTheme.typography.body1)
        } else {
            Text("Response:", style = MaterialTheme.typography.subtitle1)
            Text(botResponse)
        }
    }
}
