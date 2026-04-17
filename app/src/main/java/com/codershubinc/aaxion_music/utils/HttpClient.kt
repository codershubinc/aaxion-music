package com.codershubinc.aaxion_music.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object HttpClient {

    suspend fun postJson(urlStr: String, jsonPayload: JSONObject, token: String? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer $token")
                }
                conn.doOutput = true

                conn.outputStream.use { os ->
                    val input = jsonPayload.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    Result.success(response)
                } else {
                    val errorStream: InputStream? = conn.errorStream
                    val errorResponse = errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                    Result.failure(Exception("HTTP $responseCode: $errorResponse"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    suspend fun get(urlStr: String, token: String? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/json")
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer $token")
                }

                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    Result.success(response)
                } else {
                    val errorStream: InputStream? = conn.errorStream
                    val errorResponse = errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                    Result.failure(Exception("HTTP $responseCode: $errorResponse"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
}

