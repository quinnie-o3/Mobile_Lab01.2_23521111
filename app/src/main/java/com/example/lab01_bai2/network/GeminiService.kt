package com.example.lab01_bai2.network

import com.example.lab01_bai2.model.SentimentResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class GeminiService {

    private val client = OkHttpClient()

    fun analyzeSentiment(inputText: String): SentimentResult {
        val jsonBody = JSONObject()
            .put("text", inputText)
            .toString()

        val body = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://10.0.2.2:3000/analyze")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("API error: ${response.code} - ${response.message}")
            }

            val responseBody = response.body?.string()
                ?: throw Exception("Empty response body")

            val resultJson = JSONObject(responseBody)

            return SentimentResult(
                label = resultJson.optString("label", "NEUTRAL"),
                explanation = resultJson.optString("explanation", "")
            )
        }
    }
}