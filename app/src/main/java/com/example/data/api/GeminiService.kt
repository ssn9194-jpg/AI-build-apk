package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(
        history: List<ChatMessage>,
        newMessage: String,
        selectedModel: String = GeminiConstants.MODEL_PRO,
        customApiKey: String? = null,
        enableHighThinking: Boolean = true,
        customSystemPrompt: String = GeminiConstants.DEFAULT_SYSTEM_INSTRUCTION,
        temperature: Float = 0.7f
    ): Result<Pair<String, String?>> = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey.trim()
            BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isEmpty()) {
            return@withContext Result.failure(
                IllegalStateException("کلید API جمینای تنظیم نشده است. لطفاً در بخش «تنظیمات ای‌پی‌آی» کلید اختصاصی خود را وارد نمایید یا آن را در تنظیمات پروژه اضافه کنید.")
            )
        }

        try {
            val url = "${GeminiConstants.BASE_URL}$selectedModel:generateContent?key=$apiKey"

            // Construct contents array for multi-turn conversation
            val contentsArray = JSONArray()

            // Add previous message history
            for (msg in history) {
                val role = if (msg.role == "user") "user" else "model"
                val contentObj = JSONObject()
                contentObj.put("role", role)
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", msg.content)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }

            // Add the new user message
            val newContentObj = JSONObject()
            newContentObj.put("role", "user")
            val newPartsArray = JSONArray()
            val newPartObj = JSONObject()
            newPartObj.put("text", newMessage)
            newPartsArray.put(newPartObj)
            newContentObj.put("parts", newPartsArray)
            contentsArray.put(newContentObj)

            val rootJson = JSONObject()
            rootJson.put("contents", contentsArray)

            // System Instruction
            val systemObj = JSONObject()
            val sysParts = JSONArray()
            val sysPartObj = JSONObject()
            sysPartObj.put("text", customSystemPrompt)
            sysParts.put(sysPartObj)
            systemObj.put("parts", sysParts)
            rootJson.put("systemInstruction", systemObj)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", temperature.toDouble())

            // High thinking configuration when enabled or when using gemini-3.1-pro-preview
            if (enableHighThinking && selectedModel == GeminiConstants.MODEL_PRO) {
                val thinkingConfig = JSONObject()
                thinkingConfig.put("thinkingLevel", "HIGH")
                genConfig.put("thinkingConfig", thinkingConfig)
            }

            rootJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errorMessage = try {
                    val errorJson = JSONObject(responseBodyString)
                    val errorObj = errorJson.optJSONObject("error")
                    errorObj?.optString("message") ?: "کد خطا: ${response.code}"
                } catch (e: Exception) {
                    "خطای سرور: ${response.code} ${response.message}"
                }
                return@withContext Result.failure(Exception(errorMessage))
            }

            val jsonResponse = JSONObject(responseBodyString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("پاسخی از مدل جمینای دریافت نشد."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            var mainText = ""
            var thinkingText: String? = null

            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val p = parts.getJSONObject(i)
                    if (p.optBoolean("thought", false)) {
                        thinkingText = (thinkingText ?: "") + p.optString("text")
                    } else {
                        val text = p.optString("text")
                        if (text.isNotEmpty()) {
                            mainText += text
                        }
                    }
                }
            }

            if (mainText.isEmpty() && thinkingText != null) {
                mainText = thinkingText
                thinkingText = null
            }

            if (mainText.isEmpty()) {
                mainText = "پاسخ متنی تولید نشد."
            }

            Result.success(Pair(mainText, thinkingText))
        } catch (e: Exception) {
            Log.e("GeminiService", "Network call failed", e)
            Result.failure(e)
        }
    }
}
