package com.example.campusconnect

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object GeminiApi {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro-002:generateContent"
    private val client = OkHttpClient()

    fun sendMessage(
        userMessage: String,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiKey = "AIzaSyAeZfFNsz4AQXDLD9680fMq9-zSfpzGmpo"

        val urlWithKey = "$BASE_URL?key=$apiKey"

        val jsonObject = JSONObject()
        val part = JSONObject()
        part.put("text", userMessage)
        val partsArray = JSONArray().put(part)
        val content = JSONObject()
        content.put("parts", partsArray)
        val contentsArray = JSONArray().put(content)
        jsonObject.put("contents", contentsArray)

        val genConfig = JSONObject()
        genConfig.put("maxOutputTokens", 256)
        genConfig.put("temperature", 1)
        jsonObject.put("generationConfig", genConfig)

        val mediaType = "application/json".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(urlWithKey)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                if (response.isSuccessful && bodyString != null) {
                    try {
                        val json = JSONObject(bodyString)
                        val candidates = json.getJSONArray("candidates")
                        if (candidates.length() > 0) {
                            val firstCandidate = candidates.getJSONObject(0)
                            val contentObject = firstCandidate.getJSONObject("content")
                            val partsArrayResponse = contentObject.getJSONArray("parts")
                            if (partsArrayResponse.length() > 0) {
                                val firstPart = partsArrayResponse.getJSONObject(0)
                                val textResponse = firstPart.getString("text")
                                onResponse(textResponse)
                            } else {
                                onError("Failed to parse response: 'parts' array is empty. (Body: $bodyString)")
                            }
                        } else {
                            onError("Failed to parse response: 'candidates' array is empty. (Body: $bodyString)")
                        }
                    } catch (e: Exception) {
                        onError("Failed to parse response: ${e.message} (Body: $bodyString)")
                    }
                } else {
                    onError("API error: ${response.code} - (Body: $bodyString)")
                }
            }
        })
    }
}
