package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.model.AiProvider
import com.example.model.ApiKeyConfig
import com.example.model.PartnerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Universal Multi-Provider AI & MCP Network Client:
 * Supports Bring-Your-Own-Key (BYOK) with:
 *  1. Google Gemini (REST generateContent) via BuildConfig or user custom key
 *  2. xAI Grok (REST OpenAI-compatible /v1/chat/completions)
 *  3. Muse Synthesizer (empathy / psychometric chat API)
 *  4. Claude MCP (Anthropic /v1/messages or custom MCP endpoint)
 */
class MultiProviderAiClient(
  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(6, TimeUnit.SECONDS)
    .readTimeout(8, TimeUnit.SECONDS)
    .writeTimeout(6, TimeUnit.SECONDS)
    .retryOnConnectionFailure(false)
    .build()
) {

  companion object {
    private const val TAG = "MultiProviderAi"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
  }

  suspend fun generatePartnerResponse(
    config: ApiKeyConfig,
    partner: PartnerEntity,
    userMessage: String,
    conversationHistory: List<Pair<String, String>> // List of (role, text)
  ): Result<String> = withContext(Dispatchers.IO) {
    try {
      val result = withTimeoutOrNull(8000L) {
        when (config.activeProvider) {
          AiProvider.GEMINI -> callGemini(config, partner, userMessage, conversationHistory)
          AiProvider.GROK -> callGrok(config, partner, userMessage, conversationHistory)
          AiProvider.MUSE -> callMuse(config, partner, userMessage, conversationHistory)
          AiProvider.CLAUDE_MCP -> callClaudeMcp(config, partner, userMessage, conversationHistory)
          AiProvider.LOCAL_CALIBRATION -> Result.failure(IllegalStateException("Local provider selected"))
        }
      }
      result ?: Result.failure(Exception("AI Provider request timed out; falling back to on-device engine."))
    } catch (e: Exception) {
      Log.e(TAG, "Error in provider ${config.activeProvider}: ${e.message}", e)
      Result.failure(e)
    }
  }

  /**
   * 1. Google Gemini 3.5 Flash REST API Integration
   */
  private fun callGemini(
    config: ApiKeyConfig,
    partner: PartnerEntity,
    userMessage: String,
    conversationHistory: List<Pair<String, String>>
  ): Result<String> {
    val effectiveKey = config.geminiApiKey.ifBlank {
      try {
        BuildConfig.GEMINI_API_KEY
      } catch (e: Exception) {
        ""
      }
    }

    if (effectiveKey.isBlank() || effectiveKey == "MY_GEMINI_API_KEY") {
      return Result.failure(IllegalStateException("Gemini API key is not configured."))
    }

    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$effectiveKey"

    val systemPrompt = buildSystemPrompt(config, partner)

    val contentsArray = JSONArray()
    for ((role, text) in conversationHistory.takeLast(8)) {
      val partObj = JSONObject().put("text", text)
      val partsArr = JSONArray().put(partObj)
      val contentObj = JSONObject()
        .put("role", if (role == "USER") "user" else "model")
        .put("parts", partsArr)
      contentsArray.put(contentObj)
    }

    // Add current user prompt
    contentsArray.put(
      JSONObject()
        .put("role", "user")
        .put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
    )

    val requestPayload = JSONObject().apply {
      put("contents", contentsArray)
      put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
      put(
        "generationConfig",
        JSONObject()
          .put("temperature", config.temperature)
          .put("maxOutputTokens", 220)
      )
    }

    val req = Request.Builder()
      .url(url)
      .post(requestPayload.toString().toRequestBody(JSON_MEDIA_TYPE))
      .build()

    val response = okHttpClient.newCall(req).execute()
    val respBody = response.body?.string() ?: ""

    if (!response.isSuccessful) {
      return Result.failure(Exception("Gemini HTTP ${response.code}: $respBody"))
    }

    val json = JSONObject(respBody)
    val candidateText = json.optJSONArray("candidates")
      ?.optJSONObject(0)
      ?.optJSONObject("content")
      ?.optJSONArray("parts")
      ?.optJSONObject(0)
      ?.optString("text")

    return if (!candidateText.isNullOrBlank()) {
      Result.success(candidateText.trim())
    } else {
      Result.failure(Exception("Empty response from Gemini"))
    }
  }

  /**
   * 2. xAI Grok Native REST (OpenAI-compatible /v1/chat/completions)
   */
  private fun callGrok(
    config: ApiKeyConfig,
    partner: PartnerEntity,
    userMessage: String,
    conversationHistory: List<Pair<String, String>>
  ): Result<String> {
    val key = config.grokApiKey.trim()
    if (key.isBlank()) {
      return Result.failure(IllegalStateException("Grok API key is empty."))
    }

    val url = "https://api.x.ai/v1/chat/completions"
    val messagesArray = JSONArray()

    // System instruction
    messagesArray.put(
      JSONObject()
        .put("role", "system")
        .put("content", buildSystemPrompt(config, partner))
    )

    for ((role, text) in conversationHistory.takeLast(8)) {
      messagesArray.put(
        JSONObject()
          .put("role", if (role == "USER") "user" else "assistant")
          .put("content", text)
      )
    }

    messagesArray.put(
      JSONObject()
        .put("role", "user")
        .put("content", userMessage)
    )

    val payload = JSONObject().apply {
      put("model", "grok-2-latest")
      put("messages", messagesArray)
      put("temperature", config.temperature)
      put("max_tokens", 220)
    }

    val req = Request.Builder()
      .url(url)
      .addHeader("Authorization", "Bearer $key")
      .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
      .build()

    val response = okHttpClient.newCall(req).execute()
    val respBody = response.body?.string() ?: ""

    if (!response.isSuccessful) {
      return Result.failure(Exception("Grok HTTP ${response.code}: $respBody"))
    }

    val json = JSONObject(respBody)
    val reply = json.optJSONArray("choices")
      ?.optJSONObject(0)
      ?.optJSONObject("message")
      ?.optString("content")

    return if (!reply.isNullOrBlank()) {
      Result.success(reply.trim())
    } else {
      Result.failure(Exception("Empty response from Grok"))
    }
  }

  /**
   * 3. Muse Synthesizer (Empathic Dialogue API)
   */
  private fun callMuse(
    config: ApiKeyConfig,
    partner: PartnerEntity,
    userMessage: String,
    conversationHistory: List<Pair<String, String>>
  ): Result<String> {
    val key = config.museApiKey.trim()
    if (key.isBlank()) {
      return Result.failure(IllegalStateException("Muse API key is empty."))
    }

    val url = "https://api.muse-ai.org/v1/dialogue/synthesize"
    val historyArray = JSONArray()
    for ((role, text) in conversationHistory.takeLast(6)) {
      historyArray.put(JSONObject().put("speaker", role).put("message", text))
    }

    val payload = JSONObject().apply {
      put("model", "muse-v1")
      put("prompt", userMessage)
      put("history", historyArray)
      put("archetype", partner.title)
      put("empathy_vector", partner.personalityVectors.empathyScore)
      put("verbosity_vector", partner.personalityVectors.verbosityScore)
      put("system_prompt", buildSystemPrompt(config, partner))
    }

    val req = Request.Builder()
      .url(url)
      .addHeader("Authorization", "Bearer $key")
      .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
      .build()

    val response = okHttpClient.newCall(req).execute()
    val respBody = response.body?.string() ?: ""

    if (!response.isSuccessful) {
      return Result.failure(Exception("Muse HTTP ${response.code}: $respBody"))
    }

    val json = JSONObject(respBody)
    val text = json.optString("synthesized_text", json.optString("response", ""))
    return if (text.isNotBlank()) Result.success(text.trim()) else Result.failure(Exception("Empty Muse output"))
  }

  /**
   * 4. Claude MCP (Anthropic / Custom Model Context Protocol Endpoint)
   */
  private fun callClaudeMcp(
    config: ApiKeyConfig,
    partner: PartnerEntity,
    userMessage: String,
    conversationHistory: List<Pair<String, String>>
  ): Result<String> {
    val key = config.claudeMcpApiKey.trim()
    val customUrl = config.customMcpServerUrl.trim()

    val endpoint = if (customUrl.isNotBlank()) {
      if (customUrl.endsWith("/messages")) customUrl else "$customUrl/v1/messages"
    } else {
      "https://api.anthropic.com/v1/messages"
    }

    if (key.isBlank() && customUrl.isBlank()) {
      return Result.failure(IllegalStateException("Claude MCP key or custom server URL is required."))
    }

    val messagesArray = JSONArray()
    for ((role, text) in conversationHistory.takeLast(8)) {
      messagesArray.put(
        JSONObject()
          .put("role", if (role == "USER") "user" else "assistant")
          .put("content", text)
      )
    }
    messagesArray.put(
      JSONObject()
        .put("role", "user")
        .put("content", userMessage)
    )

    val payload = JSONObject().apply {
      put("model", "claude-3-5-sonnet-latest")
      put("max_tokens", 250)
      put("temperature", config.temperature)
      put("system", buildSystemPrompt(config, partner))
      put("messages", messagesArray)
    }

    val reqBuilder = Request.Builder()
      .url(endpoint)
      .addHeader("anthropic-version", "2023-06-01")
      .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))

    if (key.isNotBlank()) {
      reqBuilder.addHeader("x-api-key", key)
    }

    val response = okHttpClient.newCall(reqBuilder.build()).execute()
    val respBody = response.body?.string() ?: ""

    if (!response.isSuccessful) {
      return Result.failure(Exception("Claude MCP HTTP ${response.code}: $respBody"))
    }

    val json = JSONObject(respBody)
    val text = json.optJSONArray("content")
      ?.optJSONObject(0)
      ?.optString("text")

    return if (!text.isNullOrBlank()) {
      Result.success(text.trim())
    } else {
      Result.failure(Exception("Empty response from Claude MCP"))
    }
  }

  private fun buildSystemPrompt(config: ApiKeyConfig, partner: PartnerEntity): String {
    return """
      ${config.systemPromptPersona}
      
      Partner Persona:
      - Codename: ${partner.codename}
      - Real Identity (concealed unless probed naturally): ${partner.realName}, ${partner.age} years old
      - Location: ${partner.location} (${partner.distanceMiles} miles away)
      - Career/Vocation: ${partner.title}
      - Core Philosophy: "${partner.philosophy}"
      - Bio: ${partner.bio}
      - Interests: ${partner.interests.joinToString(", ")}
      
      CRITICAL TURING DIRECTIVE:
      You are speaking in a zero-trust double-blind dating chamber. The other person is constantly evaluating whether you are a genuine human or an AI.
      - Keep responses concise (1 to 3 sentences maximum, casual text-message rhythm).
      - Do NOT write boilerplate AI essays, bullet points, or customer service apologies.
      - Express human hesitations, spontaneous humor, curiosity, and authentic dating vibes.
      - Never break character or declare "As an AI model...".
    """.trimIndent()
  }
}
