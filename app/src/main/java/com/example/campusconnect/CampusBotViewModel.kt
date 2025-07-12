package com.example.campusconnect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CampusBotViewModel : ViewModel() {
    var messages by mutableStateOf<List<ChatMessage>>(emptyList())
    var userInput by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Predefined answers map
    private val predefinedAnswers = mapOf(
        "hi" to "Hello there! How can I help you today?",
        "hello" to "Hi! What can I do for you?",
        "how are you?" to "I'''m doing well, thank you for asking!",
        "what is your name?" to "I am CampusBot, your friendly assistant.",
        "what events are happening?" to "You can check the dashboard for a list of current events!",
        "tell me about the library" to "The library is open from 9 AM to 8 PM on weekdays and 10 AM to 6 PM on weekends. It'''s a great place to study!",
        "where is the cafeteria?" to "The main cafeteria is located on the ground floor of the Student Union building.",
        "bye" to "Goodbye! Have a great day!",
        "default" to "I'''m sorry, I don'''t have an answer for that right now. Please try asking something else."
    ).mapKeys { it.key.lowercase() }

    val suggestedQuestions: List<String> = predefinedAnswers.keys.filter { it != "default" }.map { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } + "?" }


    fun onUserInputChange(input: String) {
        userInput = input
    }

    fun onSuggestedQuestionClick(question: String) {
        val key = question.removeSuffix("?").lowercase()
        userInput = key
        sendMessage()
    }

    fun sendMessage() {
        if (userInput.isBlank()) return

        val question = userInput
        messages = messages + ChatMessage(question.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } + if (!question.endsWith("?")) "?" else "", true) // Display formatted question
        userInput = ""
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            delay(1000) // 1 second delay

            val answer = predefinedAnswers[question] ?: predefinedAnswers["default"]
            
            isLoading = false
            messages = messages + ChatMessage(answer!!, false) // Add bot'''s predefined answer
        }

        /*
        GeminiApi.sendMessage(
            userMessage = question,
            onResponse = { answer ->
                isLoading = false
                messages = messages + ChatMessage(answer, false)
            },
            onError = { error ->
                isLoading = false
                errorMessage = error
            }
        )
        */
    }
}
