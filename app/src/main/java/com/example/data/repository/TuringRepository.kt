package com.example.data.repository

import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.TuringDao
import com.example.data.local.UserVectorEntity
import kotlinx.coroutines.flow.Flow

class TuringRepository(private val dao: TuringDao) {
  val allSessions: Flow<List<ChatSessionEntity>> = dao.getAllSessions()
  val userVectors: Flow<UserVectorEntity?> = dao.getUserVectors()

  fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>> =
    dao.getMessagesForSession(sessionId)

  suspend fun saveSession(session: ChatSessionEntity) = dao.insertSession(session)

  suspend fun updateSession(sessionId: String, stage: Int, msgCount: Int, status: String, endedAt: Long?) =
    dao.updateSessionStatus(sessionId, stage, msgCount, status, endedAt)

  suspend fun saveMessage(message: ChatMessageEntity) = dao.insertMessage(message)

  suspend fun updateUserVectors(vectors: UserVectorEntity) = dao.insertOrUpdateUserVectors(vectors)
}
