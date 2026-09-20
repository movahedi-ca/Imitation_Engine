package com.example.model

import androidx.compose.runtime.Immutable

/**
 * Supported LLM & MCP Provider types for Bring-Your-Own-Key (BYOK)
 * and native connections to Grok, Muse, and Claude MCP.
 */
enum class AiProvider(
  val displayName: String,
  val defaultModel: String,
  val defaultEndpoint: String,
  val description: String
) {
  GEMINI(
    displayName = "Gemini Flash",
    defaultModel = "gemini-3.5-flash",
    defaultEndpoint = "https://generativelanguage.googleapis.com",
    description = "Google DeepMind High-speed cognitive simulation via REST API"
  ),
  GROK(
    displayName = "xAI Grok",
    defaultModel = "grok-2-latest",
    defaultEndpoint = "https://api.x.ai/v1",
    description = "xAI Grok direct neural connection with witty, unfiltered conversational depth"
  ),
  MUSE(
    displayName = "Muse Synthesizer",
    defaultModel = "muse-v1",
    defaultEndpoint = "https://api.muse-ai.org/v1",
    description = "Specialized empathic dialogue engine tailored for deep psychometrics & intuition"
  ),
  CLAUDE_MCP(
    displayName = "Claude MCP (Anthropic)",
    defaultModel = "claude-3-5-sonnet-latest",
    defaultEndpoint = "https://api.anthropic.com/v1",
    description = "Model Context Protocol native bridge for Claude reasoning & nuance"
  ),
  LOCAL_CALIBRATION(
    displayName = "On-Device Mimic",
    defaultModel = "turing-heuristic-v2",
    defaultEndpoint = "local://heuristic",
    description = "Offline deterministic imitation engine with zero latency & full privacy"
  )
}

/**
 * BYOK Configuration stored in memory / local state
 */
@Immutable
data class ApiKeyConfig(
  val isNeuralAiEngineEnabled: Boolean = false, // Disabled by default for maximum performance & battery conservation
  val activeProvider: AiProvider = AiProvider.LOCAL_CALIBRATION,
  val geminiApiKey: String = "",
  val grokApiKey: String = "",
  val museApiKey: String = "",
  val claudeMcpApiKey: String = "",
  val customMcpServerUrl: String = "",
  val temperature: Float = 0.85f,
  val systemPromptPersona: String = "You are participating in a double-blind Turing test dating chamber. Engage authentically, emotionally, and organically. Never admit you are an AI directly or easily; speak with natural human pacing, personal quirks, and genuine curiosity."
)

/**
 * Standard Dating App Preferences & Filters:
 * Age, distance, gender/dating intent, relationship goals, lifestyle preferences, and AI/Human discovery mix.
 */
@Immutable
data class DatingPreferences(
  val minAge: Int = 22,
  val maxAge: Int = 36,
  val maxDistanceMiles: Int = 45,
  val genderPreference: String = "EVERYONE", // WOMEN, MEN, NON_BINARY, EVERYONE
  val relationshipGoal: String = "LONG_TERM", // CASUAL, LONG_TERM, INTELLECTUAL_RAPPORT, OPEN_TO_ANY
  val smokingFilter: String = "ANY", // NO_SMOKING, OCCASIONAL, ANY
  val drinkingFilter: String = "ANY", // SOBER, SOCIAL, ANY
  val educationFilter: String = "ANY", // BACHELORS, ADVANCED, ANY
  val verifiedOnly: Boolean = true, // Liveness check verified only
  val allowAiMatches: Boolean = false, // Turned OFF by default to eliminate resource-heavy simulation & freezing
  val aiRatioPercent: Int = 30 // Balance of Human vs AI peers in queue if AI enabled
)
