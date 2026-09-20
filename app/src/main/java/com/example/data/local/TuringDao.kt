package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TuringDao {
  @Query("SELECT * FROM chat_sessions ORDER BY startedAt DESC")
  fun getAllSessions(): Flow<List<ChatSessionEntity>>

  @Query("SELECT * FROM chat_sessions WHERE sessionId = :sessionId LIMIT 1")
  suspend fun getSessionById(sessionId: String): ChatSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: ChatSessionEntity)

  @Query("UPDATE chat_sessions SET progressiveRevealStage = :stage, messageCount = :msgCount, sessionStatus = :status, endedAt = :endedAt WHERE sessionId = :sessionId")
  suspend fun updateSessionStatus(sessionId: String, stage: Int, msgCount: Int, status: String, endedAt: Long?)

  @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
  fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity)

  @Query("SELECT * FROM user_vectors WHERE userId = :userId LIMIT 1")
  fun getUserVectors(userId: String = "primary_user"): Flow<UserVectorEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateUserVectors(vector: UserVectorEntity)
}
