package com.example.campusconnect

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object GeminiApi {
    private val client = OkHttpClient()
    private const val API_KEY = BuildConfig.GEMINI_API_KEY

    fun sendMessage(
        userMessage: String,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        fetchEvents { events ->
            val eventsInfo = events.joinToString("\n") {
                "Event: ${it.name}, Date: ${it.date}, Description: ${it.description}, Location: ${it.location}"
            }
            val prompt = """
                You are a campus bot. Answer the user's question based on the following information about events:
                $eventsInfo
                
                Question: $userMessage
            """.trimIndent()

            val requestBody = JSONObject()
            val contents = JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt))))
            requestBody.put("contents", contents)

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=$API_KEY")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("GeminiApi", "API call failed", e)
                    onError("Failed to connect. Please check your internet connection.")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        Log.e("GeminiApi", "API call failed with code ${response.code} and body $errorBody")
                        onError("An error occurred (Code: ${response.code}). Please try again.")
                        return
                    }
                    response.body?.string()?.let {
                        try {
                            val jsonObject = JSONObject(it)
                            val candidates = jsonObject.getJSONArray("candidates")
                            if (candidates.length() > 0) {
                                val content = candidates.getJSONObject(0).getJSONObject("content")
                                val parts = content.getJSONArray("parts")
                                if (parts.length() > 0) {
                                    val answer = parts.getJSONObject(0).getString("text")
                                    onResponse(answer)
                                } else {
                                    onResponse("No response text found.")
                                }
                            } else {
                                onResponse("I can't answer that right now.")
                            }
                        } catch (e: Exception) {
                            Log.e("GeminiApi", "Failed to parse response", e)
                            onError("Failed to parse the response.")
                        }
                    } ?: onError("The response was empty.")
                }
            })
        }
    }

    private fun fetchEvents(onResult: (List<EventData>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val events = result.map { document ->
                    document.toObject(EventData::class.java)
                }
                onResult(events)
            }
            .addOnFailureListener { exception ->
                Log.w("GeminiApi", "Error getting documents.", exception)
                onResult(emptyList())
            }
    }
}
