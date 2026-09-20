package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
  @PrimaryKey
  val sessionId: String,
  val partnerCodename: String,
  val isAiSession: Boolean,
  val sessionStatus: String, // "ACTIVE", "CONCLUDED", "TERMINATED"
  val progressiveRevealStage: Int,
  val messageCount: Int,
  val startedAt: Long,
  val endedAt: Long? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey
  val messageId: String,
  val sessionId: String,
  val senderType: String, // "USER", "PARTNER", "SYSTEM"
  val content: String,
  val progressiveRevealStage: Int,
  val timestamp: Long
)

@Entity(tableName = "user_vectors")
data class UserVectorEntity(
  @PrimaryKey
  val userId: String = "primary_user",
  val verbosityScore: Float = 0.65f,
  val humorIndex: Float = 0.55f,
  val empathyScore: Float = 0.70f,
  val responseLatencyAvgSec: Int = 18,
  val updatedAt: Long = System.currentTimeMillis()
)
