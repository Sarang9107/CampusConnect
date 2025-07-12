package com.example.campusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CampusBotScreen(
    messages: List<ChatMessage>,
    userInput: String,
    onUserInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    suggestedQuestions: List<String>,
    onSuggestedQuestionClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Campus Bot",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF6F6F6))
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageBubble(message)
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Suggested Questions
            if (suggestedQuestions.isNotEmpty()) {
                Text(
                    text = "Suggestions:",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestedQuestions) { question ->
                        OutlinedButton(
                            onClick = { onSuggestedQuestionClick(question) },
                            shape = RoundedCornerShape(50) // Pill shape
                        ) {
                            Text(question)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = onUserInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Ask your question...") }
                )
                Button(onClick = onSendClick) {
                    Text("Ask")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) Color(0xFFD2F8D2) else Color.White
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.Black
            )
        }
    }
}

object MaterialTheme {
    object typography {
        val subtitle2 = androidx.compose.ui.text.TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 0.1.sp
        )
    }
}
