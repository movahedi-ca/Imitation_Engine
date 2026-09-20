package com.example.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class SenderType {
  USER,
  PARTNER,
  SYSTEM
}

data class WebSocketReceiveMessage(
  val event: String = "message:receive",
  val payload: ReceivePayload
)

data class ReceivePayload(
  val message_id: String = UUID.randomUUID().toString(),
  val sender_type: String = "PARTNER",
  val content: String,
  val progressive_reveal_stage: Int,
  val timestamp: Long = System.currentTimeMillis() / 1000
)

@Immutable
data class ChatMessage(
  val messageId: String = UUID.randomUUID().toString(),
  val sessionId: String,
  val senderType: SenderType,
  val content: String,
  val progressiveRevealStage: Int,
  val timestamp: Long = System.currentTimeMillis()
)

@Immutable
data class ProgressiveStageInfo(
  val stage: Int,
  val title: String,
  val description: String,
  val blurRadiusDp: Float,
  val unlockedClue: String,
  val accentColor: Color
)

object ProgressiveStageDefinitions {
  fun getInfo(stage: Int): ProgressiveStageInfo {
    return when (stage.coerceIn(0, 4)) {
      0 -> ProgressiveStageInfo(
        stage = 0,
        title = "Stage 0: Full Obfuscation",
        description = "Zero-Trust double-blind visual cipher. Profile fully encrypted.",
        blurRadiusDp = 38f,
        unlockedClue = "Biometric liveness verified. Voice tone: Calibrated.",
        accentColor = Color(0xFF64748B)
      )
      1 -> ProgressiveStageInfo(
        stage = 1,
        title = "Stage 1: Soft Contour",
        description = "First milestone achieved. Silhouette & aesthetic essence unmasking.",
        blurRadiusDp = 26f,
        unlockedClue = "Communication style: Conceptual & Analytical",
        accentColor = Color(0xFF38BDF8)
      )
      2 -> ProgressiveStageInfo(
        stage = 2,
        title = "Stage 2: Structural Form",
        description = "Deep conversational cadence established. Geographic coordinates emerging.",
        blurRadiusDp = 15f,
        unlockedClue = "Region: Pacific North-West / West Coast",
        accentColor = Color(0xFF818CF8)
      )
      3 -> ProgressiveStageInfo(
        stage = 3,
        title = "Stage 3: High Definition",
        description = "Mutual rapport threshold reached. Shared core philosophy deciphered.",
        blurRadiusDp = 6f,
        unlockedClue = "Philosophy: 'Code is poetry, action is substance.'",
        accentColor = Color(0xFFA78BFA)
      )
      else -> ProgressiveStageInfo(
        stage = 4,
        title = "Stage 4: Zero Obfuscation",
        description = "Final barrier unlocked. Complete authentic identity revealed.",
        blurRadiusDp = 0f,
        unlockedClue = "Identity completely unmasked and verified.",
        accentColor = Color(0xFF34D399)
      )
    }
  }
}

@Immutable
data class PartnerEntity(
  val id: String,
  val isAi: Boolean,
  val codename: String,
  val realName: String,
  val title: String,
  val location: String,
  val philosophy: String,
  val avatarGradientStart: Long,
  val avatarGradientEnd: Long,
  val personalityVectors: UserVectors,
  val bio: String,
  val age: Int = 28,
  val distanceMiles: Int = 12,
  val gender: String = "NON_BINARY",
  val relationshipGoal: String = "INTELLECTUAL_RAPPORT",
  val interests: List<String> = listOf("Philosophy", "Cybernetics", "Vinyl Records", "Urban Trekking"),
  val heightCm: Int = 175,
  val drinkingHabit: String = "SOCIAL",
  val smokingHabit: String = "NO_SMOKING",
  val education: String = "ADVANCED"
)

@Immutable
data class UserVectors(
  val verbosityScore: Float = 0.65f,
  val humorIndex: Float = 0.58f,
  val empathyScore: Float = 0.72f,
  val responseLatencyAvgSec: Int = 18
)

@Immutable
data class SessionResult(
  val sessionId: String,
  val partner: PartnerEntity,
  val userGuessIsHuman: Boolean?,
  val initialRevealStage: Int,
  val finalRevealStage: Int,
  val messageCount: Int,
  val calibratedUserVectors: UserVectors,
  val compatibilityPercent: Int,
  val rawContractPayloads: List<String>
)
