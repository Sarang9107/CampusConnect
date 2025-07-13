package com.example.campusconnect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CampusBotViewModel : ViewModel() {
    var messages by mutableStateOf<List<ChatMessage>>(emptyList())
    var userInput by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val predefinedAnswers = mapOf(
        "hi" to "Hello there! How can I help you today?",
        "hello" to "Hi! What can I do for you?",
        "how are you?" to "I'm doing well, thank you for asking!",
        "what is your name?" to "I am CampusBot, your friendly assistant.",
        "what events are happening?" to "You can check the dashboard for a list of current events!",
        "bye" to "Goodbye! Have a great day!",
        "default" to "I'm sorry, I don't have an answer for that right now. Please try asking something else."
    ).mapKeys { it.key.lowercase() }

    val suggestedQuestions: List<String> = predefinedAnswers.keys.filter { it != "default" }.map { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } + "?" }


    fun onUserInputChange(input: String) {
        userInput = input
    }

    fun onSuggestedQuestionClick(question: String) {
        val processedQuestion = question.removeSuffix("?").lowercase()
        userInput = processedQuestion
        sendMessage()
    }

    fun sendMessage() {
        if (userInput.isBlank()) return

        val questionToSend = userInput
        messages = messages + ChatMessage(questionToSend.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } + if (!questionToSend.endsWith("?")) "?" else "", true)
        userInput = ""
        isLoading = true
        errorMessage = null

        // Call GeminiApi
        GeminiApi.sendMessage(
            userMessage = questionToSend,
            onResponse = { answer ->
                messages = messages + ChatMessage(answer, false)
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }
}
