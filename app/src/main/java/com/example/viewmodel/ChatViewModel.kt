package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.TuringDatabase
import com.example.data.repository.TuringRepository
import com.example.engine.ImitationEngine
import com.example.model.ChatMessage
import com.example.model.PartnerEntity
import com.example.model.SenderType
import com.example.model.SessionResult
import com.example.model.UserVectors
import com.example.network.ConnectionStatus
import com.example.network.TuringWebSocketManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Task requirement:
 * Create a ChatViewModel to manage the conversation state, including tracking
 * progressive_reveal_stage and message history in an observable StateFlow.
 */
data class ChatUiState(
  val sessionId: String = "",
  val partnerCodename: String = "PARTNER-??",
  val partner: PartnerEntity? = null,
  val messages: List<ChatMessage> = emptyList(),
  val progressiveRevealStage: Int = 0,
  val isPartnerTyping: Boolean = false,
  val typingStatusText: String = "",
  val lastWebSocketJson: String = "",
  val sessionStartedAt: Long = System.currentTimeMillis(),
  val userMessageCount: Int = 0,
  val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
  val isSessionConcluded: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

  private val database = TuringDatabase.getDatabase(application)
  private val repository = TuringRepository(database.turingDao())
  private val imitationEngine = ImitationEngine()
  private val webSocketManager = TuringWebSocketManager()

  private val _chatUiState = MutableStateFlow(ChatUiState())
  val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

  private val _sessionResult = MutableStateFlow<SessionResult?>(null)
  val sessionResult: StateFlow<SessionResult?> = _sessionResult.asStateFlow()

  private var partnerReplyJob: Job? = null
  private var lastPartnerMessageTimestamp: Long = System.currentTimeMillis()
  private val userLatencyDeltas = mutableListOf<Long>()
  private val rawContractHistory = mutableListOf<String>()

  init {
    // Observe incoming WebSocket messages
    viewModelScope.launch {
      webSocketManager.incomingMessages.collect { incoming ->
        handleIncomingWebSocketMessage(incoming)
      }
    }

    viewModelScope.launch {
      webSocketManager.connectionStatus.collect { status ->
        _chatUiState.update { it.copy(connectionStatus = status) }
      }
    }

    viewModelScope.launch {
      webSocketManager.rawPayloads.collect { rawJson ->
        _chatUiState.update { it.copy(lastWebSocketJson = rawJson) }
      }
    }
  }

  /**
   * Initializes a new session in The Chamber
   */
  fun startSession(partner: PartnerEntity) {
    val sessionId = UUID.randomUUID().toString()
    userLatencyDeltas.clear()
    rawContractHistory.clear()
    lastPartnerMessageTimestamp = System.currentTimeMillis()

    viewModelScope.launch {
      repository.saveSession(
        ChatSessionEntity(
          sessionId = sessionId,
          partnerCodename = partner.codename,
          isAiSession = partner.isAi,
          sessionStatus = "ACTIVE",
          progressiveRevealStage = 0,
          messageCount = 0,
          startedAt = System.currentTimeMillis()
        )
      )
    }

    _chatUiState.value = ChatUiState(
      sessionId = sessionId,
      partnerCodename = partner.codename,
      partner = partner,
      messages = emptyList(),
      progressiveRevealStage = 0,
      isPartnerTyping = false,
      typingStatusText = "",
      lastWebSocketJson = "",
      sessionStartedAt = System.currentTimeMillis(),
      userMessageCount = 0,
      connectionStatus = ConnectionStatus.CONNECTED
    )

    // Connect WebSocket
    webSocketManager.connect(sessionId)

    // Initial greeting from partner with artificial friction
    schedulePartnerReply(initialGreeting = true)
  }

  /**
   * Sends user message and broadcasts over WebSocket & Room DB
   */
  fun sendMessage(content: String) {
    val trimmed = content.trim()
    if (trimmed.isEmpty()) return

    val current = _chatUiState.value
    val sessionId = current.sessionId
    val now = System.currentTimeMillis()

    // Calculate response latency
    val latencyMs = now - lastPartnerMessageTimestamp
    userLatencyDeltas.add((latencyMs / 1000).coerceIn(1, 120))

    val userMsg = ChatMessage(
      messageId = UUID.randomUUID().toString(),
      sessionId = sessionId,
      senderType = SenderType.USER,
      content = trimmed,
      progressiveRevealStage = current.progressiveRevealStage,
      timestamp = now
    )

    val updatedMessages = current.messages + userMsg
    val newUserCount = current.userMessageCount + 1

    _chatUiState.update {
      it.copy(
        messages = updatedMessages,
        userMessageCount = newUserCount
      )
    }

    // Attempt transmission via WebSocket
    webSocketManager.sendMessage(trimmed)

    // Persist to Room
    viewModelScope.launch {
      repository.saveMessage(
        ChatMessageEntity(
          messageId = userMsg.messageId,
          sessionId = sessionId,
          senderType = "USER",
          content = trimmed,
          progressiveRevealStage = userMsg.progressiveRevealStage,
          timestamp = now
        )
      )
    }

    // Trigger partner reply simulation with artificial friction and state-machine progression
    schedulePartnerReply(initialGreeting = false)
  }

  private fun schedulePartnerReply(initialGreeting: Boolean) {
    val current = _chatUiState.value
    val partner = current.partner ?: return
    val sessionId = current.sessionId

    partnerReplyJob?.cancel()
    partnerReplyJob = viewModelScope.launch {
      val exchangeCount = current.messages.size
      val lastUserMsg = current.messages.lastOrNull { it.senderType == SenderType.USER }?.content ?: ""

      val replyContent = imitationEngine.generatePartnerResponse(
        partner = partner,
        messageExchangeCount = exchangeCount,
        lastUserMessage = lastUserMsg,
        currentRevealStage = current.progressiveRevealStage
      )

      // Calculate Artificial Friction (token count + human hesitation delay)
      val delayMs = imitationEngine.calculateArtificialFrictionMs(replyContent)

      _chatUiState.update {
        it.copy(
          isPartnerTyping = true,
          typingStatusText = if (initialGreeting) "Establishing cryptographic handshake..." else "Partner is formulating thought..."
        )
      }

      delay(delayMs)

      // State machine calculates next reveal stage based on message milestone
      val nextStage = imitationEngine.calculateProgressiveStage(exchangeCount + 1)
      val wsMessage = imitationEngine.createReceivePayload(replyContent, nextStage)

      val formattedJson = """
{
  "event": "${wsMessage.event}",
  "payload": {
    "message_id": "${wsMessage.payload.message_id}",
    "sender_type": "${wsMessage.payload.sender_type}",
    "content": "${wsMessage.payload.content}",
    "progressive_reveal_stage": ${wsMessage.payload.progressive_reveal_stage},
    "timestamp": ${wsMessage.payload.timestamp}
  }
}
      """.trimIndent()

      rawContractHistory.add(formattedJson)

      // Dispatch through WebSocket Manager pipeline
      webSocketManager.dispatchSimulatedIncoming(wsMessage, formattedJson)
    }
  }

  private fun handleIncomingWebSocketMessage(incoming: com.example.model.WebSocketReceiveMessage) {
    val current = _chatUiState.value
    val payload = incoming.payload
    val nextStage = payload.progressive_reveal_stage
    val now = System.currentTimeMillis()
    lastPartnerMessageTimestamp = now

    val partnerMsg = ChatMessage(
      messageId = payload.message_id,
      sessionId = current.sessionId,
      senderType = SenderType.PARTNER,
      content = payload.content,
      progressiveRevealStage = nextStage,
      timestamp = now
    )

    _chatUiState.update {
      it.copy(
        messages = it.messages + partnerMsg,
        progressiveRevealStage = nextStage,
        isPartnerTyping = false,
        typingStatusText = ""
      )
    }

    viewModelScope.launch {
      repository.saveMessage(
        ChatMessageEntity(
          messageId = partnerMsg.messageId,
          sessionId = current.sessionId,
          senderType = "PARTNER",
          content = partnerMsg.content,
          progressiveRevealStage = nextStage,
          timestamp = now
        )
      )

      repository.updateSession(
        sessionId = current.sessionId,
        stage = nextStage,
        msgCount = _chatUiState.value.messages.size,
        status = "ACTIVE",
        endedAt = null
      )
    }
  }

  fun forceAdvanceStage() {
    val current = _chatUiState.value.progressiveRevealStage
    if (current < 4) {
      _chatUiState.update { it.copy(progressiveRevealStage = current + 1) }
    }
  }

  fun endSession(): SessionResult? {
    val current = _chatUiState.value
    val partner = current.partner ?: return null
    val now = System.currentTimeMillis()

    webSocketManager.disconnect()

    viewModelScope.launch {
      repository.updateSession(
        sessionId = current.sessionId,
        stage = current.progressiveRevealStage,
        msgCount = current.messages.size,
        status = "CONCLUDED",
        endedAt = now
      )
    }

    val userMessages = current.messages.filter { it.senderType == SenderType.USER }.map { it.content }
    val avgLatency = if (userLatencyDeltas.isNotEmpty()) userLatencyDeltas.average().toInt() else 16

    val calibratedVectors = imitationEngine.calculateUserVectors(
      userMessages = userMessages,
      avgLatencySec = avgLatency,
      currentVectors = UserVectors()
    )

    val delta = Math.abs(calibratedVectors.empathyScore - partner.personalityVectors.empathyScore) +
        Math.abs(calibratedVectors.verbosityScore - partner.personalityVectors.verbosityScore)
    val compatibility = ((1.0f - (delta / 2f)) * 100).toInt().coerceIn(68, 98)

    val result = SessionResult(
      sessionId = current.sessionId,
      partner = partner,
      userGuessIsHuman = null,
      initialRevealStage = 0,
      finalRevealStage = current.progressiveRevealStage,
      messageCount = current.messages.size,
      calibratedUserVectors = calibratedVectors,
      compatibilityPercent = compatibility,
      rawContractPayloads = rawContractHistory.toList()
    )

    _sessionResult.value = result
    _chatUiState.update { it.copy(isSessionConcluded = true) }
    return result
  }

  override fun onCleared() {
    super.onCleared()
    webSocketManager.disconnect()
    partnerReplyJob?.cancel()
  }
}
