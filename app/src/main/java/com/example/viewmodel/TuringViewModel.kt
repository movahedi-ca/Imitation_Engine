package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.TuringDatabase
import com.example.data.local.UserVectorEntity
import com.example.data.repository.TuringRepository
import com.example.engine.ImitationEngine
import com.example.model.AiProvider
import com.example.model.ApiKeyConfig
import com.example.model.ChatMessage
import com.example.model.DatingPreferences
import com.example.model.PartnerEntity
import com.example.model.SenderType
import com.example.model.SessionResult
import com.example.model.UserVectors
import com.example.network.MultiProviderAiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.model.UserProfile
import com.example.network.ConnectionStatus
import com.example.network.TuringWebSocketManager
import java.util.UUID
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class TuringScreen {
  REGISTRATION,
  TUTORIAL,
  SANCTUARY,
  CHAMBER,
  INSIGHT_DASHBOARD,
  RESEARCH_LAB,
  ACCOUNT_MANAGEMENT
}

enum class QueueState {
  IDLE,
  SCANNING,
  PAIRING,
  LOCKED_IN
}

@Immutable
data class ChamberUiState(
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
  val isSessionConcluded: Boolean = false,
  val momentIntuitionHuman: Float = 0.50f // 0.0f = AI, 1.0f = Human
)

@Immutable
data class SanctuaryUiState(
  val queueState: QueueState = QueueState.IDLE,
  val queueMode: String = "VECTOR_COMPATIBILITY",
  val biometricVerified: Boolean = true,
  val activePartnersOnline: Int = 1420,
  val queueProgress: Float = 0f
)

class TuringViewModel(application: Application) : AndroidViewModel(application) {

  private val database = TuringDatabase.getDatabase(application)
  private val repository = TuringRepository(database.turingDao())
  private val imitationEngine = ImitationEngine()
  private val multiProviderAiClient = MultiProviderAiClient()

  private val _currentScreen = MutableStateFlow(TuringScreen.SANCTUARY)
  val currentScreen: StateFlow<TuringScreen> = _currentScreen.asStateFlow()

  private val _sanctuaryState = MutableStateFlow(SanctuaryUiState())
  val sanctuaryState: StateFlow<SanctuaryUiState> = _sanctuaryState.asStateFlow()

  private val _chamberState = MutableStateFlow(ChamberUiState())
  val chamberState: StateFlow<ChamberUiState> = _chamberState.asStateFlow()

  private val _userVectors = MutableStateFlow(UserVectors(0.68f, 0.62f, 0.74f, 15))
  val userVectors: StateFlow<UserVectors> = _userVectors.asStateFlow()

  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _sessionResult = MutableStateFlow<SessionResult?>(null)
  val sessionResult: StateFlow<SessionResult?> = _sessionResult.asStateFlow()

  private val _lowPowerMode = MutableStateFlow(true) // Optimized Eco mode enabled by default to prevent device freeze & lag
  val lowPowerMode: StateFlow<Boolean> = _lowPowerMode.asStateFlow()

  private val _datingPreferences = MutableStateFlow(DatingPreferences())
  val datingPreferences: StateFlow<DatingPreferences> = _datingPreferences.asStateFlow()

  private val _apiKeyConfig = MutableStateFlow(ApiKeyConfig())
  val apiKeyConfig: StateFlow<ApiKeyConfig> = _apiKeyConfig.asStateFlow()

  val sessionHistory = repository.allSessions

  fun getMessagesForSession(sessionId: String) = repository.getMessagesForSession(sessionId)

  private var partnerReplyJob: Job? = null
  private var queueJob: Job? = null
  private var lastPartnerMessageTimestamp: Long = System.currentTimeMillis()
  private val userLatencyDeltas = mutableListOf<Long>()
  private val rawContractHistory = mutableListOf<String>()

  init {
    // Load persisted user vectors or initialize default
    viewModelScope.launch {
      val saved = repository.userVectors.first()
      if (saved != null) {
        _userVectors.value = UserVectors(
          verbosityScore = saved.verbosityScore,
          humorIndex = saved.humorIndex,
          empathyScore = saved.empathyScore,
          responseLatencyAvgSec = saved.responseLatencyAvgSec
        )
      } else {
        repository.updateUserVectors(
          UserVectorEntity(
            verbosityScore = 0.68f,
            humorIndex = 0.62f,
            empathyScore = 0.74f,
            responseLatencyAvgSec = 15
          )
        )
      }
    }
  }

  fun setQueueMode(mode: String) {
    _sanctuaryState.update { it.copy(queueMode = mode) }
  }

  /**
   * Task 4.2: Matchmaking Radar Queue flow
   */
  fun startMatchmaking(forceAi: Boolean? = null) {
    if (_sanctuaryState.value.queueState != QueueState.IDLE) return

    queueJob?.cancel()
    queueJob = viewModelScope.launch {
      _sanctuaryState.update { it.copy(queueState = QueueState.SCANNING, queueProgress = 0.1f) }
      delay(900)
      _sanctuaryState.update { it.copy(queueProgress = 0.45f) }
      delay(1100)
      _sanctuaryState.update { it.copy(queueState = QueueState.PAIRING, queueProgress = 0.85f) }
      delay(900)
      _sanctuaryState.update { it.copy(queueState = QueueState.LOCKED_IN, queueProgress = 1.0f) }
      delay(600)

      initiateChamberSession(forceAi)
    }
  }

  fun cancelMatchmaking() {
    queueJob?.cancel()
    _sanctuaryState.update { it.copy(queueState = QueueState.IDLE, queueProgress = 0f) }
  }

  fun enterChamberDirectly(forceAi: Boolean? = null) {
    initiateChamberSession(forceAi)
  }

  /**
   * Task 4.3: Develop the "Chamber" (Dual-bubble chat UI)
   */
  private fun initiateChamberSession(forceAi: Boolean? = null) {
    val partner = imitationEngine.selectMatch(
      forceAi = forceAi,
      preferences = _datingPreferences.value
    )
    val sessionId = UUID.randomUUID().toString()
    userLatencyDeltas.clear()
    rawContractHistory.clear()
    lastPartnerMessageTimestamp = System.currentTimeMillis()

    // Save session in Room
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

    _chamberState.value = ChamberUiState(
      sessionId = sessionId,
      partnerCodename = partner.codename,
      partner = partner,
      messages = emptyList(),
      progressiveRevealStage = 0,
      isPartnerTyping = false,
      typingStatusText = "",
      lastWebSocketJson = "",
      sessionStartedAt = System.currentTimeMillis(),
      momentIntuitionHuman = 0.50f
    )

    _sanctuaryState.update { it.copy(queueState = QueueState.IDLE, queueProgress = 0f) }
    _currentScreen.value = TuringScreen.CHAMBER

    // Partner sends opening greeting with artificial friction
    schedulePartnerReply(initialGreeting = true)
  }

  /**
   * User sends message in the Chamber
   */
  fun sendUserMessage(content: String) {
    val trimmed = content.trim()
    if (trimmed.isEmpty()) return

    val currentChamber = _chamberState.value
    val sessionId = currentChamber.sessionId
    val now = System.currentTimeMillis()

    // Calculate response latency
    val latencyMs = now - lastPartnerMessageTimestamp
    userLatencyDeltas.add((latencyMs / 1000).coerceIn(1, 120))

    val userMsg = ChatMessage(
      messageId = UUID.randomUUID().toString(),
      sessionId = sessionId,
      senderType = SenderType.USER,
      content = trimmed,
      progressiveRevealStage = currentChamber.progressiveRevealStage,
      timestamp = now
    )

    val updatedMessages = currentChamber.messages + userMsg
    val newUserCount = currentChamber.userMessageCount + 1

    _chamberState.update {
      it.copy(
        messages = updatedMessages,
        userMessageCount = newUserCount
      )
    }

    // Persist user message in Room on background IO thread
    viewModelScope.launch(Dispatchers.IO) {
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

    // Schedule partner reply with artificial friction & state machine progression
    schedulePartnerReply(initialGreeting = false)
  }

  /**
   * Simulates the Imitation Engine Artificial Friction & WebSocket Contract Dispatch
   */
  private fun schedulePartnerReply(initialGreeting: Boolean) {
    val currentChamber = _chamberState.value
    val partner = currentChamber.partner ?: return
    val sessionId = currentChamber.sessionId

    partnerReplyJob?.cancel()
    partnerReplyJob = viewModelScope.launch {
      val exchangeCount = currentChamber.messages.size

      // Context response content
      val lastUserMsg = currentChamber.messages.lastOrNull { it.senderType == SenderType.USER }?.content ?: ""
      
      // Determine response content:
      // If the partner is an AI and neural AI engine is explicitly enabled with a live provider,
      // generate using the real neural client; otherwise use the local deterministic imitation engine.
      var replyContent = ""
      if (partner.isAi && _apiKeyConfig.value.isNeuralAiEngineEnabled && _apiKeyConfig.value.activeProvider != AiProvider.LOCAL_CALIBRATION) {
        val history = currentChamber.messages.map { msg ->
          (if (msg.senderType == SenderType.USER) "USER" else "MODEL") to msg.content
        }
        val apiResult = multiProviderAiClient.generatePartnerResponse(
          config = _apiKeyConfig.value,
          partner = partner,
          userMessage = lastUserMsg.ifBlank { "Hello from the double-blind chamber." },
          conversationHistory = history
        )
        if (apiResult.isSuccess) {
          replyContent = apiResult.getOrThrow()
        }
      }

      // Fast, zero-lag on-device imitation engine fallback
      if (replyContent.isBlank()) {
        replyContent = imitationEngine.generatePartnerResponse(
          partner = partner,
          messageExchangeCount = exchangeCount,
          lastUserMessage = lastUserMsg,
          currentRevealStage = currentChamber.progressiveRevealStage
        )
      }

      // Calculate Artificial Friction (token count + human hesitation delay)
      val calculatedDelay = imitationEngine.calculateArtificialFrictionMs(replyContent)
      val delayMs = if (_lowPowerMode.value) calculatedDelay.coerceIn(500L, 1400L) else calculatedDelay

      // Artificial Friction status typing
      _chamberState.update {
        it.copy(
          isPartnerTyping = true,
          typingStatusText = if (initialGreeting) "Partner establishing connection..." else "Partner is formulating thought..."
        )
      }

      delay(delayMs)

      // State machine calculates next reveal stage based on message milestones
      val nextStage = imitationEngine.calculateProgressiveStage(exchangeCount + 1)

      // Format payload according to contract:
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
      lastPartnerMessageTimestamp = System.currentTimeMillis()

      val partnerMsg = ChatMessage(
        messageId = wsMessage.payload.message_id,
        sessionId = sessionId,
        senderType = SenderType.PARTNER,
        content = replyContent,
        progressiveRevealStage = nextStage,
        timestamp = System.currentTimeMillis()
      )

      _chamberState.update {
        it.copy(
          messages = it.messages + partnerMsg,
          progressiveRevealStage = nextStage,
          isPartnerTyping = false,
          typingStatusText = "",
          lastWebSocketJson = formattedJson
        )
      }

      // Persist to Room on background IO thread
      withContext(Dispatchers.IO) {
        repository.saveMessage(
          ChatMessageEntity(
            messageId = partnerMsg.messageId,
            sessionId = sessionId,
            senderType = "PARTNER",
            content = replyContent,
            progressiveRevealStage = nextStage,
            timestamp = partnerMsg.timestamp
          )
        )

        repository.updateSession(
          sessionId = sessionId,
          stage = nextStage,
          msgCount = _chamberState.value.messages.size,
          status = "ACTIVE",
          endedAt = null
        )
      }
    }
  }

  /**
   * Manually trigger a reveal milestone advance (for demonstration/testing)
   */
  fun forceAdvanceStage() {
    val current = _chamberState.value.progressiveRevealStage
    if (current < 4) {
      val next = current + 1
      _chamberState.update { it.copy(progressiveRevealStage = next) }
    }
  }

  /**
   * Task 4.5: Post-Session Insight Dashboard UI
   */
  fun concludeSession(userGuessIsHuman: Boolean?) {
    val chamber = _chamberState.value
    val partner = chamber.partner ?: return
    val sessionId = chamber.sessionId
    val now = System.currentTimeMillis()

    val userMessages = chamber.messages.filter { it.senderType == SenderType.USER }.map { it.content }
    val avgLatency = if (userLatencyDeltas.isNotEmpty()) userLatencyDeltas.average().toInt() else 16

    val calibratedVectors = imitationEngine.calculateUserVectors(
      userMessages = userMessages,
      avgLatencySec = avgLatency,
      currentVectors = _userVectors.value
    )

    _userVectors.value = calibratedVectors

    // Update Room DB
    viewModelScope.launch {
      repository.updateSession(
        sessionId = sessionId,
        stage = chamber.progressiveRevealStage,
        msgCount = chamber.messages.size,
        status = "CONCLUDED",
        endedAt = now
      )
      repository.updateUserVectors(
        UserVectorEntity(
          verbosityScore = calibratedVectors.verbosityScore,
          humorIndex = calibratedVectors.humorIndex,
          empathyScore = calibratedVectors.empathyScore,
          responseLatencyAvgSec = calibratedVectors.responseLatencyAvgSec,
          updatedAt = now
        )
      )
    }

    // Calculate compatibility match %
    val vectorDelta = Math.abs(calibratedVectors.empathyScore - partner.personalityVectors.empathyScore) +
        Math.abs(calibratedVectors.verbosityScore - partner.personalityVectors.verbosityScore)
    val compatibility = ((1.0f - (vectorDelta / 2f)) * 100).toInt().coerceIn(68, 98)

    _sessionResult.value = SessionResult(
      sessionId = sessionId,
      partner = partner,
      userGuessIsHuman = userGuessIsHuman,
      initialRevealStage = 0,
      finalRevealStage = chamber.progressiveRevealStage,
      messageCount = chamber.messages.size,
      calibratedUserVectors = calibratedVectors,
      compatibilityPercent = compatibility,
      rawContractPayloads = rawContractHistory.toList()
    )

    _currentScreen.value = TuringScreen.INSIGHT_DASHBOARD
  }

  fun returnToSanctuary() {
    partnerReplyJob?.cancel()
    _currentScreen.value = TuringScreen.SANCTUARY
    _sanctuaryState.update { it.copy(queueState = QueueState.IDLE) }
  }

  fun toggleLowPowerMode() {
    _lowPowerMode.update { !it }
  }

  fun setLowPowerMode(enabled: Boolean) {
    _lowPowerMode.value = enabled
  }

  fun openChamberAgain() {
    _currentScreen.value = TuringScreen.CHAMBER
  }

  fun navigateTo(screen: TuringScreen) {
    _currentScreen.value = screen
  }

  fun completeRegistration(handle: String, pseudonym: String) {
    _userProfile.update {
      it.copy(
        handle = if (handle.startsWith("@")) handle else "@$handle",
        pseudonym = pseudonym.ifEmpty { "Observer-01" },
        isBiometricVerified = true
      )
    }
    _currentScreen.value = TuringScreen.TUTORIAL
  }

  fun completeTutorial() {
    _currentScreen.value = TuringScreen.SANCTUARY
  }

  fun updateProfile(updated: UserProfile) {
    _userProfile.value = updated
  }

  fun resetUserVectors() {
    val recalibrated = UserVectors(0.70f, 0.65f, 0.75f, 14)
    _userVectors.value = recalibrated
    viewModelScope.launch {
      repository.updateUserVectors(
        UserVectorEntity(
          verbosityScore = recalibrated.verbosityScore,
          humorIndex = recalibrated.humorIndex,
          empathyScore = recalibrated.empathyScore,
          responseLatencyAvgSec = recalibrated.responseLatencyAvgSec
        )
      )
    }
  }

  fun updateApiKeyConfig(config: ApiKeyConfig) {
    _apiKeyConfig.value = config
  }

  fun updateDatingPreferences(preferences: DatingPreferences) {
    _datingPreferences.value = preferences
  }

  fun updateMomentIntuition(value: Float) {
    _chamberState.update { it.copy(momentIntuitionHuman = value.coerceIn(0f, 1f)) }
  }

  fun logout() {
    _currentScreen.value = TuringScreen.REGISTRATION
  }
}
